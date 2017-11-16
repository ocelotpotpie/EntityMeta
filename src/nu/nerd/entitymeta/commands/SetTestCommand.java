package nu.nerd.entitymeta.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import nu.nerd.entitymeta.EntityMeta;
import nu.nerd.entitymeta.EntityMetadataException;
import nu.nerd.entitymeta.IPendingInteraction;

// ----------------------------------------------------------------------------
/**
 * Handles {@code /entitymeta-set-test}.
 */
public class SetTestCommand implements CommandExecutor {
    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be in game to use this command!");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            return false;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /" + command.getName() + " help.");
        } else {
            sender.sendMessage(ChatColor.GOLD + "Right click on an entity to set test metadata.");
            Player player = (Player) sender;
            player.setMetadata(IPendingInteraction.METADATA_KEY, new FixedMetadataValue(EntityMeta.PLUGIN, new IPendingInteraction() {
                @Override
                public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
                    Player player = event.getPlayer();
                    Entity entity = event.getRightClicked();

                    player.sendMessage(ChatColor.GOLD + "Set test metadata on " +
                                       ChatColor.YELLOW + entity.getType() +
                                       ChatColor.GOLD + entity.getUniqueId() + '.');
                    try {
                        EntityMeta.api().set(entity, "Test", "b", true);
                        EntityMeta.api().set(entity, "Test", "B", (byte) 127);
                        EntityMeta.api().set(entity, "Test", "c", '@');
                        EntityMeta.api().set(entity, "Test", "s", (short) 32767);
                        EntityMeta.api().set(entity, "Test", "i", 0x7FFF_FFFF);
                        EntityMeta.api().set(entity, "Test", "l", 0x7FFF_FFFF_FFFF_FFFFL);
                        EntityMeta.api().set(entity, "Test", "f", 1.234f);
                        EntityMeta.api().set(entity, "Test", "d", 1.23456789);
                        EntityMeta.api().set(entity, "Test", "S", "Hello world!");
                        EntityMeta.api().set(entity, "Test", "u", player.getUniqueId());
                        EntityMeta.api().set(entity, "Test", "location", player.getLocation());
                        EntityMeta.api().set(entity, "Test", "main-hand", player.getEquipment().getItemInMainHand());
                    } catch (EntityMetadataException ex) {
                        player.sendMessage(ChatColor.RED + "Error retrieving metadata for that entity!");
                    }
                }
            }));
        }

        return true;
    }
} // class SetTestCommand