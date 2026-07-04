package dev.twme.visiblebarrier.menu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import dev.twme.visiblebarrier.display.OverlayManager;
import dev.twme.visiblebarrier.player.PlayerSettings;
import dev.twme.visiblebarrier.player.PlayerSettingsStore;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import net.kyori.adventure.text.Component;

public final class VisibleBarrierMenu implements Listener {
    private static final Component TITLE = Component.text("VisibleBarrier");

    private final JavaPlugin plugin;
    private final OverlayManager overlayManager;
    private final PlayerSettingsStore playerSettingsStore;
    private final Map<UUID, Inventory> openMenus = new ConcurrentHashMap<>();

    public VisibleBarrierMenu(JavaPlugin plugin, OverlayManager overlayManager, PlayerSettingsStore playerSettingsStore) {
        this.plugin = plugin;
        this.overlayManager = overlayManager;
        this.playerSettingsStore = playerSettingsStore;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, TITLE);
        PlayerSettings settings = playerSettingsStore.get(player);
        inventory.setItem(10, toggleItem(Material.LEVER, "Enabled", settings.isEnabled()));
        inventory.setItem(11, toggleItem(Material.STRUCTURE_BLOCK, "Everything", settings.isEverything()));
        inventory.setItem(12, toggleItem(Material.BARRIER, "Barriers", settings.isBarriers()));
        inventory.setItem(13, toggleItem(Material.LIGHT, "Lights", settings.isLights()));
        inventory.setItem(14, toggleItem(Material.STRUCTURE_VOID, "Structure Voids", settings.isStructureVoids()));
        inventory.setItem(15, toggleItem(Material.WATER_BUCKET, "Bubble Columns", settings.isBubbleColumns()));
        inventory.setItem(16, toggleItem(Material.FEATHER, "Visible Air", settings.isVisibleAir()));
        inventory.setItem(22, item(Material.REDSTONE, "Refresh"));
        openMenus.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory inventory = openMenus.get(player.getUniqueId());
        if (inventory == null || !event.getInventory().equals(inventory)) {
            return;
        }
        event.setCancelled(true);
        PlayerSettings settings = playerSettingsStore.get(player);
        switch (event.getRawSlot()) {
            case 10 -> settings.setEnabled(!settings.isEnabled());
            case 11 -> settings.setEverything(!settings.isEverything());
            case 12 -> settings.setBarriers(!settings.isBarriers());
            case 13 -> settings.setLights(!settings.isLights());
            case 14 -> settings.setStructureVoids(!settings.isStructureVoids());
            case 15 -> settings.setBubbleColumns(!settings.isBubbleColumns());
            case 16 -> settings.setVisibleAir(!settings.isVisibleAir());
            case 22 -> overlayManager.scheduleRefresh(player);
            default -> {
                return;
            }
        }
        playerSettingsStore.save(player.getUniqueId());
        if (!settings.isEnabled()) {
            overlayManager.clear(player.getUniqueId());
        } else {
            overlayManager.scheduleRefresh(player);
        }
        FoliaScheduler.getEntityScheduler().run(player, plugin, task -> open(player), null);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        openMenus.remove(event.getPlayer().getUniqueId());
    }

    private ItemStack toggleItem(Material material, String name, boolean enabled) {
        ItemStack itemStack = item(material, name + ": " + (enabled ? "Enabled" : "Disabled"));
        itemStack.setAmount(enabled ? 1 : 2);
        return itemStack;
    }

    private ItemStack item(Material material, String name) {
        ItemStack itemStack = new ItemStack(material.isItem() ? material : Material.PAPER);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}