package dev.twme.visiblebarrier.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.twme.visiblebarrier.VisibleBarrierPlugin;
import dev.twme.visiblebarrier.display.OverlayManager;
import dev.twme.visiblebarrier.item.TechnicalBlockService;
import dev.twme.visiblebarrier.menu.VisibleBarrierMenu;
import dev.twme.visiblebarrier.player.PlayerSettings;
import dev.twme.visiblebarrier.player.PlayerSettingsStore;

public final class VisibleBarrierCommand implements TabExecutor {
    private static final List<String> ROOT = List.of("toggle", "settings", "show", "reload", "give", "place", "menu");
    private static final List<String> TOGGLES = List.of("all", "everything", "barriers", "lights", "structurevoids", "bubblecolumns", "air");
    private static final List<String> SETTINGS = List.of("sendfeedback", "hideparticles", "solidlights", "visibleair");
    private static final List<String> BOOLEAN = List.of("on", "off", "true", "false");
    private static final List<String> ITEMS = List.of("barrier", "light", "structure_void", "bubble_column", "moving_piston", "air", "cave_air", "void_air", "end_portal", "end_gateway");

    private final VisibleBarrierPlugin plugin;
    private final OverlayManager overlayManager;
    private final PlayerSettingsStore playerSettingsStore;
    private final VisibleBarrierMenu menu;
    private final TechnicalBlockService technicalBlockService = new TechnicalBlockService();

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
            case "settings", "setting" -> handleSettings(sender, args);
            case "show" -> handleShow(sender);
            case "reload" -> handleReload(sender);
            case "give" -> handleGive(sender, args);
            case "place" -> handlePlace(sender, args);
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
        if ("settings".equals(subCommand) || "setting".equals(subCommand)) {
            if (args.length == 2) return filter(SETTINGS, args[1]);
            if (args.length == 3) return filter(BOOLEAN, args[2]);
        }
        if ("give".equals(subCommand) || "place".equals(subCommand)) {
            if (args.length == 2) return filter(ITEMS, args[1]);
            if (args.length == 3) return filter(List.of("up", "down", "normal", "sticky", "15"), args[2]);
            if (args.length == 4 && "give".equals(subCommand)) return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[3]);
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
            case "everything" -> explicit != null ? explicit : !settings.isEverything();
            case "barriers" -> explicit != null ? explicit : !settings.isBarriers();
            case "lights" -> explicit != null ? explicit : !settings.isLights();
            case "structurevoids", "structure_voids" -> explicit != null ? explicit : !settings.isStructureVoids();
            case "bubblecolumns", "bubble_columns" -> explicit != null ? explicit : !settings.isBubbleColumns();
            case "air" -> explicit != null ? explicit : !settings.isVisibleAir();
            default -> {
                send(player, "§cUnknown toggle: " + target);
                yield false;
            }
        };

        switch (target) {
            case "all" -> settings.setEnabled(value);
            case "everything" -> settings.setEverything(value);
            case "barriers" -> settings.setBarriers(value);
            case "lights" -> settings.setLights(value);
            case "structurevoids", "structure_voids" -> settings.setStructureVoids(value);
            case "bubblecolumns", "bubble_columns" -> settings.setBubbleColumns(value);
            case "air" -> settings.setVisibleAir(value);
            default -> {
                return;
            }
        }

        playerSettingsStore.save(player.getUniqueId());
        if (!settings.isEnabled()) {
            overlayManager.clear(player.getUniqueId());
        } else {
            overlayManager.scheduleRefresh(player);
        }
        send(player, "§7VisibleBarrier " + target + " is now " + (value ? "§aenabled" : "§cdisabled") + "§7.");
    }

    private void handleSettings(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return;
        if (!player.hasPermission("visiblebarrier.settings")) {
            send(player, "§cYou do not have permission to change VisibleBarrier settings.");
            return;
        }
        if (args.length < 2) {
            send(player, "§7Usage: /visiblebarrier settings <sendfeedback|hideparticles|solidlights|visibleair> [on|off]");
            return;
        }

        PlayerSettings settings = playerSettingsStore.get(player);
        String setting = args[1].toLowerCase(Locale.ROOT);
        Boolean explicit = args.length >= 3 ? parseBoolean(args[2]) : null;
        SettingAccess access = settingAccess(setting);
        if (access == null) {
            send(player, "§cUnknown setting: " + setting);
            return;
        }
        boolean value = explicit != null ? explicit : !access.get().apply(settings);
        access.set().accept(settings, value);
        playerSettingsStore.save(player.getUniqueId());
        overlayManager.scheduleRefresh(player);
        send(player, "§7VisibleBarrier setting " + setting + " is now " + (value ? "§aenabled" : "§cdisabled") + "§7.");
    }

    private void handleShow(CommandSender sender) {
        Player player = requirePlayer(sender);
        if (player == null) return;
        PlayerSettings settings = playerSettingsStore.get(player);
        send(player, "§6VisibleBarrier settings");
        send(player, "§7Enabled: " + format(settings.isEnabled()) + " §7Everything: " + format(settings.isEverything()));
        send(player, "§7Barriers: " + format(settings.isBarriers()) + " §7Lights: " + format(settings.isLights()) + " §7Structure Voids: " + format(settings.isStructureVoids()));
        send(player, "§7Bubble Columns: " + format(settings.isBubbleColumns()) + " §7Visible Air: " + format(settings.isVisibleAir()));
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

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("visiblebarrier.give")) {
            send(sender, "§cYou do not have permission to give VisibleBarrier blocks.");
            return;
        }
        if (args.length < 2) {
            send(sender, "§7Usage: /visiblebarrier give <item> [variant] [player]");
            return;
        }
        Player target = args.length >= 4 ? Bukkit.getPlayerExact(args[3]) : requirePlayer(sender);
        if (target == null) {
            send(sender, "§cPlayer not found.");
            return;
        }
        if (!technicalBlockService.give(target, args[1], args.length >= 3 ? args[2] : "")) {
            send(sender, "§cCannot give that block as an item. Try /visiblebarrier place instead.");
            return;
        }
        send(sender, "§aGave " + args[1] + " to " + target.getName() + ".");
    }

    private void handlePlace(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return;
        if (!player.hasPermission("visiblebarrier.give")) {
            send(player, "§cYou do not have permission to place VisibleBarrier technical blocks.");
            return;
        }
        if (args.length < 2) {
            send(player, "§7Usage: /visiblebarrier place <item> [variant]");
            return;
        }
        if (!technicalBlockService.placeTarget(player, args[1], args.length >= 3 ? args[2] : "")) {
            send(player, "§cCould not place that technical block at your target.");
            return;
        }
        overlayManager.scheduleRefresh(player);
        send(player, "§aPlaced " + args[1] + ".");
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
        send(sender, "§7/" + label + " toggle [all|barriers|lights|structurevoids|bubblecolumns|air] [on|off]");
        send(sender, "§7/" + label + " settings <sendfeedback|hideparticles|solidlights|visibleair> [on|off]");
        send(sender, "§7/" + label + " show");
        send(sender, "§7/" + label + " menu");
        send(sender, "§7/" + label + " give <item> [variant] [player]");
        send(sender, "§7/" + label + " place <item> [variant]");
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

    private SettingAccess settingAccess(String setting) {
        return switch (setting) {
            case "sendfeedback", "send_feedback" -> new SettingAccess(PlayerSettings::isSendFeedback, PlayerSettings::setSendFeedback);
            case "hideparticles", "hide_particles" -> new SettingAccess(PlayerSettings::isHideParticles, PlayerSettings::setHideParticles);
            case "solidlights", "solid_lights" -> new SettingAccess(PlayerSettings::isSolidLights, PlayerSettings::setSolidLights);
            case "visibleair", "visible_air" -> new SettingAccess(PlayerSettings::isVisibleAir, PlayerSettings::setVisibleAir);
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

    private record SettingAccess(Function<PlayerSettings, Boolean> get, BiConsumer<PlayerSettings, Boolean> set) {
    }
}