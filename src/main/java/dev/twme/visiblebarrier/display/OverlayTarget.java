package dev.twme.visiblebarrier.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public record OverlayTarget(
        BlockKey key,
        Location blockLocation,
        Material iconMaterial,
        BlockData markerBlockData,
        String label,
        int glowColor) {
}