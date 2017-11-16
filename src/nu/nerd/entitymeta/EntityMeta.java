package nu.nerd.entitymeta;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import nu.nerd.entitymeta.commands.ClearCommand;
import nu.nerd.entitymeta.commands.FindCommand;
import nu.nerd.entitymeta.commands.GetCommand;
import nu.nerd.entitymeta.commands.ListCommand;
import nu.nerd.entitymeta.commands.ReloadCommand;
import nu.nerd.entitymeta.commands.SetCommand;
import nu.nerd.entitymeta.commands.SetTestCommand;

// ----------------------------------------------------------------------------
/**
 * The plugin class, which provides access to the API with
 * {@link EntityMeta#api()}.
 */
public class EntityMeta extends JavaPlugin implements Listener {
    /**
     * This plugin as a singleton.
     */
    public static EntityMeta PLUGIN;

    /**
     * The configuration as a singleton.
     */
    public static final Configuration CONFIG = new Configuration();

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

        getCommand("entitymeta-reload").setExecutor(new ReloadCommand());
        getCommand("entitymeta-list").setExecutor(new ListCommand());
        getCommand("entitymeta-list-raw").setExecutor(new ListCommand());
        getCommand("entitymeta-find").setExecutor(new FindCommand());
        getCommand("entitymeta-set").setExecutor(new SetCommand());
        getCommand("entitymeta-set-test").setExecutor(new SetTestCommand());
        getCommand("entitymeta-get").setExecutor(new GetCommand());
        getCommand("entitymeta-get-raw").setExecutor(new GetCommand());
        getCommand("entitymeta-clear").setExecutor(new ClearCommand());

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // ------------------------------------------------------------------------
    /**
     * Handle any pending entity interactions that were set by commands.
     * 
     * @param event the event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        handleInteraction(event);
    }

    // ------------------------------------------------------------------------
    /**
     * Handle any pending entity interactions that were set by commands.
     * 
     * Armour stands, at least, get {@link PlayerInteractAtEntityEvent} and not
     * {@link PlayerInteractEvent}.
     * 
     * @param event the event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        handleInteraction(event);
    }

    // ------------------------------------------------------------------------
    /**
     * Clear any pending entity interactions on logout.
     * 
     * @param event the event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata(IPendingInteraction.METADATA_KEY, this);
    }

    // ------------------------------------------------------------------------
    /**
     * Common code for handling right click interactions.
     * 
     * @param event the triggering event.
     */
    void handleInteraction(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        List<MetadataValue> metas = player.getMetadata(IPendingInteraction.METADATA_KEY);
        if (!metas.isEmpty()) {
            IPendingInteraction interaction = (IPendingInteraction) metas.get(0).value();
            interaction.onPlayerInteractEntity(event);
            event.setCancelled(true);
            player.removeMetadata(IPendingInteraction.METADATA_KEY, this);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * The API implementation.
     */
    private static final EntityMetaAPI _api = new EntityMetaAPI();
} // class EntityMeta