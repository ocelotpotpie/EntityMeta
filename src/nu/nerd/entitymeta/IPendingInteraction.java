package nu.nerd.entitymeta;

import org.bukkit.event.player.PlayerInteractEntityEvent;

// ----------------------------------------------------------------------------
/**
 * Records pending processing of a PlayerInteractEntityEvent for a specific
 * player.
 * 
 * An implementation of this interface is attached to a player as transient
 * Bukkit metadata and consulted when that player clicks on an entity.
 */
public interface IPendingInteraction {
    /**
     * Key when instances of this interface are used as Bukkit metadata.
     */
    public static final String METADATA_KEY = "IPendingInteraction";// .class.getSimpleName();

    // ------------------------------------------------------------------------
    /**
     * Handle a player interacting with an entity by performing a pending task.
     * 
     * @param event the entity interaction event.
     */
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event);
} // class IPendingInteraction