package org.royaldev.royalauth;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.royaldev.royalauth.listeners.RApListener;
import org.royaldev.royalauth.rcommands.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

public class RoyalAuth extends JavaPlugin {

    public Logger log = Logger.getLogger("Minecraft");
    public RAAuth auth;

    public String db;
    public String host;
    public String user;
    public String pass;
    public String tp;
    public String namedb;
    public String locdb;
    public static String lang;
    public static String type;
    public List<String> allowedCommands;

    public Boolean disableOn;
    public Boolean useSessions;
    public Boolean expandSession;

    public Long sessionLength;

    public void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        db = getConfig().getString("database");
        host = getConfig().getString("host");
        user = getConfig().getString("user");
        pass = getConfig().getString("password");
        tp = getConfig().getString("table_prefix");
        namedb = getConfig().getString("userdb_name");
        locdb = getConfig().getString("locationdb_name");
        lang = getConfig().getString("lang");
        type = getConfig().getString("encryption_type");

        allowedCommands = getConfig().getStringList("allowed_commands");

        disableOn = getConfig().getBoolean("disable_if_online");
        useSessions = getConfig().getBoolean("sessions.enabled");
        expandSession = getConfig().getBoolean("sessions.expand_session_on_quit");

        sessionLength = getConfig().getLong("sessions.length");
    }

    public void onEnable() {

        if (!new File(getDataFolder() + File.separator + "config.yml").exists()) saveResource("config.yml", false);
        if (!new File(getDataFolder() + File.separator + "messages.properties").exists())
            saveResource("messages.properties", false);

        loadConfiguration();

        try {
            new Language(this);
        } catch (Exception e) {
            log.info("[RoyalAuth] Language file not found! Disabling plugin.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (disableOn && getServer().getOnlineMode()) {
            getPluginLoader().disablePlugin(this);
            log.info("[" + getDescription().getName() + "] Disabled - online mode is active.");
            return;
        }

        auth = new RAAuth(host + db, user, pass, db, tp, namedb, locdb);

        final String user2 = user;
        final String url2 = host + db;
        final String pass2 = pass;
        Runnable pinger = new Runnable() {
            public void run() {
                try {
                    Connection con = DriverManager.getConnection(url2, user2, pass2);
                    Statement stmt = con.createStatement();
                    stmt.execute("/* ping */ SELECT 1");
                    con.close();
                } catch (Exception ignored) {
                }
            }
        };

        getServer().getScheduler().scheduleAsyncDelayedTask(this, pinger, 3600);

        PluginManager pm = getServer().getPluginManager();

        RApListener pListener = new RApListener(this);

        pm.registerEvents(pListener, this);

        getCommand("login").setExecutor(new CmdLogin(this));
        getCommand("logout").setExecutor(new CmdLogout(this));
        getCommand("register").setExecutor(new CmdRegister(this));
        getCommand("unregister").setExecutor(new CmdUnregister(this));
        getCommand("changepass").setExecutor(new CmdChangePass(this));
        getCommand("rauth").setExecutor(new CmdRAuth(this));

        log.info("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " enabled.");

    }

    public void onDisable() {

        log.info("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " disabled.");
        try {
            RAAuth.con.close();
        } catch (Exception ignored) {
        }

    }

}
