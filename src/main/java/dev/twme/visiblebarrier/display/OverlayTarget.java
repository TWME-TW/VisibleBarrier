package dev.twme.visiblebarrier.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public record OverlayTarget(
        BlockKey key,
        Location blockLocation,
        Material iconMaterial,
        BlockData iconBlockData,
        BlockData markerBlockData,
        String label,
        boolean showMarker,
        boolean showLabel,
        boolean glowingMarker,
        int glowColor) {
}