package nu.nerd.entitymeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.entity.Entity;

// ----------------------------------------------------------------------------
/**
 * Holds loaded, cached metadata for one Entity.
 * 
 * Metadata is stored in the scoreboard tags of an entity, which are a set of
 * persistent strings attached to the entity as a vanilla Minecraft feature.
 * Updated tags are immediately stored in the Entity's state. The corresponding
 * unserialised object values (before serialisation as tags) are cached in a
 * map.
 * 
 * Each tag takes the form {@literal <plugin>.<name>:<type>:<value>}. The prefix
 * {@literal <plugin>.<name>} constitutes a unique key for the metadata value,
 * based on the name of the owning plugin and a name for the metadata dictated
 * by that plugin. The {@literal <type>} is a single character code identifying
 * the {@link MetadataType} used to encode and decode the value.
 */
class PerEntityMetadata {
    // ------------------------------------------------------------------------
    /**
     * Create a new instance by parsing scoreboard tags and caching their
     * deserialised values.
     * 
     * @param entity the entity whose metadata is accessed.
     */
    public PerEntityMetadata(Entity entity) {
        parseMetadata(entity);
    }

    // ------------------------------------------------------------------------
    /**
     * Store a new metadata value.
     * 
     * @param entity the entity whose metadata is accessed.
     * @param key the key, of the form plugin.name.
     * @param value the value to store.
     * @param type the type of the value.
     */
    public void setEntry(Entity entity, String key, Object value, MetadataType type) {
        String tag = key + ':' + type.getCode() + ':' + type.toString(value);
        entity.addScoreboardTag(tag);
        _entries.put(key, new MetadataEntry(value, type, tag));
    }

    // ------------------------------------------------------------------------
    /**
     * Remove the specified metadata value.
     * 
     * @param entity the entity whose metadata is accessed.
     * @param key the key, of the form plugin.name.
     */
    public void removeEntry(Entity entity, String key) {
        MetadataEntry entry = _entries.remove(key);
        if (entry != null) {
            entity.removeScoreboardTag(entry.getTag());
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Return the {@link MetadataEntry} corresponding to the specified key.
     * 
     * @param key the key, of the form plugin.name.
     * @return the {@link MetadataEntry} corresponding to the specified key.
     */
    public MetadataEntry getEntry(String key) {
        return _entries.get(key);
    }

    // ------------------------------------------------------------------------
    /**
     * Return all {@link MetadataEntry}s, in no particular order.
     * 
     * @return all {@link MetadataEntry}s, in no particular order.
     */
    public Map<String, MetadataEntry> getAllEntries() {
        return Collections.unmodifiableMap(_entries);
    }

    // ------------------------------------------------------------------------
    /**
     * Return all {@link MetadataEntry}s belonging to the specified plugin, in
     * no particular order.
     * 
     * @param pluginName the name of the plugin; must not be null.
     * @return all {@link MetadataEntry}s belonging to the specified plugin, in
     *         no particular order.
     */
    public Map<String, MetadataEntry> getPluginEntries(String pluginName) {
        String prefix = pluginName + '.';
        return _entries.entrySet().stream()
        .filter(e -> e.getKey().startsWith(prefix))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    // ------------------------------------------------------------------------
    /**
     * Parse and cache metadata from the scoreboard tags of the entity.
     * 
     * @param entity the entity.
     */
    private void parseMetadata(Entity entity) {
        for (String tag : entity.getScoreboardTags()) {
            Matcher matcher = TAG_PATTERN.matcher(tag);
            if (matcher.matches()) {
                String key = matcher.group(1);
                char code = matcher.group(2).charAt(0);
                String serialisedValue = matcher.group(3);

                MetadataType type = MetadataTypeManager.INSTANCE.getTypeByCode(code);
                if (type != null) {
                    try {
                        Object value = type.fromString(serialisedValue);
                        _entries.put(key, new MetadataEntry(value, type, tag));
                    } catch (IllegalArgumentException ex) {
                        EntityMeta.PLUGIN.getLogger().severe("Error loading " + entity.getType() + " " + entity.getUniqueId() + " metadata: " + tag);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    /**
     * The regular expression that all scoreboard tags holding metadata conform
     * to.
     * 
     * Note that the names of metadata values can only consist of hyphens and
     * Java identifier characters, i.e. (-|\w)+.
     */
    private static final Pattern TAG_PATTERN = Pattern.compile("^(\\w+.(?:-|\\w)+):(.):(.+)$", Pattern.CASE_INSENSITIVE);

    /**
     * A map from key (<plugin>.<name>) to {@link MetadataEntry}, which includes
     * a complete formatted scoreboard tag value.
     * 
     * This is used to facilitate fast removal of a metadata value by storing
     * it's current scoreboard tag representation in full.
     */
    private final HashMap<String, MetadataEntry> _entries = new HashMap<>();

} // class PerEntityMetadata