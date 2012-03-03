package org.royaldev.royalauth;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.royaldev.royalauth.listeners.RApListener;
import org.royaldev.royalauth.rcommands.*;

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
    public List<String> allowedCommands;

    public Boolean disableOn;
    public Boolean useSessions;
    public Boolean expandSession;
    
    public Long sessionLength;

    public void loadConfiguration() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        db = getConfig().getString("database");
        host = getConfig().getString("host");
        user = getConfig().getString("user");
        pass = getConfig().getString("password");
        tp = getConfig().getString("table_prefix");
        allowedCommands = getConfig().getStringList("allowed_commands");

        disableOn = getConfig().getBoolean("disable_if_online");
        useSessions = getConfig().getBoolean("sessions.enabled");
        expandSession = getConfig().getBoolean("sessions.expand_session_on_quit");

        sessionLength = getConfig().getLong("sessions.length");
    }

    public void onEnable() {

        loadConfiguration();

        if (disableOn && getServer().getOnlineMode()) {
            setEnabled(false);
            log.info("[" + getDescription().getName() + "] Disabled - online mode is active.");
            return;
        }

        auth = new RAAuth(host + db, user, pass, db, tp);

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
