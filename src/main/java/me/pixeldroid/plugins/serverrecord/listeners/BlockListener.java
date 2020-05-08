package me.pixeldroid.plugins.serverrecord.listeners;

import me.pixeldroid.plugins.serverrecord.core.ServerRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

    ServerRecorder serverRecorder;

    public BlockListener(ServerRecorder serverRecorder) {
        this.serverRecorder = serverRecorder;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {

    }
}
