package dev.twme.visiblebarrier.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import dev.twme.visiblebarrier.display.OverlayManager;
import dev.twme.visiblebarrier.player.PlayerSettingsStore;

public final class PlayerLifecycleListener implements Listener {
    private final OverlayManager overlayManager;
    private final PlayerSettingsStore playerSettingsStore;

    public PlayerLifecycleListener(OverlayManager overlayManager, PlayerSettingsStore playerSettingsStore) {
        this.overlayManager = overlayManager;
        this.playerSettingsStore = playerSettingsStore;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerSettingsStore.get(event.getPlayer());
        overlayManager.scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerSettingsStore.save(event.getPlayer().getUniqueId());
        overlayManager.clear(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        overlayManager.clear(event.getPlayer().getUniqueId());
        overlayManager.scheduleRefresh(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        overlayManager.clear(event.getPlayer().getUniqueId());
        overlayManager.scheduleRefresh(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        overlayManager.refreshNearby(event.getBlockPlaced().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        overlayManager.refreshNearby(event.getBlock().getLocation());
    }
}