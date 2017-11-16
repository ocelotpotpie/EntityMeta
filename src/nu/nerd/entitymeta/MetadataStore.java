package nu.nerd.entitymeta;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

// ----------------------------------------------------------------------------
/**
 * MetadataStore maintains a mapping from entities to cached
 * {@link PerEntityMetadata}.
 * <p>
 * 
 * PerEntityMetadata entries are expired from the cache when they are no longer
 * reachable by strong or soft references.
 */
final class MetadataStore {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     */
    public MetadataStore() {
        rebuildCache();
    }

    // ------------------------------------------------------------------------
    /**
     * Rebuild the cache according to the new configuration settings.
     * 
     * Since Minecraft itself is responsible for storing tags when entities are
     * unloaded, we don't need to carry over cache entries to the new cache.
     */
    public void rebuildCache() {
        _entityMetadata = CacheBuilder.newBuilder()
        .weakKeys()
        .maximumSize(EntityMeta.CONFIG.CACHE_SIZE)
        .expireAfterAccess(EntityMeta.CONFIG.CACHE_SECONDS, TimeUnit.SECONDS)
        .removalListener(REMOVAL_LISTENER)
        .build(new CacheLoader<Entity, PerEntityMetadata>() {
            @Override
            public PerEntityMetadata load(Entity entity) throws Exception {
                return new PerEntityMetadata(entity);
            }
        });
    }

    // ------------------------------------------------------------------------
    /**
     * Set a single metadata value on an Entity.
     * 
     * @param entity the entity.
     * @param key must be of the form {@code <plugin>.<name>}.
     * @param value the value; must be non-null.
     * 
     * @throws EntityMetadataException if the entity is null, if the value is of
     *         an unsupported type, or enclosing any exception thrown when
     *         loading metadata.
     */
    public void setValue(Entity entity, String key, Object value) throws EntityMetadataException {
        MetadataType type = MetadataTypeManager.INSTANCE.getType(value);
        if (type == null) {
            throw new EntityMetadataException("unsuported metadata type", null);
        }

        PerEntityMetadata meta = getCached(entity);
        meta.removeEntry(entity, key);
        meta.setEntry(entity, key, value, type);
    }

    // ------------------------------------------------------------------------
    /**
     * Remove a metadata value from an entity.
     * 
     * @param entity the entity.
     * @param key must be of the form {@code <plugin>.<name>}.
     * 
     * @throws EntityMetadataException if the entity is null, or enclosing any
     *         exception thrown when loading metadata.
     */
    public void removeValue(Entity entity, String key) throws EntityMetadataException {
        PerEntityMetadata meta = getIfCached(entity);
        if (meta != null) {
            meta.removeEntry(entity, key);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Return an entity's metadata value, or null if not set.
     * 
     * @param entity the entity.
     * @param key must be of the form {@code <plugin>.<name>}.
     * @return an entity's metadata value, or null if not set.
     * 
     * @throws EntityMetadataException if the entity is null, or enclosing any
     *         exception thrown when loading metadata.
     */
    public Object getValue(Entity entity, String key) throws EntityMetadataException {
        MetadataEntry entry = getEntry(entity, key);
        return (entry != null) ? entry.getValue() : null;
    }

    // ------------------------------------------------------------------------
    /**
     * Return a {@link MetadataEntry} of an entity.
     * 
     * @param entity the entity.
     * @param key must be of the form {@code <plugin>.<name>}.
     * @return the {@link MetadataEntry}, or null if not set.
     * 
     * @throws EntityMetadataException if the entity is null, or enclosing any
     *         exception thrown when loading metadata.
     */
    public MetadataEntry getEntry(Entity entity, String key) throws EntityMetadataException {
        PerEntityMetadata meta = getCached(entity);
        return meta.getEntry(key);
    }

    // ------------------------------------------------------------------------
    /**
     * Return all {@link MetadataEntry}s, in no particular order.
     * 
     * @param entity the entity.
     * @return all {@link MetadataEntry}s, in no particular order.
     * 
     * @throws EntityMetadataException if the entity is null, or enclosing any
     *         exception thrown when loading metadata.
     */
    public Map<String, MetadataEntry> getAllEntries(Entity entity) throws EntityMetadataException {
        PerEntityMetadata meta = getCached(entity);
        return meta.getAllEntries();
    }

    // ------------------------------------------------------------------------
    /**
     * Return all {@link MetadataEntry}s belonging to the specified plugin, in
     * no particular order.
     * 
     * @param entity the entity.
     * @return all {@link MetadataEntry}s, in no particular order.
     * 
     * @throws EntityMetadataException if the entity is null, or enclosing any
     *         exception thrown when loading metadata.
     */
    public Map<String, MetadataEntry> getPluginEntries(Entity entity, String pluginName) throws EntityMetadataException {
        PerEntityMetadata meta = getCached(entity);
        return meta.getPluginEntries(pluginName);
    }

    // ------------------------------------------------------------------------
    /**
     * Return metadata for the entity, caching it as necessary.
     * 
     * @param entity the entity.
     * @returns the cached metadata.
     * 
     * @throws EntityMetadataException if the entity is null, or enclosing any
     *         exception thrown when loading metadata.
     */
    private PerEntityMetadata getCached(Entity entity) throws EntityMetadataException {
        checkEntity(entity);
        try {
            return _entityMetadata.get(entity);
        } catch (ExecutionException ex) {
            throw new EntityMetadataException("error loading metadata", ex);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Return metadata for the entity if it is cached, or null if it is not.
     * 
     * @param entity the entity.
     * @returns the cached metadata, or null if not cached.
     * 
     * @throws EntityMetadataException if the entity is null.
     */
    private PerEntityMetadata getIfCached(Entity entity) throws EntityMetadataException {
        checkEntity(entity);
        return _entityMetadata.getIfPresent(entity);
    }

    // --------------------------------------------------------------------------
    /**
     * Check that the specified Entity is not null.
     * 
     * @param entity the entity.
     * 
     * @throws EntityMetadataException if the entity is null.
     */
    private void checkEntity(Entity entity) throws EntityMetadataException {
        if (entity == null) {
            throw new EntityMetadataException("null entity");
        }
    }

    // ------------------------------------------------------------------------
    /**
     * If enabled in the configuration, log cache expiration of entity metadata.
     */
    private final RemovalListener<Entity, PerEntityMetadata> REMOVAL_LISTENER = new RemovalListener<Entity, PerEntityMetadata>() {
        @Override
        public void onRemoval(RemovalNotification<Entity, PerEntityMetadata> notification) {
            if (EntityMeta.CONFIG.DEBUG_EXPIRY) {
                Logger logger = EntityMeta.PLUGIN.getLogger();
                Entity entity = notification.getKey();
                String entityText = (entity != null) ? entity.getType() + " " + entity.getUniqueId()
                                                     : "null";
                logger.info("Expiring " + entityText + " because " + notification.getCause());
            }
        }
    };

    /**
     * A map from entity to its metadata, which is automatically cached on
     * get(). Weak keys are used to expire metadata automatically.
     */
    private LoadingCache<Entity, PerEntityMetadata> _entityMetadata;

} // class MetadataStore