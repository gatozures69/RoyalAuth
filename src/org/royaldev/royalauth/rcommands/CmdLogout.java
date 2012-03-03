package org.royaldev.royalauth.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalauth.RoyalAuth;

public class CmdLogout implements CommandExecutor {

    RoyalAuth plugin;

    public CmdLogout(RoyalAuth instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("logout")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            plugin.auth.updateLocation(p, p.getLocation());
            p.teleport(p.getWorld().getSpawnLocation());
            plugin.auth.setLoggedIn(p, false);
            return true;
        }
        return false;
    }
}
