package dev.twme.visiblebarrier.display;

import java.util.List;

import me.tofaa.entitylib.wrapper.WrapperEntity;

public final class OverlayEntry {
    private final List<WrapperEntity> entities;

    public OverlayEntry(List<WrapperEntity> entities) {
        this.entities = List.copyOf(entities);
    }

    public void remove() {
        entities.forEach(WrapperEntity::remove);
    }
}