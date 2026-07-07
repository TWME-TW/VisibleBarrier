package dev.twme.visiblebarrier.player;

import dev.twme.visiblebarrier.config.PluginSettings;

public final class PlayerSettings {
    private volatile boolean enabled;
    private volatile boolean barriers;
    private volatile boolean lights;
    private volatile boolean structureVoids;
    private volatile boolean bubbleColumns;
    private volatile boolean movingPistons;
    private volatile boolean visibleAir;
    private volatile boolean labels;
    private volatile int displayRadius;

    public PlayerSettings(PluginSettings defaults) {
        PluginSettings.Defaults playerDefaults = defaults.defaults();
        this.displayRadius = defaults.scanRadius();
        this.enabled = playerDefaults.enabled();
        this.barriers = playerDefaults.barriers();
        this.lights = playerDefaults.lights();
        this.structureVoids = playerDefaults.structureVoids();
        this.bubbleColumns = playerDefaults.bubbleColumns();
        this.movingPistons = playerDefaults.movingPistons();
        this.visibleAir = playerDefaults.visibleAir();
        this.labels = playerDefaults.labels();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBarriers() {
        return barriers;
    }

    public void setBarriers(boolean barriers) {
        this.barriers = barriers;
    }

    public boolean isLights() {
        return lights;
    }

    public void setLights(boolean lights) {
        this.lights = lights;
    }

    public boolean isStructureVoids() {
        return structureVoids;
    }

    public void setStructureVoids(boolean structureVoids) {
        this.structureVoids = structureVoids;
    }

    public boolean isBubbleColumns() {
        return bubbleColumns;
    }

    public void setBubbleColumns(boolean bubbleColumns) {
        this.bubbleColumns = bubbleColumns;
    }

    public boolean isMovingPistons() {
        return movingPistons;
    }

    public void setMovingPistons(boolean movingPistons) {
        this.movingPistons = movingPistons;
    }

    public boolean isVisibleAir() {
        return visibleAir;
    }

    public void setVisibleAir(boolean visibleAir) {
        this.visibleAir = visibleAir;
    }

    public boolean isLabels() {
        return labels;
    }

    public void setLabels(boolean labels) {
        this.labels = labels;
    }

    public int displayRadius() {
        return displayRadius;
    }

    public void setDisplayRadius(int displayRadius) {
        this.displayRadius = displayRadius;
    }
}