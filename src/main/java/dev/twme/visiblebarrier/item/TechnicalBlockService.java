package dev.twme.visiblebarrier.item;

import java.util.Locale;

import org.bukkit.Material;
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
            default -> null;
        };
    }

    private String normalize(String itemName) {
        if (itemName == null) {
            return "";
        }
        return itemName.toLowerCase(Locale.ROOT).replace("-", "_");
    }
}