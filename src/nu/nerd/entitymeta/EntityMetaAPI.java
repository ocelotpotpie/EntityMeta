package nu.nerd.entitymeta;

import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

// --------------------------------------------------------------------------
/**
 * This is the public API for accessing type-safe, persistent entity metadata.
 * <p>
 * 
 * In a nutshell:
 * 
 * <pre class="brush:java">
 * // Example: Store a UUID, Location and double in an entity.
 * Plugin myPlugin;
 * Entity entity;
 * Player owner;
 * 
 * EntityMeta.api().set(entity, myPlugin, "owner", owner.getUniqueId());
 * EntityMeta.api().set(entity, myPlugin, "location", owner.getLocation());
 * EntityMeta.api().set(entity, myPlugin, "chance", 1.2345);
 * 
 * // Example: Retrieve my metadata.
 * UUID ownerUuid = (UUID) EntityMeta.api().get(entity, myPlugin, "owner");
 * Location location = (Location) EntityMeta.api().get(entity, myPlugin, "location");
 * double chance = (Double) EntityMeta.api().get(entity, myPlugin, "chance");
 * 
 * // Example: Enumerate all metadata set by a specific plugin.
 * String pluginName = "SomePlugin";
 * for (Entry&lt;String, MetadataEntry&gt; entry : EntityMeta.api().getPluginEntries(entity, pluginName)) {
 *     String key = entry.getKey(); // e.g. "SomePlugin.owner"
 *     MetadataEntry meta = entry.getValue();
 *     String className = meta.getType().getValueClass().getSimpleName();
 *     getLogger().info(key + " (" + className + ") = " + meta.getValue() + " (stored as: " + meta.getTag() + ")");
 * }
 * </pre>
 * 
 * <h3>Types</h3> Metadata values can be of any boxed Java primitive type,
 * {@link java.lang.String}, {@link java.util.UUID} or any class in the Bukkit
 * API that implements {@link ConfigurationSerializable} (e.g. {@link Location},
 * {@link ItemStack}, etc.). Note, however, that
 * {@link ConfigurationSerializable} instances are serialised to YAML and are
 * therefore not as efficient as simpler types. Support for additional types can
 * be added using
 * {@link nu.nerd.entitymeta.MetadataTypeManager#addType(MetadataType)}.<br>
 * <br>
 * 
 * <h3>Null</h3> An unset metadata value is indistinguishable from one that has
 * been set to {@code null}. You can clear a metadata value by calling
 * {@link nu.nerd.entitymeta.EntityMetaAPI#clear(Entity, Plugin, String)}, or
 * setting it to {@code null}.<br>
 * <br>
 * 
 * <h3>Names and Keys</h3> Metadata names can only contain letters, digits,
 * underscores and hyphens. Plugin names are assumed to be that subset of valid
 * Java class names that consist only of letters, digits and underscores. The
 * full <i>key</i> of a metadata value (as exposed by
 * {@link EntityMetaAPI#getAllEntries(Entity)}) is the concatenation of a plugin
 * name, a dot ('.'), and the <code>name</code> argument passed to
 * {@linkplain EntityMetaAPI#set(Entity, Plugin, String, Object)}, e.g.
 * {@code "SomePlugin.entity-owner-uuid"}.<br>
 * <br>
 * 
 * <h3>Thread Safety</h3> This API is <i>not</i> thread safe. Metadata should
 * only be accessed from the Bukkit main thread.<br>
 * <br>
 * 
 * <h3>How It Works</h3> Metadata values are serialised into strings and stored
 * in the scoreboard tags of entities (see {@link Entity#getScoreboardTags()}).
 * Vanilla Minecraft code ensures that the scoreboard tags are persistent across
 * restarts.<br>
 * <br>
 * 
 * <h3>A Word of Caution On Object Ownership and Mutability</h3> For the sake of
 * efficiency, the metadata value passed to
 * {@link EntityMetaAPI#set(Entity, Plugin, String, Object)} is cached in a
 * {@link MetadataEntry} and the same metadata value object instance may be
 * returned from {@link EntityMetaAPI#get(Entity, Plugin, String)} and
 * {@link MetadataEntry#getValue()}. This is to avoid the overhead of
 * deserialising the tag value repeatedly (whenever it is retrieved).
 * <p>
 * 
 * You should treat the metadata value passed to
 * {@link EntityMetaAPI#set(Entity, Plugin, String, Object)} or returned from
 * the various getters as immutable internal state. Do <i>not</i> modify a
 * metadata value that has passed into or out of the API. That would make the
 * cached object value inconsistent with the scoreboard tag held by the
 * {@link org.bukkit.entity.Entity}. Modify a <i>clone</i> instead.
 */
public final class EntityMetaAPI {
    // ------------------------------------------------------------------------
    /**
     * A regular expression describing valid keys ({@code <plugin>.<name>}).
     */
    public static final Pattern KEY_PATTERN = Pattern.compile("^\\w+\\.(?:-|\\w)+$");

