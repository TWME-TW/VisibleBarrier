package dev.twme.visiblebarrier.display;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;

public record BlockKey(UUID worldId, int x, int y, int z, Material material, String data) {
    public static BlockKey of(Block block) {
        return new BlockKey(
                block.getWorld().getUID(),
                block.getX(),
                block.getY(),
                block.getZ(),
                block.getType(),
                block.getBlockData().getAsString(false));
    }
}