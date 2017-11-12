package nu.nerd.entitymeta;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

// ----------------------------------------------------------------------------
/**
 * The plugin class, which provides access to the API with
 * {@link EntityMeta#api()}.
 */
public class EntityMeta extends JavaPlugin {
    /**
     * This plugin as a singleton.
     */
    static EntityMeta PLUGIN;

    /**
     * The configuration as a singleton.
     */
    static final Configuration CONFIG = new Configuration();

    // ------------------------------------------------------------------------
    /**
     * Get a reference to the API.
     * 
     * @return the API.
     */
    public static EntityMetaAPI api() {
        return _api;
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        PLUGIN = this;

        saveDefaultConfig();
        CONFIG.reload();
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * The API implementation.
     */
    private static final EntityMetaAPI _api = new EntityMetaAPI();
} // class EntityMeta