    // ------------------------------------------------------------------------
    /**
     * Set a metadata value on an {@link org.bukkit.entity.Entity}.
     * 
     * @param entity the Entity.
     * @param pluginName the name of the plugin that owns the value.
     * @param name the name of the value.
     * @param value the value.
     * 
     * @throws EntityMetadataException if the entity is null, the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters or
     *         the value is an unsupported type.
     */
    public void set(Entity entity, String pluginName, String name, Object value) throws EntityMetadataException {
        String key = key(pluginName, name);
        if (value == null) {
            _store.removeValue(entity, key);
        } else {
            _store.setValue(entity, key, value);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Set a metadata value on an {@link org.bukkit.entity.Entity}.
     * 
     * @param entity the Entity.
     * @param plugin the plugin that owns the value.
     * @param name the name of the value.
     * @param value the value.
     * 
     * @throws EntityMetadataException if the entity is null, the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters or
     *         the value is an unsupported type.
     */
    public void set(Entity entity, Plugin plugin, String name, Object value) throws EntityMetadataException {
        set(entity, plugin.getName(), name, value);
    }

    // ------------------------------------------------------------------------
    /**
     * Get a metadata value.
     * 
     * @param entity the Entity.
     * @param pluginName the name of the plugin that owns the value.
     * @param name the name of the value.
     * @return the value, or null if not set.
     * 
     * @throws EntityMetadataException if the entity is null or the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters.
     */
    public Object get(Entity entity, String pluginName, String name) throws EntityMetadataException {
        return _store.getValue(entity, key(pluginName, name));
    }

    // ------------------------------------------------------------------------
    /**
     * Get a metadata value.
     * 
     * @param entity the Entity.
     * @param plugin the plugin that owns the value.
     * @param name the name of the value.
     * @return the value, or null if not set.
     *
     * @throws EntityMetadataException if the entity is null or the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters.
     */
    public Object get(Entity entity, Plugin plugin, String name) throws EntityMetadataException {
        return get(entity, plugin.getName(), name);
    }

    // ------------------------------------------------------------------------
    /**
     * Clear a metadata value.
     * 
     * Note that removing a value can also be achieved by setting it to
     * {@code null}.
     * 
     * @param entity the Entity.
     * @param pluginName the name of the plugin that owns the value.
     * @param name the name of the value.
     * 
     * @throws EntityMetadataException if the entity is null or the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters.
     */
    public void clear(Entity entity, String pluginName, String name) throws EntityMetadataException {
        _store.removeValue(entity, key(pluginName, name));
    }

    // ------------------------------------------------------------------------
    /**
     * Clear a metadata value.
     * 
     * Note that removing a value can also be achieved by setting it to
     * {@code null}.
     * 
     * @param entity the Entity.
     * @param plugin the plugin that owns the value.
     * @param name the name of the value.
     * 
     * @throws EntityMetadataException if the entity is null or the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters.
     */
    public void clear(Entity entity, Plugin plugin, String name) throws EntityMetadataException {
        clear(entity, plugin.getName(), name);
    }

    // ------------------------------------------------------------------------
    /**
     * Return a {@link MetadataEntry} corresponding to a stored metadata value,
     * or null if not found.
     * 
     * @param entity the Entity.
     * @param pluginName the name of the plugin that owns the value.
     * @param name the name of the value.
     * @return a {@link MetadataEntry} corresponding to a stored metadata value,
     *         or null if not found.
     * 
     * @throws EntityMetadataException if the entity is null or the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters.
     */
    public MetadataEntry getEntry(Entity entity, String pluginName, String name) throws EntityMetadataException {
        return _store.getEntry(entity, key(pluginName, name));
    }

    // ------------------------------------------------------------------------
    /**
     * Return a {@link MetadataEntry} corresponding to a stored metadata value,
     * or null if not found.
     * 
     * @param entity the Entity.
     * @param plugin the plugin that owns the value.
     * @param name the name of the value.
     * @return a {@link MetadataEntry} corresponding to a stored metadata value,
     *         or null if not found.
     * 
     * @throws EntityMetadataException if the entity is null or the key
     *         ({@code <plugin>.<name>}) contains nulls or invalid characters.
     */
    public MetadataEntry getEntry(Entity entity, Plugin plugin, String name) throws EntityMetadataException {
        return getEntry(entity, plugin.getName(), name);
    }

    // ------------------------------------------------------------------------
    /**
     * Return all {@link MetadataEntry metadata entries} associated with an
     * {@link org.bukkit.entity.Entity}.
     * 
     * @param entity the Entity.
     * @return all {@link MetadataEntry metadata entries} associated with an
     *         {@link org.bukkit.entity.Entity}.
     * 
     * @throws EntityMetadataException if the entity is null.
     */
    public Map<String, MetadataEntry> getAllEntries(Entity entity) throws EntityMetadataException {
        return _store.getAllEntries(entity);
    }

    // ------------------------------------------------------------------------
    /**
     * Return all {@link MetadataEntry metadata entries} associated with an
     * {@link org.bukkit.entity.Entity} that belong to a specified plugin.
     * 
     * @param entity the Entity.
     * @param pluginName the name of the plugin.
     * @return all {@link MetadataEntry metadata entries} associated with an
     *         {@link org.bukkit.entity.Entity} that belong to a specified
     *         plugin.
     * 
     * @throws EntityMetadataException if the entity is null.
     */
    public Map<String, MetadataEntry> getPluginEntries(Entity entity, String pluginName) throws EntityMetadataException {
        return _store.getPluginEntries(entity, pluginName);
    }

    // ------------------------------------------------------------------------
    /**
     * Format the key of metadata.
     * 
     * @param pluginName the name of the plugin; must be only letters, digits
     *        and underscores.
     * @param name the name of the metadata; must be only letters, digits,
     *        underscores and hyphens.
     * @return a key of the form {@code <plugin>.<name>}.
     * 
     * @throws EntityMetadataException if the key would contain invalid
     *         characters.
     */
    public String key(String pluginName, String name) throws EntityMetadataException {
        if (pluginName == null) {
            throw new EntityMetadataException("null plugin name");
        }
        if (name == null) {
            throw new EntityMetadataException("null metadata name");
        }

        String key = pluginName + '.' + name;
        if (!KEY_PATTERN.matcher(key).matches()) {
            throw new EntityMetadataException("invalid characters in key");
        }
        return key;
    }

    // ------------------------------------------------------------------------
    /**
     * Implementation of metadata storage.
     */
    private static MetadataStore _store = new MetadataStore();
} // class EntityMetaAPI