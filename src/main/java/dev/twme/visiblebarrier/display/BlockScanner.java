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
        int radius = pluginSettings.scanRadius();
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
        boolean everything = settings.isEverything();

        if (material == Material.BARRIER && (everything || settings.isBarriers())) {
            return target(block, Material.BARRIER, Material.RED_STAINED_GLASS, "Barrier", 0xFF5555);
        }
        if (material == Material.LIGHT && (everything || settings.isLights())) {
            String label = "Light";
            if (block.getBlockData() instanceof Levelled levelled) {
                label = "Light " + levelled.getLevel();
            }
            return target(block, Material.LIGHT, Material.YELLOW_STAINED_GLASS, label, settings.isSolidLights(), pluginSettings.showLightLevels(), 0xFFFF55);
        }
        if (material == Material.STRUCTURE_VOID && (everything || settings.isStructureVoids())) {
            return target(block, Material.STRUCTURE_VOID, Material.PURPLE_STAINED_GLASS, "Structure Void", 0xAA55FF);
        }
        if (material == Material.BUBBLE_COLUMN && (everything || settings.isBubbleColumns())) {
            String label = "Bubble Column";
            if (block.getBlockData() instanceof BubbleColumn bubbleColumn) {
                label = bubbleColumn.isDrag() ? "Bubble Down" : "Bubble Up";
            }
            return target(block, Material.WATER_BUCKET, Material.CYAN_STAINED_GLASS, label, 0x55FFFF);
        }
        if (everything && settings.isVisibleAir() && (material == Material.CAVE_AIR || material == Material.VOID_AIR)) {
            return target(block, Material.FEATHER, Material.WHITE_STAINED_GLASS, material == Material.CAVE_AIR ? "Cave Air" : "Void Air", 0xFFFFFF);
        }
        if (everything && (material == Material.END_PORTAL || material == Material.END_GATEWAY || material == Material.MOVING_PISTON)) {
            return target(block, iconForTechnical(material), Material.LIME_STAINED_GLASS, labelForTechnical(material), 0x55FF55);
        }
        return null;
    }

    private OverlayTarget target(Block block, Material iconMaterial, Material markerMaterial, String label, int glowColor) {
        return target(block, iconMaterial, markerMaterial, label, true, true, glowColor);
    }

    private OverlayTarget target(Block block, Material iconMaterial, Material markerMaterial, String label, boolean showMarker, boolean showLabel, int glowColor) {
        BlockData markerBlockData = markerMaterial.createBlockData();
        return new OverlayTarget(BlockKey.of(block), block.getLocation(), safeIcon(iconMaterial), markerBlockData, label, showMarker, showLabel, glowColor);
    }

    private Material safeIcon(Material material) {
        return material.isItem() ? material : Material.PAPER;
    }

    private Material iconForTechnical(Material material) {
        return switch (material) {
            case END_PORTAL -> Material.ENDER_EYE;
            case END_GATEWAY -> Material.END_CRYSTAL;
            case MOVING_PISTON -> Material.PISTON;
            default -> Material.PAPER;
        };
    }

    private String labelForTechnical(Material material) {
        return switch (material) {
            case END_PORTAL -> "End Portal";
            case END_GATEWAY -> "End Gateway";
            case MOVING_PISTON -> "Moving Piston";
            default -> material.name();
        };
    }
}