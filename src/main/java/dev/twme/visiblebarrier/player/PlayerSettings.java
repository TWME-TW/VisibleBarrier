package dev.twme.visiblebarrier.player;

import dev.twme.visiblebarrier.config.PluginSettings;

public final class PlayerSettings {
    private boolean enabled;
    private boolean everything;
    private boolean barriers;
    private boolean lights;
    private boolean structureVoids;
    private boolean bubbleColumns;
    private boolean movingPistons;
    private boolean visibleAir;
    private boolean labels;

    public PlayerSettings(PluginSettings.Defaults defaults) {
        this.enabled = defaults.enabled();
        this.everything = defaults.everything();
        this.barriers = defaults.barriers();
        this.lights = defaults.lights();
        this.structureVoids = defaults.structureVoids();
        this.bubbleColumns = defaults.bubbleColumns();
        this.movingPistons = defaults.movingPistons();
        this.visibleAir = defaults.visibleAir();
        this.labels = defaults.labels();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEverything() {
        return everything;
    }

    public void setEverything(boolean everything) {
        this.everything = everything;
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
}