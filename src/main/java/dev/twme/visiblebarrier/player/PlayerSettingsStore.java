package dev.twme.visiblebarrier.player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.twme.visiblebarrier.config.PluginSettings;

public final class PlayerSettingsStore {
    private final JavaPlugin plugin;
    private final PluginSettings pluginSettings;
    private final File file;
    private final Map<UUID, PlayerSettings> settings = new ConcurrentHashMap<>();
    private YamlConfiguration data;

    public PlayerSettingsStore(JavaPlugin plugin, PluginSettings pluginSettings) {
        this.plugin = plugin;
        this.pluginSettings = pluginSettings;
        this.file = new File(plugin.getDataFolder(), "players.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public PlayerSettings get(Player player) {
        return get(player.getUniqueId());
    }

    public PlayerSettings get(UUID playerId) {
        return settings.computeIfAbsent(playerId, this::load);
    }

    public void save(UUID playerId) {
        PlayerSettings playerSettings = settings.get(playerId);
        if (playerSettings == null) {
            return;
        }
        String path = playerId.toString();
        data.set(path + ".enabled", playerSettings.isEnabled());
        data.set(path + ".everything", playerSettings.isEverything());
        data.set(path + ".barriers", playerSettings.isBarriers());
        data.set(path + ".lights", playerSettings.isLights());
        data.set(path + ".structure-voids", playerSettings.isStructureVoids());
        data.set(path + ".bubble-columns", playerSettings.isBubbleColumns());
        data.set(path + ".moving-pistons", playerSettings.isMovingPistons());
        data.set(path + ".visible-air", playerSettings.isVisibleAir());
        data.set(path + ".labels", playerSettings.isLabels());
        saveFile();
    }

    public void saveAll() {
        for (UUID playerId : settings.keySet()) {
            save(playerId);
        }
    }

    public void reload() {
        this.data = YamlConfiguration.loadConfiguration(file);
        this.settings.clear();
    }

    private PlayerSettings load(UUID playerId) {
        PlayerSettings playerSettings = new PlayerSettings(pluginSettings.defaults());
        ConfigurationSection section = data.getConfigurationSection(playerId.toString());
        if (section == null) {
            return playerSettings;
        }
        playerSettings.setEnabled(section.getBoolean("enabled", playerSettings.isEnabled()));
        playerSettings.setEverything(section.getBoolean("everything", playerSettings.isEverything()));
        playerSettings.setBarriers(section.getBoolean("barriers", playerSettings.isBarriers()));
        playerSettings.setLights(section.getBoolean("lights", playerSettings.isLights()));
        playerSettings.setStructureVoids(section.getBoolean("structure-voids", playerSettings.isStructureVoids()));
        playerSettings.setBubbleColumns(section.getBoolean("bubble-columns", playerSettings.isBubbleColumns()));
        playerSettings.setMovingPistons(section.getBoolean("moving-pistons", playerSettings.isMovingPistons()));
        playerSettings.setVisibleAir(section.getBoolean("visible-air", playerSettings.isVisibleAir()));
        playerSettings.setLabels(section.getBoolean("labels", playerSettings.isLabels()));
        return playerSettings;
    }

    private void saveFile() {
        try {
            data.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save player settings: " + exception.getMessage());
        }
    }
}