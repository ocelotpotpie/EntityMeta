package nu.nerd.entitymeta.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
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
import nu.nerd.entitymeta.MetadataType;
import nu.nerd.entitymeta.MetadataTypeManager;

// ----------------------------------------------------------------------------
/**
 * Handles the {@code /entitymeta-set} command.
 */
public class SetCommand implements CommandExecutor {
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
            // Let the help system format the help topic and sneakily message
            // the player a list of all type codes on the next tick.
            Bukkit.getScheduler().runTaskLater(EntityMeta.PLUGIN, () -> {
                sender.sendMessage(ChatColor.GOLD + "Supported metadata type codes and corresponding classes:");
                sender.sendMessage(MetadataTypeManager.INSTANCE.getAllTypes().stream()
                .map(type -> type.getDescription())
                .collect(Collectors.joining(", ")));
            }, 1L);
            return false;
        } else if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /" + command.getName() + " help.");
        } else {
            String key = args[0];
            String typeCode = args[1];
            String serialisedValue = Arrays.stream(args, 2, args.length).collect(Collectors.joining(" "));

            if (!EntityMetaAPI.KEY_PATTERN.matcher(key).matches()) {
                sender.sendMessage(ChatColor.RED + "Invalid key! It has to be of the form <plugin>.<name>, " +
                                   "where <plugin> consists only of letters, digits and underscores, " +
                                   "and <name> consists only of letters, digits, underscores and hyphens.");
                return true;
            }

            MetadataType type;
            if (typeCode.length() == 1) {
                type = MetadataTypeManager.INSTANCE.getTypeByCode(typeCode.charAt(0));

            } else {
                Class<?> clazz;
                try {
                    clazz = Class.forName(typeCode);
                    type = MetadataTypeManager.INSTANCE.getTypeByClass(clazz);
                } catch (ClassNotFoundException ex) {
                    sender.sendMessage(ChatColor.RED + typeCode + " is not a valid class name! Did you forget to fully qualify it?");
                    return true;
                }
            }

            if (type == null) {
                sender.sendMessage(ChatColor.RED + typeCode + " is not a supported type code or class name!");
                sender.sendMessage(ChatColor.RED + "Try /" + command.getName() + " help.");
            }

            Object value;
            try {
                value = type.fromString(serialisedValue);
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(ChatColor.RED + serialisedValue + " could not be encoded as type " + type.getValueClass().getName());
                return true;
            }

            String[] keyParts = key.split("\\.", 2);
            String pluginName = keyParts[0];
            String name = keyParts[1];

            sender.sendMessage(ChatColor.GOLD + "Right click on an entity to set that metadata value.");
            Player player = (Player) sender;
            player.setMetadata(IPendingInteraction.METADATA_KEY, new FixedMetadataValue(EntityMeta.PLUGIN, new IPendingInteraction() {
                @Override
                public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
                    Entity entity = event.getRightClicked();
                    EntityMeta.api().set(entity, pluginName, name, value);
                    event.getPlayer().sendMessage(ChatColor.GOLD + "Successfully set " + ChatColor.YELLOW + key +
                                                  ChatColor.GOLD + " to " + ChatColor.YELLOW + serialisedValue +
                                                  ChatColor.GOLD + " on " + ChatColor.YELLOW + entity.getType() + " " +
                                                  ChatColor.GOLD + entity.getUniqueId() + ".");
                }
            }));
        }
        return true;
    }
} // class SetCommand