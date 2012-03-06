package org.royaldev.royalauth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")

/**
 * Contains all functions for RoyalAuth API
 */
public class RAAuth {

    Statement stmt;
    String prefix;
    static Connection con;

    /**
     * Main method for API functions
     *
     * @param url      URL of the MySQL database (jbdc)
     * @param user     Username for MySQL
     * @param password Password for user
     * @param database Database name in MySQL
     * @param prefix   Prefix for tables in the database
     */
    public RAAuth(String url, String user, String password, String database, String prefix) {

        this.prefix = prefix;

        Logger log = Logger.getLogger("Minecraft");
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
        } catch (SQLException e) {
            log.severe("[RoyalAuth] Could not connect to the MySQL database!");
            log.severe(e.getMessage());
            con = null;
        }

        /*try {
            stmt.execute("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = `" + database + "`;");
        } catch (Exception e) {
            plugin.getServer().getPluginManager().disablePlugin(ra);
            log.severe("[RoyalAuth] Database did not exist!");
            e.printStackTrace();
        }
        */

        try {
            stmt.execute("USE `" + database + "`;");
            stmt.execute("CREATE TABLE IF NOT EXISTS `" + prefix + "users` (name text, password text, date long, ip text);");
            stmt.execute("CREATE TABLE IF NOT EXISTS `" + prefix + "locations` (name text, world text, x int, y int, z int, pitch float, yaw float);");
        } catch (Exception e) {
            log.severe("[RoyalAuth] Could not create tables!");
            e.printStackTrace();
        }
    }

    /**
     * List of logged in players.
     */
    public static List<Player> login = new ArrayList<Player>();

    /**
     * Returns if the player is logged in.
     * <p/>
     * true - Player is logged in
     * <p/>
     * false - Player is not logged in
     *
     * @param p Player to check
     * @return If player is logged in or not
     */
    public boolean getLoggedIn(Player p) {
        return login.contains(p);
    }

    /**
     * Sets a player's login status.
     * <p/>
     * true - Player is logged in.
     * <p/>
     * false - Player is not logged in.
     *
     * @param p      Player to set status for
     * @param status Whether they are logged in
     */
    public void setLoggedIn(Player p, boolean status) {
        if (status) {
            login.add(p);
            String sql = "UPDATE `" + prefix + "users` SET ip ='" + p.getAddress().getAddress().toString().replace("/", "") + "' WHERE name = '" + p.getName() + "';";
            if (con == null) return;
            try {
                stmt.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        if (!status) login.remove(p);
    }

    /**
     * Checks if the player is in the MySQL database. Useful for seeing if a player
     * is registered.
     *
     * @param p Player to check for
     * @return true if player is in database, false if not
     */
    public boolean isInDatabase(Player p) {
        if (con == null) return false;
        ResultSet success;
        try {
            success = stmt.executeQuery("SELECT * FROM `" + prefix + "users` WHERE `name` = '" + p.getName() + "';");
            return success.absolute(1);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the player is in the MySQL database. Useful for seeing if a player
     * is registered.
     *
     * @param name Player name to check for
     * @return true if player is in database, false if not
     */
    public boolean isInDatabase(String name) {
        if (con == null) return false;
        ResultSet success;
        try {
            success = stmt.executeQuery("SELECT * FROM `" + prefix + "users` WHERE name = '" + name + "';");
            return success.absolute(1);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Changes the player's password.
     * Note: You may want to check if the player knows the old password first.
     *
     * @param name Name of player to change password for
     * @param pass New password
     * @return true if password was changed, false if it wasn't
     */
    public boolean changePassword(String name, String pass) {
        if (con == null) return false;
        String password;
        try {
            password = RASha.encrypt(pass);
        } catch (Exception e) {
            return false;
        }
        try {
            if (!isInDatabase(name)) return false;
            stmt.execute("UPDATE `" + prefix + "users` SET password = '" + password + "' WHERE name = '" + name + "';"); // <- fix
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Changes the player's password.
     * <p/>
     * Note: You may want to check if the player knows the old password first.
     *
     * @param p    Player to change password for
     * @param pass New password
     * @return true if password was changed, false if it wasn't
     */
    public boolean changePassword(Player p, String pass) {
        if (con == null) return false;
        String password;
        try {
            password = RASha.encrypt(pass);
        } catch (Exception e) {
            return false;
        }
        try {
            if (!isInDatabase(p)) return false;
            stmt.execute("UPDATE `" + prefix + "users` SET password = '" + password + "' WHERE name = '" + p.getName() + "';"); // <- fix
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets the location the player will be sent to when they are logged in.
     * <p/>
     * This should only be run if the player has no location. Use
     * updateLocation() instead.
     *
     * @param name Name of player
     * @param loc  Location for the player to be sent to
     * @return true if location was set, false if not
     */
    public boolean setLocation(String name, Location loc) {
        String sql = String.format("INSERT INTO `" + prefix + "locations` VALUES ('%s', '%s', %s, %s, %s, %s, %s);",
                name,
                loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw());
        try {
            return stmt.execute(sql);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates the location a player will be sent to upon login.
     *
     * @param name Name of player
     * @param loc  New location for the player to be sent to
     * @return true if location was updated, false if not
     */
    public boolean updateLocation(String name, Location loc) {
        String sql = String.format("UPDATE `" + prefix + "locations` SET world = '%s', x = %s, y = %s, z = %s, pitch = %s, yaw = %s WHERE name = '%s';",
                loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw(),
                name);
        try {
            return stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sets the location the player will be sent to when they are logged in.
     * <p/>
     * This should only be run if the player has no location. Use
     * updateLocation() instead.
     *
     * @param p   Player to update location for
     * @param loc Location for the player to be sent to
     * @return true if location was set, false if not
     */
    public boolean setLocation(Player p, Location loc) {
        String sql = String.format("INSERT INTO `" + prefix + "locations` VALUES ('%s', '%s', %s, %s, %s, %s, %s);",
                p.getName(),
                loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw());
        try {
            return stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the location a player will be sent to upon login.
     *
     * @param name Name of player to get location for
     * @return The location the player will be sent to
     */
    public Location getLocation(String name) {
        String sql = "SELECT * FROM `" + prefix + "locations` WHERE name = '" + name + "';";
        Location l;
        try {
            ResultSet r = stmt.executeQuery(sql);
            boolean success = r.absolute(1);
            if (!success) return null;
            String world = r.getString("world");
            int x = r.getInt("x");
            int y = r.getInt("y");
            int z = r.getInt("z");
            float pitch = r.getFloat("pitch");
            float yaw = r.getFloat("yaw");
            World w = Bukkit.getServer().getWorld(world);
            l = new Location(w, x, y, z, yaw, pitch);
            return l;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the location a player will be sent to upon login.
     *
     * @param p Player to get location for
     * @return The location the player will be sent to
     */
    public Location getLocation(Player p) {
        String sql = "SELECT * FROM `" + prefix + "locations` WHERE name = '" + p.getName() + "';";
        Location l;
        try {
            ResultSet r = stmt.executeQuery(sql);
            boolean success = r.absolute(1);
            if (!success) return null;
            String world = r.getString("world");
            int x = r.getInt("x");
            int y = r.getInt("y");
            int z = r.getInt("z");
            float pitch = r.getFloat("pitch");
            float yaw = r.getFloat("yaw");
            World w = Bukkit.getServer().getWorld(world);
            l = new Location(w, x, y, z, yaw, pitch);
            return l;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the location a player will be sent to upon login.
     *
     * @param p   Player to update
     * @param loc New location for the player to be sent to
     * @return true if location was updated, false if not
     */
    public boolean updateLocation(Player p, Location loc) {
        String sql = String.format("UPDATE `" + prefix + "locations` SET world = '%s', x = %s, y = %s, z = %s, pitch = %s, yaw = %s WHERE name = '%s';",
                loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw(),
                p.getName());
        try {
            return stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a player from the database. Requires their password for security
     * reasons.
     *
     * @param p    Player to remove from database
     * @param pass Player's password
     * @return true if player was removed, false if not
     */
    public boolean removePlayer(Player p, String pass) {
        if (con == null) return false;
        String password;
        try {
            password = RASha.encrypt(pass);
        } catch (Exception e) {
            return false;
        }
        try {
            if (!isInDatabase(p)) {
                p.sendMessage(ChatColor.RED + "You are not registered!");
                return false;
            }
            stmt.execute("DELETE FROM `" + prefix + "users` WHERE `name` = '" + p.getName() + "' AND `password` = '" + password + "';");
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Could not unregister you!");
            return false;
        }
        return true;
    }

    /**
     * Puts a player into the database, registering them.
     *
     * @param name Name of player to insert into database
     * @param pass Password for the new player
     * @return true if inserted, false if not
     */
    public boolean registerPlayer(String name, String pass) {
        if (con == null) return false;
        String password;
        try {
            password = RASha.encrypt(pass);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            if (isInDatabase(name)) {
                return false;
            }
            stmt.execute("INSERT INTO `" + prefix + "users` VALUES ('" + name + "', '" + password + "', '" + new Date().getTime() + "', '1';");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Puts a player into the database, registering them.
     *
     * @param p    Player to insert into database
     * @param pass Password for the new player
     * @return true if inserted, false if not
     */
    public boolean registerPlayer(Player p, String pass) {
        if (con == null) return false;
        String password;
        try {
            password = RASha.encrypt(pass);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            if (isInDatabase(p)) {
                p.sendMessage(ChatColor.RED + "You are already registered!");
                return false;
            }
            stmt.execute("INSERT INTO `" + prefix + "users` VALUES ('" + p.getName() + "', '" + password + "', '" + new Date().getTime() + "', '" + p.getAddress().getAddress().toString().replace("/", "") + "');");
        } catch (Exception e) {
            e.printStackTrace();
            p.sendMessage(ChatColor.RED + "Could not register you!");
            return false;
        }
        return true;
    }

    /**
     * Checks the password of the player.
     *
     * @param p    Player to check password on
     * @param pass Password to check
     * @return true if password was correct, false if not
     */
    public boolean checkPassword(Player p, String pass) {
        if (con == null) return false;
        ResultSet rs;
        String password = null;
        try {
            rs = stmt.executeQuery("SELECT * FROM `" + prefix + "users` WHERE name = '" + p.getName() + "';");
            if (rs.absolute(1)) {
                password = rs.getString("password");
            }
        } catch (Exception e) {
            return false;
        }
        String input;
        try {
            input = RASha.encrypt(pass);
        } catch (Exception e) {
            return false;
        }
        return !(password == null || input == null) && password.equals(input);
    }

    /**
     * Checks if the player has a valid session.
     *
     * @param p      Player to check for
     * @param length Length of session (in milliseconds)
     * @return true if the session is valid, false if not
     */
    public boolean isSessionValid(Player p, long length) {
        if (con == null) return false;
        long dates;
        String ip;
        String sql = "SELECT * FROM `" + prefix + "users` WHERE name = '" + p.getName() + "';";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            boolean success = rs.absolute(1);
            if (!success) return false;
            dates = rs.getLong("date");
            ip = rs.getString("ip");
        } catch (Exception e) {
            return false;
        }
        Date sDate = new Date(dates);
        Date cDate = new Date();
        String playerip = p.getAddress().getAddress().toString().replace("/", "");
        return (sDate.getTime() + length >= cDate.getTime()) && ip.equals(playerip);
    }

    /**
     * Updates the date the player logged in. Useful for extending sessions.
     *
     * @param name Name of player to update login time for
     * @param time Time player logged in (in milliseconds since Unix epoch)
     * @return true if date was changed, false if not
     */
    public boolean setLoginDate(String name, long time) {
        if (con == null) return false;
        String sql = "UPDATE `" + prefix + "users` SET date = '" + time + "' WHERE name = '" + name + "';";
        try {
            return stmt.execute(sql);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates the date the player logged in. Useful for extending sessions.
     *
     * @param p    Player to update login time for
     * @param time Time player logged in (in milliseconds since Unix epoch)
     * @return true if date was changed, false if not
     */
    public boolean setLoginDate(Player p, long time) {
        if (con == null) return false;
        String sql = "UPDATE `" + prefix + "users` SET date = '" + time + "' WHERE name = '" + p.getName() + "';";
        try {
            return stmt.execute(sql);
        } catch (Exception e) {
            return false;
        }
    }

}
