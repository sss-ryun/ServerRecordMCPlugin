package me.pixeldroid.plugins.serverrecord.listeners;

import me.pixeldroid.plugins.serverrecord.core.Actions;
import me.pixeldroid.plugins.serverrecord.core.ServerRecorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.json.JSONObject;

import java.util.logging.Level;

public class BlockListener implements Listener {

    ServerRecorder serverRecorder;

    public BlockListener(ServerRecorder serverRecorder) {
        this.serverRecorder = serverRecorder;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBreakBlock(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            Block block = event.getBlock();
            Player player = event.getPlayer();

            BlockData blockData = block.getBlockData();
            String blockDataAsString = blockData.getAsString();

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("blockData", blockDataAsString);

            JSONObject blockPos = new JSONObject();

            Location blockLocation = block.getLocation();

            blockPos.put("x", blockLocation.getBlockX());
            blockPos.put("y", blockLocation.getBlockY());
            blockPos.put("z", blockLocation.getBlockZ());

            jsonObject.put("blockPos", blockPos);

            serverRecorder.save(Actions.BLOCK_BREAK, player, jsonObject);
        }
    }
}
