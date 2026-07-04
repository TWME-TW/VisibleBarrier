package dev.twme.visiblebarrier;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;

import dev.twme.visiblebarrier.command.VisibleBarrierCommand;
import dev.twme.visiblebarrier.config.PluginSettings;
import dev.twme.visiblebarrier.display.OverlayManager;
import dev.twme.visiblebarrier.listener.PlayerLifecycleListener;
import dev.twme.visiblebarrier.menu.VisibleBarrierMenu;
import dev.twme.visiblebarrier.player.PlayerSettingsStore;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;

public final class VisibleBarrierPlugin extends JavaPlugin {
    private PluginSettings pluginSettings;
    private PlayerSettingsStore playerSettingsStore;
    private OverlayManager overlayManager;
    private VisibleBarrierMenu menu;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .debug(false)
                .checkForUpdates(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        EntityLib.init(new SpigotEntityLibPlatform(this), new APIConfig(PacketEvents.getAPI()).usePlatformLogger());

        saveDefaultConfig();
        this.pluginSettings = new PluginSettings(this);
        this.pluginSettings.reload();
        this.playerSettingsStore = new PlayerSettingsStore(this, pluginSettings);
        this.overlayManager = new OverlayManager(this, pluginSettings, playerSettingsStore);
        this.menu = new VisibleBarrierMenu(this, overlayManager, playerSettingsStore);

        VisibleBarrierCommand command = new VisibleBarrierCommand(this, overlayManager, playerSettingsStore, menu);
        PluginCommand pluginCommand = getCommand("visiblebarrier");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        getServer().getPluginManager().registerEvents(new PlayerLifecycleListener(overlayManager, playerSettingsStore), this);
        getServer().getPluginManager().registerEvents(menu, this);
        overlayManager.start();

        getLogger().info("VisibleBarrier enabled");
    }

    @Override
    public void onDisable() {
        if (overlayManager != null) {
            overlayManager.shutdown();
        }
        if (playerSettingsStore != null) {
            playerSettingsStore.saveAll();
        }
        getLogger().info("VisibleBarrier disabled");
    }

    public PluginSettings getPluginSettings() {
        return pluginSettings;
    }

    public PlayerSettingsStore getPlayerSettingsStore() {
        return playerSettingsStore;
    }

    public OverlayManager getOverlayManager() {
        return overlayManager;
    }
}