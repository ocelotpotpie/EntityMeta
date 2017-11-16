package nu.nerd.entitymeta.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import nu.nerd.entitymeta.EntityMeta;
import nu.nerd.entitymeta.EntityMetaAPI;
import nu.nerd.entitymeta.MetadataType;
import nu.nerd.entitymeta.MetadataTypeManager;

// ----------------------------------------------------------------------------
/**
 * Handles
 * {@code /entitymeta-find <entity-types> <radius> <key> [<type> <value>]}.
 */
public class FindCommand implements CommandExecutor {
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
        Player player = (Player) sender;

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
        } else if (args.length < 3 || args.length == 4) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /" + command.getName() + " help.");
        } else {
            String entityTypesArg = args[0];
            String radiusArg = args[1];
            String keyArg = args[2];

            EnumSet<EntityType> entityTypes;
            if (entityTypesArg.equals("*")) {
                entityTypes = EnumSet.allOf(EntityType.class);
            } else {
                entityTypes = EnumSet.noneOf(EntityType.class);
                for (String entityTypeName : entityTypesArg.split(",")) {
                    // Allow leading and trailing commas.
                    if (!entityTypeName.isEmpty()) {
                        try {
                            entityTypes.add(EntityType.valueOf(entityTypeName.toUpperCase()));
                        } catch (IllegalArgumentException ex) {
                            sender.sendMessage(ChatColor.RED + entityTypeName + " is not a valid entity type!");
                            return true;
                        }
                    }
                }
            }

            double radius;
            try {
                radius = Double.parseDouble(radiusArg);
                if (radius <= 0) {
                    sender.sendMessage(ChatColor.RED + "A negative radius isn't going to return any results!");
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + radiusArg + " is not a number!");
                return true;
            }

            if (!EntityMetaAPI.KEY_PATTERN.matcher(keyArg).matches()) {
                sender.sendMessage(ChatColor.RED + "Invalid key! It has to be of the form <plugin>.<name>, " +
                                   "where <plugin> consists only of letters, digits and underscores, " +
                                   "and <name> consists only of letters, digits, underscores and hyphens.");
                return true;
            }

            Object value;
            if (args.length < 5) {
                // Don't filter on value.
                value = null;
            } else {
                String typeArg = args[3];
                String valueArg = Arrays.stream(args, 4, args.length).collect(Collectors.joining(" "));

                MetadataType type;
                if (typeArg.length() == 1) {
                    type = MetadataTypeManager.INSTANCE.getTypeByCode(typeArg.charAt(0));

                } else {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(typeArg);
                        type = MetadataTypeManager.INSTANCE.getTypeByClass(clazz);
                    } catch (ClassNotFoundException ex) {
                        sender.sendMessage(ChatColor.RED + typeArg + " is not a valid class name! Did you forget to fully qualify it?");
                        return true;
                    }
                }

                if (type == null) {
                    sender.sendMessage(ChatColor.RED + typeArg + " is not a supported type code or class name!");
                    sender.sendMessage(ChatColor.RED + "Try /" + command.getName() + " help.");
                }

                try {
                    value = type.fromString(valueArg);
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(ChatColor.RED + valueArg + " could not be encoded as type " + type.getValueClass().getName());
                    return true;
                }
            }

            String[] keyParts = keyArg.split("\\.", 2);
            String pluginName = keyParts[0];
            String name = keyParts[1];

            Location loc = player.getLocation();
            World world = loc.getWorld();
            Collection<Entity> nearbyEntities = world.getNearbyEntities(loc, radius, radius, radius);
            double radiusSquared = radius * radius;
            List<Entity> sortedFilteredEntities = nearbyEntities.stream()
            .filter(e -> entityTypes.contains(e.getType()) &&
                         e.getLocation().distanceSquared(loc) <= radiusSquared &&
                         ((value == null && EntityMeta.api().getEntry(e, pluginName, name) != null) ||
                          (value != null && value.equals(EntityMeta.api().get(e, pluginName, name)))))
            .sorted(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(loc)))
            .collect(Collectors.toList());

            if (sortedFilteredEntities.isEmpty()) {
                sender.sendMessage(ChatColor.GOLD + "No matching entities found.");
                return true;
            }

            final int MAX_RESULTS = 100;
            if (sortedFilteredEntities.size() > MAX_RESULTS) {
                sender.sendMessage(ChatColor.GOLD + "Showing the nearest " + MAX_RESULTS + " of " +
                                   sortedFilteredEntities.size() + " matching entities:");
            } else {
                sender.sendMessage(ChatColor.GOLD + "Showing matching entities, nearest first:");
            }

            int i = 0;
            StringBuilder message = new StringBuilder();
            String sep = "";
            for (Entity entity : sortedFilteredEntities) {
                Location entityLoc = entity.getLocation();
                message.append(ChatColor.WHITE).append(sep);
                sep = ", ";
                message.append(ChatColor.WHITE).append('(').append(++i).append(") ");
                message.append(ChatColor.YELLOW).append(entity.getType());
                message.append(ChatColor.GOLD).append(" (").append(ChatColor.YELLOW).append(entityLoc.getBlockX());
                message.append(ChatColor.GOLD).append(", ").append(ChatColor.YELLOW).append(entityLoc.getBlockY());
                message.append(ChatColor.GOLD).append(", ").append(ChatColor.YELLOW).append(entityLoc.getBlockZ());
                message.append(ChatColor.GOLD).append(")");
            }
            sender.sendMessage(message.toString());
        }
        return true;
    }
} // class FindCommand