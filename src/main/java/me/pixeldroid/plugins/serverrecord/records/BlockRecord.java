package me.pixeldroid.plugins.serverrecord.records;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class BlockRecord {

    public BlockData DATA;
    public Location LOCATION;

    public BlockRecord(BlockData blockData, Location location) {
        this.DATA = blockData;
        this.LOCATION = location;
    }
}
