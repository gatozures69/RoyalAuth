package org.royaldev.royalauth.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.royaldev.royalauth.RoyalAuth;

import java.util.Date;

import static org.royaldev.royalauth.Language._;

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
        for (String s : plugin.allowedCommands) if (e.getMessage().equalsIgnoreCase(s.trim())) return;
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (plugin.useSessions && plugin.auth.isSessionValid(p, plugin.sessionLength * 60000)) {
            p.sendMessage(ChatColor.BLUE + _("SESSION"));
            plugin.log.info("[RoyalAuth] " + p.getName() + " logged in via session.");
            plugin.auth.setLoggedIn(p, true);
            return;
        }
        if (plugin.auth.getLocation(p) == null) {
            plugin.auth.setLocation(p, p.getLocation());
        } else {
            plugin.auth.updateLocation(p, p.getLocation());
        }
        p.teleport(p.getWorld().getSpawnLocation());
        plugin.auth.setLoggedIn(p, false);
        if (plugin.auth.isInDatabase(p)) {
            p.sendMessage(ChatColor.RED + _("NOT_LOGGED"));
            p.sendMessage(ChatColor.RED + _("PLEASE_LOG"));
        } else {
            p.sendMessage(ChatColor.RED + _("NOT_REGGED"));
            p.sendMessage(ChatColor.RED + _("PLEASE_REG"));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler
    public void onDamageIn(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler
    public void onDamageOut(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        if (!plugin.auth.getLoggedIn(p)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        for (Player o : plugin.getServer().getOnlinePlayers()) {
            if (p.getName().equals(o.getName()))
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, _("NAME_USED"));
        }
    }

}
