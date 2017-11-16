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
import nu.nerd.entitymeta.EntityMetaAPI;
import nu.nerd.entitymeta.IPendingInteraction;
import nu.nerd.entitymeta.MetadataEntry;

// ----------------------------------------------------------------------------
/**
 * Handles {@code /entitymeta-get} and {@code /entitymeta-get-raw} commands,
 * choosing the formatting on the basis of the command name.
 */
public class GetCommand implements CommandExecutor {
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
        } else if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /" + command.getName() + " help.");
        } else {
            String key = args[0];
            if (!EntityMetaAPI.KEY_PATTERN.matcher(key).matches()) {
                sender.sendMessage(ChatColor.RED + "Invalid key! It has to be of the form <plugin>.<name>, " +
                                   "where <plugin> consists only of letters, digits and underscores, " +
                                   "and <name> consists only of letters, digits, underscores and hyphens.");
                return true;
            }

            String[] keyParts = key.split("\\.", 2);
            String pluginName = keyParts[0];
            String name = keyParts[1];

            sender.sendMessage(ChatColor.GOLD + "Right click on an entity to get that metadata value.");
            Player player = (Player) sender;
            player.setMetadata(IPendingInteraction.METADATA_KEY, new FixedMetadataValue(EntityMeta.PLUGIN, new IPendingInteraction() {
                @Override
                public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
                    Entity entity = event.getRightClicked();
                    Player player = event.getPlayer();
                    MetadataEntry entry = EntityMeta.api().getEntry(entity, pluginName, name);
                    if (entry == null) {
                        player.sendMessage(ChatColor.GOLD + "There is no " + ChatColor.YELLOW + key +
                                           ChatColor.GOLD + " metadata on " + ChatColor.YELLOW + entity.getType() + " " +
                                           ChatColor.GOLD + entity.getUniqueId() + ".");
                    } else {
                        boolean raw = command.getName().equalsIgnoreCase("entitymeta-get-raw");
                        player.sendMessage(entry.format(raw));
                    }
                }
            }));
        }
        return true;
    }
} // class GetCommand