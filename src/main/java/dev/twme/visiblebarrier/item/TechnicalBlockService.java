package dev.twme.visiblebarrier.item;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.BubbleColumn;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class TechnicalBlockService {
    public boolean give(Player player, String itemName, String variant) {
        Material material = materialFor(itemName);
        if (material == null || !material.isItem()) {
            return false;
        }
        ItemStack stack = new ItemStack(material);
        player.getInventory().addItem(stack).values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
        return true;
    }

    public boolean placeTarget(Player player, String itemName, String variant) {
        Block target = player.getTargetBlockExact(8);
        if (target == null) {
            return false;
        }
        Material material = materialFor(itemName);
        if (material == null || !material.isBlock()) {
            return false;
        }
        try {
            BlockData data = blockDataFor(material, variant, player);
            target.setBlockData(data, false);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public Material materialFor(String itemName) {
        return switch (normalize(itemName)) {
            case "barrier" -> Material.BARRIER;
            case "light" -> Material.LIGHT;
            case "structurevoid", "structure_void" -> Material.STRUCTURE_VOID;
            case "bubblecolumn", "bubble_column" -> Material.BUBBLE_COLUMN;
            case "movingpiston", "moving_piston" -> Material.MOVING_PISTON;
            case "air" -> Material.AIR;
            case "caveair", "cave_air" -> Material.CAVE_AIR;
            case "voidair", "void_air" -> Material.VOID_AIR;
            case "endportal", "end_portal" -> Material.END_PORTAL;
            case "endgateway", "end_gateway" -> Material.END_GATEWAY;
            default -> null;
        };
    }

    private BlockData blockDataFor(Material material, String variant, Player player) {
        BlockData data = material.createBlockData();
        if (data instanceof Levelled levelled) {
            int level = parseInt(variant, levelled.getMaximumLevel());
            levelled.setLevel(Math.max(levelled.getMinimumLevel(), Math.min(levelled.getMaximumLevel(), level)));
        }
        if (data instanceof BubbleColumn bubbleColumn) {
            bubbleColumn.setDrag(!"up".equalsIgnoreCase(variant));
        }
        if (data instanceof TechnicalPiston technicalPiston) {
            technicalPiston.setFacing(faceFromPlayer(player));
            if ("sticky".equalsIgnoreCase(variant)) {
                technicalPiston.setType(TechnicalPiston.Type.STICKY);
            } else {
                technicalPiston.setType(TechnicalPiston.Type.NORMAL);
            }
        }
        return data;
    }

    private BlockFace faceFromPlayer(Player player) {
        return player.getFacing().getOppositeFace();
    }

    private int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private String normalize(String itemName) {
        if (itemName == null) {
            return "";
        }
        return itemName.toLowerCase(Locale.ROOT).replace("-", "_");
    }
}