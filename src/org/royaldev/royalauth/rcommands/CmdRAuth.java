package org.royaldev.royalauth.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalauth.Language;
import org.royaldev.royalauth.RoyalAuth;

public class CmdRAuth implements CommandExecutor {

    RoyalAuth plugin;

    public CmdRAuth(RoyalAuth instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rauth")) {
            if (!cs.hasPermission("rauth.rauth")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String command = args[0];
            if (command.equals("?")) {
                cs.sendMessage(ChatColor.GRAY + "/" + cmd.getName() + " register [player] [password]" + ChatColor.BLUE + " - Registers specified player.");
                cs.sendMessage(ChatColor.GRAY + "/" + cmd.getName() + " changepass [player] [password]" + ChatColor.BLUE + " - Changes the user's password.");
                cs.sendMessage(ChatColor.GRAY + "/" + cmd.getName() + " logout [player]" + ChatColor.BLUE + " - Logs out the player.");
                cs.sendMessage(ChatColor.GRAY + "/" + cmd.getName() + " ?" + ChatColor.BLUE + " - Shows this help.");
                return true;
            } else if (command.toLowerCase().startsWith("register")) {
                if (args.length < 3) {
                    cs.sendMessage(ChatColor.RED + "Not enough arguments!");
                    cs.sendMessage(ChatColor.RED + "Try " + ChatColor.GRAY + "/" + cmd.getName() + " ?" + ChatColor.RED + ".");
                    return true;
                }
                String name = args[1];
                String pass = args[2];
                boolean success = plugin.auth.registerPlayer(name, pass, RoyalAuth.type);
                if (success)
                    cs.sendMessage(ChatColor.BLUE + "Registered " + ChatColor.GRAY + name + ChatColor.BLUE + " successfully.");
                if (!success)
                    cs.sendMessage(ChatColor.RED + "Could not register " + ChatColor.GRAY + name + ChatColor.RED + ".");
                return true;
            } else if (command.toLowerCase().startsWith("changepass")) {
                if (args.length < 3) {
                    cs.sendMessage(ChatColor.RED + "Not enough arguments!");
                    cs.sendMessage(ChatColor.RED + "Try " + ChatColor.GRAY + "/" + cmd.getName() + " ?" + ChatColor.RED + ".");
                    return true;
                }
                String name = args[1];
                String pass = args[2];
                boolean success = plugin.auth.changePassword(name, pass, RoyalAuth.type);
                if (success) cs.sendMessage(ChatColor.BLUE + "Password changed.");
                if (!success) cs.sendMessage(ChatColor.RED + "Could not change password.");
                return true;
            } else if (command.equalsIgnoreCase("reload")) {
                plugin.loadConfiguration();
                Language.reload();
                cs.sendMessage(ChatColor.BLUE + "Configuration reloaded.");
                return true;
            } else if (command.toLowerCase().startsWith("logout")) {
                if (args.length < 2) {
                    cs.sendMessage(ChatColor.RED + "Not enough arguments!");
                    cs.sendMessage(ChatColor.RED + "Try " + ChatColor.GRAY + "/" + cmd.getName() + " ?" + ChatColor.RED + ".");
                    return true;
                }
                String name = args[1];
                Player t = plugin.getServer().getPlayer(name);
                if (t == null) {
                    cs.sendMessage(ChatColor.RED + "That player does not exist!");
                    return true; 
                }
                if (!plugin.auth.getLoggedIn(t)) {
                    cs.sendMessage(ChatColor.RED + "That player is not logged in!");
                    return true;
                }
                plugin.auth.setLoggedIn(t, false);
                cs.sendMessage(ChatColor.BLUE + "Logged player " + ChatColor.GRAY + t.getName() + ChatColor.BLUE + " out.");
                return true;
            } else {
                cs.sendMessage(ChatColor.RED + "Invalid subcommand!");
                cs.sendMessage(ChatColor.RED + "Try " + ChatColor.GRAY + "/" + cmd.getName() + " ?" + ChatColor.RED + ".");
                return true;
            }
        }
        return false;
    }

}
