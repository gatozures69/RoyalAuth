package org.royaldev.royalauth.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalauth.RoyalAuth;

import java.util.Date;

import static org.royaldev.royalauth.Language._;

public class CmdLogin implements CommandExecutor {

    RoyalAuth plugin;

    public CmdLogin(RoyalAuth instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("login")) {
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + _("ONLY_PLAYERS"));
                return true;
            }
            Player p = (Player) cs;
            if (plugin.auth.getLoggedIn(p)) {
                p.sendMessage(ChatColor.RED + _("ALREADY_LOGGED_IN"));
                return true;
            }
            if (plugin.auth.checkPassword(p, args[0])) p.sendMessage(ChatColor.BLUE + _("SUCCESS"));
            else {
                p.sendMessage(ChatColor.RED + _("WRONG_PASS"));
                plugin.log.warning("[RoyalAuth] " + p.getName() + " used the wrong password!");
                return true;
            }
            plugin.auth.setLoggedIn(p, true);
            plugin.auth.setLoginDate(p, new Date().getTime());
            Location l = plugin.auth.getLocation(p);
            if (l != null) p.teleport(l);
            plugin.log.info("[RoyalAuth] " + p.getName() + " logged in.");
            return true;
        }
        return false;
    }
}
