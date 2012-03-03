package org.royaldev.royalauth.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalauth.RoyalAuth;

public class CmdRegister implements CommandExecutor {

    RoyalAuth plugin;

    public CmdRegister(RoyalAuth instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("register")) {
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            if (plugin.auth.isInDatabase(p)) {
                p.sendMessage(ChatColor.RED + "You're already registered!");
                return true;
            }
            boolean success = plugin.auth.registerPlayer(p, args[0]);
            if (success) p.sendMessage(ChatColor.BLUE + "Success!");
            if (!success) {
                p.sendMessage(ChatColor.RED + "Failure!");
                return true;
            }
            plugin.auth.setLoggedIn(p, true);
            return true;
        }
        return false;
    }

}
