package dev.twme.visiblebarrier.display;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.BubbleColumn;
import org.bukkit.entity.Player;

import dev.twme.visiblebarrier.config.PluginSettings;
import dev.twme.visiblebarrier.player.PlayerSettings;

public final class BlockScanner {
    private final PluginSettings pluginSettings;

    public BlockScanner(PluginSettings pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public Map<BlockKey, OverlayTarget> scan(Player player, PlayerSettings playerSettings) {
        Map<BlockKey, OverlayTarget> targets = new LinkedHashMap<>();
        Location center = player.getLocation();
        World world = center.getWorld();
        if (world == null) {
            return targets;
        }

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        int radius = pluginSettings.clampScanRadius(playerSettings.displayRadius());
        int verticalRadius = pluginSettings.verticalRadius();

        for (int y = Math.max(world.getMinHeight(), centerY - verticalRadius); y <= Math.min(world.getMaxHeight() - 1, centerY + verticalRadius); y++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if (targets.size() >= pluginSettings.maxOverlaysPerPlayer()) {
                        return targets;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    OverlayTarget target = classify(block, playerSettings);
                    if (target != null) {
                        targets.put(target.key(), target);
                    }
                }
            }
        }

        return targets;
    }

    private OverlayTarget classify(Block block, PlayerSettings settings) {
        Material material = block.getType();

        if (material == Material.BARRIER && settings.isBarriers()) {
            return target(block, Material.BARRIER, null, Material.RED_STAINED_GLASS, "Barrier", true, settings.isLabels(), true);
        }
        if (material == Material.LIGHT && settings.isLights()) {
            String label = "Light";
            if (pluginSettings.showLightLevels() && block.getBlockData() instanceof Levelled levelled) {
                label = "Light " + levelled.getLevel();
            }
            return target(block, Material.LIGHT, block.getBlockData(), Material.YELLOW_STAINED_GLASS, label, true, settings.isLabels(), true);
        }
        if (material == Material.STRUCTURE_VOID && settings.isStructureVoids()) {
            return target(block, Material.STRUCTURE_VOID, null, Material.PURPLE_STAINED_GLASS, "Structure Void", true, settings.isLabels(), false);
        }
        if (material == Material.BUBBLE_COLUMN && settings.isBubbleColumns()) {
            String label = "Bubble Column";
            if (block.getBlockData() instanceof BubbleColumn bubbleColumn) {
                label = bubbleColumn.isDrag() ? "Bubble Down" : "Bubble Up";
            }
            return target(block, Material.WATER_BUCKET, Material.CYAN_STAINED_GLASS, label, settings.isLabels());
        }
        if (settings.isVisibleAir() && (material == Material.CAVE_AIR || material == Material.VOID_AIR)) {
            return target(block, Material.FEATHER, null, Material.WHITE_STAINED_GLASS, material == Material.CAVE_AIR ? "cave_air" : "void_air", true, settings.isLabels(), true);
        }
        if (material == Material.MOVING_PISTON && settings.isMovingPistons()) {
            return target(block, Material.PISTON, null, Material.LIME_STAINED_GLASS, "Moving Piston", true, settings.isLabels(), true);
        }
        return null;
    }

    private OverlayTarget target(Block block, Material iconMaterial, Material markerMaterial, String label, boolean showLabel) {
        return target(block, iconMaterial, null, markerMaterial, label, true, showLabel, false);
    }

    private OverlayTarget target(Block block, Material iconMaterial, BlockData iconBlockData, Material markerMaterial, String label, boolean showMarker, boolean showLabel, boolean fullSizeMarker) {
        BlockData markerBlockData = markerMaterial.createBlockData();
        return new OverlayTarget(BlockKey.of(block), block.getLocation(), safeIcon(iconMaterial), iconBlockData, markerBlockData, label, showMarker, showLabel, fullSizeMarker);
    }

    private Material safeIcon(Material material) {
        return material.isItem() ? material : Material.PAPER;
    }

}