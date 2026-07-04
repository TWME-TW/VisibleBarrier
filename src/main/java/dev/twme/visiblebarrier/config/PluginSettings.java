package dev.twme.visiblebarrier.config;

import org.bukkit.plugin.java.JavaPlugin;

public final class PluginSettings {
    private final JavaPlugin plugin;
    private int scanRadius;
    private int verticalRadius;
    private long intervalTicks;
    private int maxOverlaysPerPlayer;
    private float viewRange;
    private float iconScale;
    private float labelScale;
    private boolean showLabels;
    private boolean showLightLevels;
    private Defaults defaults;

    public PluginSettings(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
        this.scanRadius = Math.max(1, plugin.getConfig().getInt("scan.radius", 16));
        this.verticalRadius = Math.max(1, plugin.getConfig().getInt("scan.vertical-radius", 8));
        this.intervalTicks = Math.max(5L, plugin.getConfig().getLong("scan.interval-ticks", 20L));
        this.maxOverlaysPerPlayer = Math.max(16, plugin.getConfig().getInt("scan.max-overlays-per-player", 512));
        this.viewRange = (float) plugin.getConfig().getDouble("display.view-range", 48.0D);
        this.iconScale = (float) plugin.getConfig().getDouble("display.icon-scale", 0.55D);
        this.labelScale = (float) plugin.getConfig().getDouble("display.label-scale", 0.8D);
        this.showLabels = plugin.getConfig().getBoolean("display.show-labels", true);
        this.showLightLevels = plugin.getConfig().getBoolean("display.show-light-levels", true);
        this.defaults = new Defaults(
                plugin.getConfig().getBoolean("defaults.enabled", false),
                plugin.getConfig().getBoolean("defaults.everything", false),
                plugin.getConfig().getBoolean("defaults.barriers", true),
                plugin.getConfig().getBoolean("defaults.lights", true),
                plugin.getConfig().getBoolean("defaults.structure-voids", true),
                plugin.getConfig().getBoolean("defaults.bubble-columns", true),
                plugin.getConfig().getBoolean("defaults.visible-air", false),
                plugin.getConfig().getBoolean("defaults.send-feedback", true),
                plugin.getConfig().getBoolean("defaults.solid-lights", false));
    }

    public int scanRadius() {
        return scanRadius;
    }

    public int verticalRadius() {
        return verticalRadius;
    }

    public long intervalTicks() {
        return intervalTicks;
    }

    public int maxOverlaysPerPlayer() {
        return maxOverlaysPerPlayer;
    }

    public float viewRange() {
        return viewRange;
    }

    public float iconScale() {
        return iconScale;
    }

    public float labelScale() {
        return labelScale;
    }

    public boolean showLabels() {
        return showLabels;
    }

    public boolean showLightLevels() {
        return showLightLevels;
    }

    public Defaults defaults() {
        return defaults;
    }

    public record Defaults(boolean enabled, boolean everything, boolean barriers, boolean lights,
                           boolean structureVoids, boolean bubbleColumns, boolean visibleAir,
                           boolean sendFeedback, boolean solidLights) {
    }
}