package nu.nerd.entitymeta.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nu.nerd.entitymeta.EntityMeta;

// ----------------------------------------------------------------------------
/**
 * Handles the {@code /entitymeta-reload} command.
 */
public class ReloadCommand implements CommandExecutor {
    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        EntityMeta.CONFIG.reload();
        sender.sendMessage(ChatColor.GOLD + "EntityMeta configuration reloaded.");
        return true;
    }
} // class ReloadCommand