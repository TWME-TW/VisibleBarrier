package dev.twme.visiblebarrier.menu;

import java.util.Set;
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
import dev.twme.visiblebarrier.config.PluginSettings;
import dev.twme.visiblebarrier.player.PlayerSettings;
import dev.twme.visiblebarrier.player.PlayerSettingsStore;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import net.kyori.adventure.text.Component;

public final class VisibleBarrierMenu implements Listener {
    private static final Component TITLE = Component.text("VisibleBarrier");

    private final JavaPlugin plugin;
    private final OverlayManager overlayManager;
    private final PlayerSettingsStore playerSettingsStore;
    private final PluginSettings pluginSettings;
    private final Set<UUID> openMenuPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> reopeningPlayers = ConcurrentHashMap.newKeySet();
    private final java.util.Map<UUID, Inventory> openMenus = new ConcurrentHashMap<>();

    public VisibleBarrierMenu(JavaPlugin plugin, OverlayManager overlayManager, PlayerSettingsStore playerSettingsStore) {
        this.plugin = plugin;
        this.overlayManager = overlayManager;
        this.playerSettingsStore = playerSettingsStore;
        this.pluginSettings = ((dev.twme.visiblebarrier.VisibleBarrierPlugin) plugin).getPluginSettings();
    }

    public void open(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory(player, 27, TITLE);
        PlayerSettings settings = playerSettingsStore.get(player);
        inventory.setItem(10, toggleItem(Material.BARRIER, "Barriers", settings.isBarriers()));
        inventory.setItem(11, toggleItem(Material.LIGHT, "Lights", settings.isLights()));
        inventory.setItem(12, toggleItem(Material.STRUCTURE_VOID, "Structure Voids", settings.isStructureVoids()));
        inventory.setItem(13, toggleItem(Material.WATER_BUCKET, "Bubble Columns", settings.isBubbleColumns()));
        inventory.setItem(14, toggleItem(Material.PISTON, "Moving Pistons", settings.isMovingPistons()));
        inventory.setItem(15, toggleItem(Material.FEATHER, "Cave/Void Air", settings.isVisibleAir()));
        inventory.setItem(16, toggleItem(Material.NAME_TAG, "Labels", settings.isLabels()));
        inventory.setItem(20, item(Material.REDSTONE_TORCH, "Distance -1"));
        inventory.setItem(22, item(Material.SPYGLASS, "Distance: " + settings.displayRadius()));
        inventory.setItem(24, item(Material.TORCH, "Distance +1"));
        inventory.setItem(26, item(Material.COMPASS, "Reset Distance"));
        openMenus.put(playerId, inventory);
        openMenuPlayers.add(playerId);
        reopeningPlayers.add(playerId);
        try {
            player.openInventory(inventory);
        } finally {
            reopeningPlayers.remove(playerId);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        UUID playerId = player.getUniqueId();
        if (!openMenuPlayers.contains(playerId)) {
            return;
        }
        Inventory inventory = openMenus.get(playerId);
        if (inventory == null || event.getView().getTopInventory() != inventory) {
            return;
        }

        event.setCancelled(true);
        if (!player.hasPermission("visiblebarrier.menu")) {
            player.closeInventory();
            return;
        }
        if (event.getRawSlot() >= inventory.getSize()) {
            return;
        }

        PlayerSettings settings = playerSettingsStore.get(player);
        boolean recreateOverlays = false;
        switch (event.getRawSlot()) {
            case 10 -> settings.setBarriers(!settings.isBarriers());
            case 11 -> settings.setLights(!settings.isLights());
            case 12 -> settings.setStructureVoids(!settings.isStructureVoids());
            case 13 -> settings.setBubbleColumns(!settings.isBubbleColumns());
            case 14 -> settings.setMovingPistons(!settings.isMovingPistons());
            case 15 -> settings.setVisibleAir(!settings.isVisibleAir());
            case 16 -> {
                settings.setLabels(!settings.isLabels());
                recreateOverlays = true;
            }
            case 20 -> {
                settings.setDisplayRadius(pluginSettings.clampScanRadius(settings.displayRadius() - 1));
                recreateOverlays = true;
            }
            case 22 -> {
                return;
            }
            case 24 -> {
                settings.setDisplayRadius(pluginSettings.clampScanRadius(settings.displayRadius() + 1));
                recreateOverlays = true;
            }
            case 26 -> {
                settings.setDisplayRadius(pluginSettings.scanRadius());
                recreateOverlays = true;
            }
            default -> {
                return;
            }
        }
        playerSettingsStore.saveDebounced(player.getUniqueId());
        if (!settings.isEnabled()) {
            overlayManager.clear(player.getUniqueId());
        } else {
            if (recreateOverlays) {
                overlayManager.clear(player.getUniqueId());
            }
            overlayManager.scheduleRefresh(player);
        }
        FoliaScheduler.getEntityScheduler().run(player, plugin, task -> open(player), null);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (reopeningPlayers.contains(playerId)) {
            return;
        }
        openMenuPlayers.remove(playerId);
        openMenus.remove(playerId);
    }

    private ItemStack toggleItem(Material material, String name, boolean enabled) {
        return item(material, name + ": " + (enabled ? "Enabled" : "Disabled"));
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