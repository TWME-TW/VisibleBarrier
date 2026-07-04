# VisibleBarrier

VisibleBarrier is a PaperMC plugin port of the Fabric `visiblebarriers-mod`. It visualizes invisible mapmaking blocks with packet-only display entities, so overlays are only sent to players who enable them and no real display entities are spawned in the world.

## Requirements

- Paper or Folia compatible server, Java 21
- PacketEvents installed as a plugin

## Features

- Per-player overlays for barriers, light blocks, structure voids, and bubble columns.
- Packet-only `BlockDisplay`, `ItemDisplay`, and `TextDisplay` overlays through PacketEvents and EntityLib.
- Command toggles replacing the original client keybinds.
- Optional chest menu for common visibility toggles.
- Helper commands for giving or placing technical mapmaking blocks when Paper can represent them safely.

## Commands

- `/visiblebarrier toggle [all|everything|barriers|lights|structurevoids|bubblecolumns|air] [on|off]`
- `/visiblebarrier settings <sendfeedback|solidlights|visibleair> [on|off]`
- `/visiblebarrier show`
- `/visiblebarrier menu`
- `/visiblebarrier reload`
- `/visiblebarrier give <barrier|light|structure_void|bubble_column|moving_piston|air|cave_air|void_air|end_portal|end_gateway> [variant] [player]`
- `/visiblebarrier place <barrier|light|structure_void|bubble_column|moving_piston|air|cave_air|void_air|end_portal|end_gateway> [variant]`

Aliases: `/vb`, `/visiblebarriers`.

## Settings

- `sendfeedback` stores the player preference for command feedback.
- `visibleair` allows cave air and void air overlays when `everything` mode is enabled.
- `solidlights` makes light block overlays include a small stained-glass block marker; when disabled, lights render as icon and label overlays only.

## Permissions

- `visiblebarrier.use` - use overlays and base commands
- `visiblebarrier.settings` - change personal settings
- `visiblebarrier.menu` - open the chest GUI
- `visiblebarrier.give` - give/place technical mapmaking blocks
- `visiblebarrier.reload` - reload plugin configuration

## Paper Port Notes

The Fabric mod changes client rendering with mixins and resource overrides. A pure Paper plugin cannot force vanilla clients to change block render shapes, FOV zoom, fullbright, or local-only weather/time in the same way. This plugin recreates the practical mapmaking visibility workflow with packet-only display overlays instead.

Exact translucent model parity would require an optional resource pack. The default implementation uses vanilla items, stained glass marker blocks, and text labels so it works on unmodified clients.

## Build

```powershell
mvn clean package
```

The plugin jar is written to `target/VisibleBarrier-0.1.0.jar`.