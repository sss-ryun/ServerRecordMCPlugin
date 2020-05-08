package me.pixeldroid.plugins.serverrecord;

import me.pixeldroid.plugins.serverrecord.core.ServerRecorder;
import me.pixeldroid.plugins.serverrecord.listeners.BlockListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class ServerRecordPlugin extends JavaPlugin {

    private PluginManager pluginManager;

    private ServerRecorder serverRecorder;
    private BlockListener blockListener;

    @Override
    public void onEnable() {
        pluginManager = this.getServer().getPluginManager();

        serverRecorder = new ServerRecorder(this);

        try {
            boolean isStarted = serverRecorder.start();

            if(isStarted) {
                this.getLogger().log(Level.INFO, "Plugin started.");
            } else {
                this.getLogger().log(Level.INFO, "Plugin was not started.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            this.getLogger().log(Level.INFO, "Plugin was not started.");

            Bukkit.getLogger().log(Level.SEVERE, e.toString());
        }

        blockListener = new BlockListener(serverRecorder);

        pluginManager.registerEvents(blockListener, this);
    }

    @Override
    public void onDisable() {
        serverRecorder.stop();
    }
}
