package org.royaldev.royalauth.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.royaldev.royalauth.RoyalAuth;

import java.util.Date;

public class RApListener implements Listener {

    RoyalAuth plugin;

    public RApListener(RoyalAuth instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) return;
        if (plugin.useSessions && plugin.expandSession) plugin.auth.setLoginDate(p, new Date().getTime());
        plugin.auth.updateLocation(p, p.getLocation());
        plugin.auth.setLoggedIn(p, false);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) return;
        if (plugin.useSessions && plugin.expandSession) plugin.auth.setLoginDate(p, new Date().getTime());
        plugin.auth.updateLocation(p, p.getLocation());
        plugin.auth.setLoggedIn(p, false);
    }

    @EventHandler()
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) p.teleport(p.getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onTele(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (e.getTo().equals(p.getWorld().getSpawnLocation())) return;
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler()
    public void onChat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler()
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler()
    public void onCreate(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler()
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler()
    public void onIntEnt(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler()
    public void onDam(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler()
    public void onCommand(PlayerCommandPreprocessEvent e) {
        for (String s : plugin.allowedCommands) if (e.getMessage().contains(s)) return;
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (plugin.auth.getLocation(p) == null) {
            plugin.auth.setLocation(p, p.getLocation());
        } else {
            plugin.auth.updateLocation(p, p.getLocation());
        }
        if (plugin.useSessions && plugin.auth.isSessionValid(p, plugin.sessionLength*60000)) {
            plugin.auth.setLoggedIn(p, true);
            p.sendMessage(ChatColor.BLUE + "Logged in by session.");
            plugin.log.info("[RoyalAuth] " + p.getName() + " logged in via session.");
            return;
        }
        p.teleport(p.getWorld().getSpawnLocation());
        plugin.auth.setLoggedIn(p, false);
        if (plugin.auth.isInDatabase(p)) {
            p.sendMessage(ChatColor.RED + "You are not logged in!");
            p.sendMessage(ChatColor.RED + "Please log in using /login [password]");
        } else {
            p.sendMessage(ChatColor.RED + "You are not registered!");
            p.sendMessage(ChatColor.RED + "Please register by using /register [password]");
        }
    }

}
