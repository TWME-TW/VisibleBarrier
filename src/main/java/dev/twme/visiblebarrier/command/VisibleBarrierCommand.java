package dev.twme.visiblebarrier.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.twme.visiblebarrier.VisibleBarrierPlugin;
import dev.twme.visiblebarrier.display.OverlayManager;
import dev.twme.visiblebarrier.menu.VisibleBarrierMenu;
import dev.twme.visiblebarrier.player.PlayerSettings;
import dev.twme.visiblebarrier.player.PlayerSettingsStore;

public final class VisibleBarrierCommand implements TabExecutor {
    private static final List<String> ROOT = List.of("toggle", "show", "reload", "menu");
    private static final List<String> TOGGLES = List.of("all", "barriers", "lights", "structurevoids", "bubblecolumns", "movingpistons", "air");
    private static final List<String> BOOLEAN = List.of("on", "off", "true", "false");

    private final VisibleBarrierPlugin plugin;
    private final OverlayManager overlayManager;
    private final PlayerSettingsStore playerSettingsStore;
    private final VisibleBarrierMenu menu;

    public VisibleBarrierCommand(VisibleBarrierPlugin plugin, OverlayManager overlayManager, PlayerSettingsStore playerSettingsStore, VisibleBarrierMenu menu) {
        this.plugin = plugin;
        this.overlayManager = overlayManager;
        this.playerSettingsStore = playerSettingsStore;
        this.menu = menu;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase(Locale.ROOT);
        switch (subCommand) {
            case "toggle" -> handleToggle(sender, args);
            case "show" -> handleShow(sender);
            case "reload" -> handleReload(sender);
            case "menu" -> handleMenu(sender);
            default -> sendHelp(sender, label);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(ROOT, args[0]);
        }
        String subCommand = args[0].toLowerCase(Locale.ROOT);
        if ("toggle".equals(subCommand)) {
            if (args.length == 2) return filter(TOGGLES, args[1]);
            if (args.length == 3) return filter(BOOLEAN, args[2]);
        }
        return List.of();
    }

    private void handleToggle(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return;
        if (!player.hasPermission("visiblebarrier.use")) {
            send(player, "§cYou do not have permission to use VisibleBarrier.");
            return;
        }
        PlayerSettings settings = playerSettingsStore.get(player);
        String target = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "all";
        Boolean explicit = args.length >= 3 ? parseBoolean(args[2]) : null;

        boolean value = switch (target) {
            case "all" -> explicit != null ? explicit : !settings.isEnabled();
            case "barriers" -> explicit != null ? explicit : !settings.isBarriers();
            case "lights" -> explicit != null ? explicit : !settings.isLights();
            case "structurevoids", "structure_voids" -> explicit != null ? explicit : !settings.isStructureVoids();
            case "bubblecolumns", "bubble_columns" -> explicit != null ? explicit : !settings.isBubbleColumns();
            case "movingpistons", "moving_pistons" -> explicit != null ? explicit : !settings.isMovingPistons();
            case "air" -> explicit != null ? explicit : !settings.isVisibleAir();
            default -> {
                send(player, "§cUnknown toggle: " + target);
                yield false;
            }
        };

        switch (target) {
            case "all" -> settings.setEnabled(value);
            case "barriers" -> settings.setBarriers(value);
            case "lights" -> settings.setLights(value);
            case "structurevoids", "structure_voids" -> settings.setStructureVoids(value);
            case "bubblecolumns", "bubble_columns" -> settings.setBubbleColumns(value);
            case "movingpistons", "moving_pistons" -> settings.setMovingPistons(value);
            case "air" -> settings.setVisibleAir(value);
            default -> {
                return;
            }
        }

        playerSettingsStore.saveDebounced(player.getUniqueId());
        if (!settings.isEnabled()) {
            overlayManager.clear(player.getUniqueId());
        } else {
            overlayManager.scheduleRefresh(player);
        }
        send(player, "§7VisibleBarrier " + target + " is now " + (value ? "§aenabled" : "§cdisabled") + "§7.");
    }

    private void handleShow(CommandSender sender) {
        Player player = requirePlayer(sender);
        if (player == null) return;
        if (!player.hasPermission("visiblebarrier.use")) {
            send(player, "§cYou do not have permission to use VisibleBarrier.");
            return;
        }
        PlayerSettings settings = playerSettingsStore.get(player);
        send(player, "§6VisibleBarrier status");
        send(player, "§7Enabled: " + format(settings.isEnabled()));
        send(player, "§7Barriers: " + format(settings.isBarriers()) + " §7Lights: " + format(settings.isLights()) + " §7Structure Voids: " + format(settings.isStructureVoids()));
        send(player, "§7Bubble Columns: " + format(settings.isBubbleColumns()) + " §7Moving Pistons: " + format(settings.isMovingPistons()));
        send(player, "§7cave_air/void_air: " + format(settings.isVisibleAir()));
        send(player, "§7Labels: " + format(settings.isLabels()));
        send(player, "§7Scan radius: §f" + plugin.getPluginSettings().scanRadius() + " §7Vertical: §f" + plugin.getPluginSettings().verticalRadius());
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("visiblebarrier.reload")) {
            send(sender, "§cYou do not have permission to reload VisibleBarrier.");
            return;
        }
        plugin.getPluginSettings().reload();
        playerSettingsStore.reload();
        overlayManager.clearAll();
        overlayManager.refreshAll();
        send(sender, "§aVisibleBarrier configuration reloaded.");
    }

    private void handleMenu(CommandSender sender) {
        Player player = requirePlayer(sender);
        if (player == null) return;
        if (!player.hasPermission("visiblebarrier.menu")) {
            send(player, "§cYou do not have permission to open the VisibleBarrier menu.");
            return;
        }
        menu.open(player);
    }

    private void sendHelp(CommandSender sender, String label) {
        send(sender, "§6VisibleBarrier");
        send(sender, "§7/" + label + " toggle [all|barriers|lights|structurevoids|bubblecolumns|movingpistons|air] [on|off]");
        send(sender, "§7/" + label + " show");
        send(sender, "§7/" + label + " menu");
    }

    private Player requirePlayer(CommandSender sender) {
        if (sender instanceof Player player) {
            return player;
        }
        send(sender, "§cThis command can only be used by a player.");
        return null;
    }

    private Boolean parseBoolean(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "on", "true", "yes", "enable", "enabled" -> true;
            case "off", "false", "no", "disable", "disabled" -> false;
            default -> null;
        };
    }

    private String format(boolean value) {
        return value ? "§aon" : "§coff";
    }

    private void send(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    private List<String> filter(List<String> values, String prefix) {
        String normalized = prefix.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(normalized)) {
                matches.add(value);
            }
        }
        return matches;
    }

}