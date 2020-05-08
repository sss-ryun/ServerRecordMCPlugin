package me.pixeldroid.plugins.serverrecord.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.Scanner;
import java.util.logging.Level;

public class ServerRecorder {

    public static final String PLUGIN_CONFIG_FILE = "plugin-config.json";
    public static final int MYSQL_DEFAULT_PORT = 3306;

    Plugin plugin;

    private Connection connection;
    private String host, database, table, username, password;
    private int port;

    private boolean isConfigured = false;
    private boolean isStarted = false;

    public ServerRecorder(Plugin plugin) {
        this.plugin = plugin;

        init();
    }

    private void init() {
        File config = new File(plugin.getDataFolder().toString() + File.separator + PLUGIN_CONFIG_FILE);

        if(!config.exists()) {
            configBroken();

            return;
        }

        StringBuilder configFile = new StringBuilder();
        try {
            Scanner myReader = new Scanner(config);
            while (myReader.hasNextLine()) {
                configFile.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, e.toString());

            configBroken();

            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(configFile.toString());

            if(!jsonObject.has("host") || !jsonObject.has("database") || !jsonObject.has("table") || !jsonObject.has("username") || !jsonObject.has("password")) {
                configBroken();

                return;
            }

            this.host = jsonObject.getString("host");
            this.database = jsonObject.getString("database");
            this.table = jsonObject.getString("table");
            this.username = jsonObject.getString("username");
            this.password = jsonObject.getString("password");
            this.port = jsonObject.optInt("port", MYSQL_DEFAULT_PORT);

            isConfigured = true;

        } catch (JSONException e) {
            plugin.getLogger().log(Level.SEVERE, e.toString());

            configBroken();
        }
    }

    private void configBroken() {
        plugin.getLogger().log(Level.WARNING, "Config File is either misconfigured or missing.");

        createConfioFile();
    }

    private void createConfioFile() {
        File config = plugin.getDataFolder();

        if(!config.mkdir()) {
            plugin.getLogger().log(Level.WARNING, "Plugin Data Directory not created. It may already exist.");
        }

        config = new File(config.toString() + File.separator + PLUGIN_CONFIG_FILE);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("host", "example.com");
        jsonObject.put("database", "database");
        jsonObject.put("table", "table");
        jsonObject.put("username", "username");
        jsonObject.put("password", "password");
        jsonObject.put("port", MYSQL_DEFAULT_PORT);

        try {
            if(!config.exists()) {
                if(!config.createNewFile()) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to create a fresh config file.");
                }
            }

            FileWriter myWriter = new FileWriter(config);
            myWriter.write(jsonObject.toString());
            myWriter.close();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, e.toString());

            return;
        }

        plugin.getLogger().log(Level.WARNING, "Created a fresh config file.");
    }

    public boolean start() throws SQLException, ClassNotFoundException {
        if(isConfigured) {
            if (connection != null && !connection.isClosed()) {
                return false;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return false;
                }

                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            }

            isStarted = true;

            return true;
        } else
            return false;
    }

    public void stop() {
        isStarted = false;
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, e.toString());
        }
    }

    public void save(Actions action, Player player, JSONObject args) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(isStarted && isConfigured) {
                    try {
                        Statement statement = connection.createStatement();

                        boolean hasPlayer = (player != null);

                        String cause = hasPlayer ? "ARTIFICIAL" : "NATURAL";

                        JSONObject recordJson = new JSONObject();

                        recordJson.put("action", action.toString());
                        recordJson.put("cause", cause);

                        String playerName = "";
                        String playerUUID = "";

                        if(hasPlayer) {
                            playerName = player.getName();
                            playerUUID = player.getUniqueId().toString();
                        }

                        boolean hasPos = args.has("blockPos");

                        int x = 0;
                        int y = 0;
                        int z = 0;

                        if(hasPos) {
                            JSONObject blockPos = args.getJSONObject("blockPos");
                            x = blockPos.getInt("x");
                            y = blockPos.getInt("y");
                            z = blockPos.getInt("z");
                        }

                        JSONObject change = new JSONObject();

                        if(action.toString().startsWith("BLOCK_")) {
                            change = recordBlockUpdate(player, action, args);
                        }

                        recordJson.put("change", change);

                        statement.executeUpdate("INSERT INTO " + table + " (RECORD,PLAYERNAME,PLAYERUUID,X,Y,Z) VALUES ('" + recordJson.toString() + "','" + playerName + "','" + playerUUID + "','" + x + "','" + y + "','" + z + "');");
                    } catch (SQLException e) {
                        plugin.getLogger().log(Level.SEVERE, e.toString());
                        plugin.getLogger().log(Level.SEVERE, "An error has occured while trying to connect to the MySQL Server.");
                        plugin.getLogger().log(Level.SEVERE, "Recording is now stopped.");

                        plugin.getServer().broadcast("[SRecord] Recording stopped. Error dumped on console.", "serverrecord.log");

                        isStarted = false;
                    }
                } else {
                    plugin.getLogger().log(Level.WARNING, "Incoming record ignored.");
                }
            }
        };

        runnable.runTaskAsynchronously(plugin);
    }

    private JSONObject recordBlockUpdate(Player player, Actions action, JSONObject args) {
        switch (action) {
            case BLOCK_BREAK: {

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("brokenBlockData", args.getString("blockData"));

                return jsonObject;
            }
        }

        JSONObject def = new JSONObject();
        def.put("isNone", true);
        return def;
    }

    public void rollback(Timestamp timestamp, int radius, String playerName) {
        // TODO: Implement rollback system
    }

}
