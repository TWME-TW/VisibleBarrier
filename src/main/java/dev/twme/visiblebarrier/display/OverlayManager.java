package dev.twme.visiblebarrier.display;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.twme.visiblebarrier.config.PluginSettings;
import dev.twme.visiblebarrier.player.PlayerSettings;
import dev.twme.visiblebarrier.player.PlayerSettingsStore;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import io.github.retrooper.packetevents.util.folia.TaskWrapper;

public final class OverlayManager {
    private final JavaPlugin plugin;
    private final PluginSettings pluginSettings;
    private final PlayerSettingsStore playerSettingsStore;
    private final BlockScanner blockScanner;
    private final OverlayFactory overlayFactory;
    private final Map<UUID, Map<BlockKey, OverlayEntry>> overlays = new ConcurrentHashMap<>();
    private TaskWrapper refreshTask;

    public OverlayManager(JavaPlugin plugin, PluginSettings pluginSettings, PlayerSettingsStore playerSettingsStore) {
        this.plugin = plugin;
        this.pluginSettings = pluginSettings;
        this.playerSettingsStore = playerSettingsStore;
        this.blockScanner = new BlockScanner(pluginSettings);
        this.overlayFactory = new OverlayFactory(pluginSettings);
    }

    public void start() {
        if (refreshTask != null && !refreshTask.isCancelled()) {
            return;
        }
        refreshTask = FoliaScheduler.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> refreshAll(), 20L, pluginSettings.intervalTicks());
    }

    public void shutdown() {
        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }
        clearAll();
    }

    public void refreshAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            scheduleRefresh(player);
        }
    }

    public void refreshNearby(Location location) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld()) && player.getLocation().distanceSquared(location) <= Math.pow(pluginSettings.scanRadius() + 2.0D, 2.0D)) {
                scheduleRefresh(player);
            }
        }
    }

    public void scheduleRefresh(Player player) {
        FoliaScheduler.getEntityScheduler().execute(player, plugin, () -> refreshPlayer(player), null, 1L);
    }

    public void refreshPlayer(Player player) {
        PlayerSettings settings = playerSettingsStore.get(player);
        if (!player.isOnline() || !settings.isEnabled()) {
            clear(player.getUniqueId());
            return;
        }

        Map<BlockKey, OverlayTarget> targets = blockScanner.scan(player, settings);
        Map<BlockKey, OverlayEntry> current = overlays.computeIfAbsent(player.getUniqueId(), key -> new ConcurrentHashMap<>());
        Set<BlockKey> stale = new HashSet<>(current.keySet());
        stale.removeAll(targets.keySet());
        for (BlockKey key : stale) {
            OverlayEntry removed = current.remove(key);
            if (removed != null) {
                removed.remove();
            }
        }

        for (OverlayTarget target : targets.values()) {
            current.computeIfAbsent(target.key(), key -> overlayFactory.create(target, player.getUniqueId()));
        }
    }

    public void clear(UUID playerId) {
        Map<BlockKey, OverlayEntry> playerOverlays = overlays.remove(playerId);
        if (playerOverlays == null) {
            return;
        }
        playerOverlays.values().forEach(OverlayEntry::remove);
        playerOverlays.clear();
    }

    public void clearAll() {
        Set<UUID> playerIds = Set.copyOf(overlays.keySet());
        for (UUID playerId : playerIds) {
            clear(playerId);
        }
    }
}