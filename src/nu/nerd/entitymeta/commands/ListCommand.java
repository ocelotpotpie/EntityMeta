package nu.nerd.entitymeta.commands;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import nu.nerd.entitymeta.MetadataEntry;
import nu.nerd.entitymeta.MetadataType;

// ----------------------------------------------------------------------------
/**
 * Handles the {@code /entitymeta-list} and {@code /entitymeta-list} commands,
 * choosing the formatting on the basis of the command name.
 */
public class ListCommand implements CommandExecutor {
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

        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /" + command.getName() + " help.");
        } else {
            String pluginName = (args.length == 1) ? args[0] : null;
            if (pluginName != null && !EntityMeta.api().PLUGIN_PATTERN.matcher(pluginName).matches()) {
                sender.sendMessage(ChatColor.RED + pluginName + " is not a valid plugin name!");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "Right click on an entity to list its metadata.");
            Player player = (Player) sender;
            player.setMetadata(IPendingInteraction.METADATA_KEY, new FixedMetadataValue(EntityMeta.PLUGIN, new IPendingInteraction() {
                @Override
                public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
                    Player player = event.getPlayer();
                    Entity entity = event.getRightClicked();
                    try {
                        Map<String, MetadataEntry> entries = EntityMeta.api().getPluginEntries(entity, pluginName);
                        if (entries.isEmpty()) {
                            player.sendMessage(ChatColor.GOLD + "No matching metadata set on " +
                                               ChatColor.YELLOW + entity.getType() + ' ' +
                                               ChatColor.GOLD + entity.getUniqueId() + '.');
                        } else {
                            String pluginClause = (pluginName == null) ? "" : ChatColor.GOLD + " for plugin " + ChatColor.YELLOW + pluginName;
                            player.sendMessage(ChatColor.GOLD + "Metadata set on " +
                                               ChatColor.YELLOW + entity.getType() + ' ' +
                                               ChatColor.GOLD + entity.getUniqueId() +
                                               pluginClause + ChatColor.GOLD + ':');
                            for (Entry<String, MetadataEntry> entry : new TreeMap<String, MetadataEntry>(entries).entrySet()) {
                                String[] keyParts = entry.getKey().split("\\.", 2);
                                String pluginName = keyParts[0];
                                String name = keyParts[1];

                                // Format raw or not based on command name.
                                if (command.getName().equalsIgnoreCase("entitymeta-list")) {
                                    MetadataType type = entry.getValue().getType();
                                    Object value = entry.getValue().getValue();
                                    player.sendMessage(ChatColor.YELLOW + pluginName + ChatColor.WHITE + '.' + ChatColor.YELLOW + name +
                                                       ChatColor.WHITE + " (" + ChatColor.GOLD + type.getCode() +
                                                       ChatColor.WHITE + ")" + ChatColor.GOLD + " -> " +
                                                       ChatColor.YELLOW + value);
                                } else {
                                    String tag = entry.getValue().getTag();
                                    String[] tagParts = tag.split(":", 3);
                                    player.sendMessage(ChatColor.YELLOW + pluginName + ChatColor.WHITE + '.' + ChatColor.YELLOW + name +
                                                       ChatColor.GOLD + ':' + ChatColor.YELLOW + tagParts[1] +
                                                       ChatColor.GOLD + ':' + ChatColor.YELLOW + tagParts[2]);
                                }
                            }
                        }
                    } catch (EntityMetadataException ex) {
                        player.sendMessage(ChatColor.RED + "Error retrieving metadata for that entity!");
                    }
                }
            }));
        }

        return true;
    }
} // class ListCommand