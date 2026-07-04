package dev.twme.visiblebarrier.display;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3f;

import dev.twme.visiblebarrier.config.PluginSettings;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.BlockDisplayMeta;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class OverlayFactory {
    private static final int FULL_BRIGHT = 15 << 4 | 15 << 20;

    private final PluginSettings pluginSettings;

    public OverlayFactory(PluginSettings pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public OverlayEntry create(OverlayTarget target, java.util.UUID viewerId) {
        List<WrapperEntity> entities = new ArrayList<>();
        if (target.showMarker()) {
            entities.add(createBlockMarker(target, viewerId));
        }
        entities.add(createItemIcon(target, viewerId));
        if (pluginSettings.showLabels() && target.showLabel()) {
            entities.add(createLabel(target, viewerId));
        }
        return new OverlayEntry(entities);
    }

    private WrapperEntity createBlockMarker(OverlayTarget target, java.util.UUID viewerId) {
        WrapperEntity entity = new WrapperEntity(EntityTypes.BLOCK_DISPLAY);
        entity.spawn(SpigotConversionUtil.fromBukkitLocation(target.blockLocation()));
        if (entity.getEntityMeta() instanceof BlockDisplayMeta meta) {
            meta.setBlockState(SpigotConversionUtil.fromBukkitBlockData(target.markerBlockData()));
            meta.setTranslation(new Vector3f(0.36f, 0.36f, 0.36f));
            meta.setScale(new Vector3f(0.28f, 0.28f, 0.28f));
            applyDisplayDefaults(meta, target.glowColor());
        }
        entity.getEntityMeta().setGlowing(true);
        entity.addViewer(viewerId);
        return entity;
    }

    private WrapperEntity createItemIcon(OverlayTarget target, java.util.UUID viewerId) {
        Location location = target.blockLocation().clone().add(0.5D, 0.72D, 0.5D);
        WrapperEntity entity = new WrapperEntity(EntityTypes.ITEM_DISPLAY);
        entity.spawn(SpigotConversionUtil.fromBukkitLocation(location));
        if (entity.getEntityMeta() instanceof ItemDisplayMeta meta) {
            Material material = target.iconMaterial().isItem() ? target.iconMaterial() : Material.PAPER;
            meta.setItem(SpigotConversionUtil.fromBukkitItemStack(new ItemStack(material)));
            meta.setDisplayType(ItemDisplayMeta.DisplayType.FIXED);
            meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setScale(new Vector3f(pluginSettings.iconScale(), pluginSettings.iconScale(), pluginSettings.iconScale()));
            applyDisplayDefaults(meta, target.glowColor());
        }
        entity.addViewer(viewerId);
        return entity;
    }

    private WrapperEntity createLabel(OverlayTarget target, java.util.UUID viewerId) {
        Location location = target.blockLocation().clone().add(0.5D, 1.16D, 0.5D);
        WrapperEntity entity = new WrapperEntity(EntityTypes.TEXT_DISPLAY);
        entity.spawn(SpigotConversionUtil.fromBukkitLocation(location));
        if (entity.getEntityMeta() instanceof TextDisplayMeta meta) {
            meta.setText(Component.text(target.label(), NamedTextColor.WHITE));
            meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setScale(new Vector3f(pluginSettings.labelScale(), pluginSettings.labelScale(), pluginSettings.labelScale()));
            meta.setSeeThrough(true);
            meta.setShadow(true);
            meta.setBackgroundColor(0xA0000000);
            applyDisplayDefaults(meta, target.glowColor());
        }
        entity.addViewer(viewerId);
        return entity;
    }

    private void applyDisplayDefaults(AbstractDisplayMeta meta, int glowColor) {
        meta.setViewRange(pluginSettings.viewRange());
        meta.setBrightnessOverride(FULL_BRIGHT);
        meta.setShadowRadius(0.0f);
        meta.setShadowStrength(0.0f);
        meta.setGlowColorOverride(glowColor);
    }
}