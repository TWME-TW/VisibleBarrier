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
        this.scanRadius = clamp(plugin.getConfig().getInt("scan.radius", 16), 1, 32);
        this.verticalRadius = clamp(plugin.getConfig().getInt("scan.vertical-radius", 8), 1, 32);
        this.intervalTicks = clamp(plugin.getConfig().getLong("scan.interval-ticks", 20L), 5L, 1200L);
        this.maxOverlaysPerPlayer = clamp(plugin.getConfig().getInt("scan.max-overlays-per-player", 512), 16, 2048);
        this.viewRange = clamp((float) plugin.getConfig().getDouble("display.view-range", 48.0D), 1.0f, 128.0f);
        this.iconScale = clamp((float) plugin.getConfig().getDouble("display.icon-scale", 0.55D), 0.1f, 4.0f);
        this.labelScale = clamp((float) plugin.getConfig().getDouble("display.label-scale", 0.8D), 0.1f, 4.0f);
        this.showLabels = plugin.getConfig().getBoolean("display.show-labels", true);
        this.showLightLevels = plugin.getConfig().getBoolean("display.show-light-levels", true);
        this.defaults = new Defaults(
                plugin.getConfig().getBoolean("defaults.enabled", false),
                plugin.getConfig().getBoolean("defaults.barriers", true),
                plugin.getConfig().getBoolean("defaults.lights", true),
                plugin.getConfig().getBoolean("defaults.structure-voids", true),
                plugin.getConfig().getBoolean("defaults.bubble-columns", false),
                plugin.getConfig().getBoolean("defaults.moving-pistons", true),
                plugin.getConfig().getBoolean("defaults.visible-air", false),
                plugin.getConfig().getBoolean("defaults.labels", false));
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

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static long clamp(long value, long minimum, long maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public record Defaults(boolean enabled, boolean barriers, boolean lights,
                           boolean structureVoids, boolean bubbleColumns, boolean movingPistons, boolean visibleAir,
                           boolean labels) {
    }
}