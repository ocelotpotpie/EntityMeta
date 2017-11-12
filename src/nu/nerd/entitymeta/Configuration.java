package nu.nerd.entitymeta;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

// ----------------------------------------------------------------------------
/**
 * Configuration wrapper.
 */
class Configuration {
    // ------------------------------------------------------------------------
    /**
     * If true, log configuration loading.
     */
    public boolean DEBUG_CONFIG;

    /**
     * If true, log cache entry expiry.
     */
    public boolean DEBUG_EXPIRY;

    /**
     * Maximum number of entities held in the metadata cache.
     */
    public int CACHE_SIZE;

    /**
     * Retention period of the entity metadata cache in seconds.
     */
    public int CACHE_SECONDS;

    // ------------------------------------------------------------------------
    /**
     * Reload the configuration file.
     */
    public void reload() {
        EntityMeta.PLUGIN.reloadConfig();

        DEBUG_CONFIG = getConfig().getBoolean("debug.config");
        DEBUG_EXPIRY = getConfig().getBoolean("debug.expiry");
        CACHE_SIZE = getConfig().getInt("cache-size");
        CACHE_SECONDS = getConfig().getInt("cache-seconds");
        if (DEBUG_CONFIG) {
            getLogger().info("Configuration:");

        }
    }

    // ------------------------------------------------------------------------
    /**
     * Return the plugin's FileConfiguration.
     *
     * @return the plugin's FileConfiguration.
     */
    protected FileConfiguration getConfig() {
        return EntityMeta.PLUGIN.getConfig();
    }

    // ------------------------------------------------------------------------
    /**
     * Return the plugin's Logger.
     *
     * @return the plugin's Logger.
     */
    protected Logger getLogger() {
        return EntityMeta.PLUGIN.getLogger();
    }
} // class Configuration