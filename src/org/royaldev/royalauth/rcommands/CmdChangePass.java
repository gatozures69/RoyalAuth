package org.royaldev.royalauth.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalauth.RoyalAuth;

public class CmdChangePass implements CommandExecutor {

    RoyalAuth plugin;

    public CmdChangePass(RoyalAuth instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("changepass")) {
            if (!cs.hasPermission("rauth.changepass")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players.");
                return true;
            }
            if (args.length < 2) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            Player p = (Player) cs;
            String oldPass = args[0];
            String newPass = args[1];
            if (!plugin.auth.checkPassword(p, oldPass)) {
                cs.sendMessage(ChatColor.RED + "Incorrect password!");
                return true;
            }
            boolean success = plugin.auth.changePassword(p, newPass);
            if (success) cs.sendMessage(ChatColor.BLUE + "Password changed.");
            if (!success) cs.sendMessage(ChatColor.RED + "Could not change password.");
            return true;
        }
        return false;
    }

}
