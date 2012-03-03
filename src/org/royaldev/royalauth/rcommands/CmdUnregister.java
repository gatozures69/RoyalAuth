package org.royaldev.royalauth.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalauth.RoyalAuth;

public class CmdUnregister implements CommandExecutor {

    RoyalAuth plugin;

    public CmdUnregister(RoyalAuth instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("unregister")) {
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            boolean success = plugin.auth.removePlayer(p, args[0]);
            plugin.auth.setLoggedIn(p, false);
            if (success) p.sendMessage(ChatColor.BLUE + "Success!");
            return true;
        }
        return false;
    }

}