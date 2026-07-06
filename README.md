# VisibleBarrier

VisibleBarrier is a PaperMC plugin port of the Fabric `visiblebarriers-mod`. It visualizes invisible mapmaking blocks with packet-only display entities, so overlays are only sent to players who enable them and no real display entities are spawned in the world.

## Requirements

- Paper or Folia compatible server, Java 21
- PacketEvents installed as a plugin

## Features

- Per-player overlays for barriers, light blocks, structure voids, bubble columns, moving pistons, cave air, and void air.
- Packet-only `BlockDisplay`, `ItemDisplay`, and `TextDisplay` overlays through PacketEvents and EntityLib.
- Command toggles replacing the original client keybinds.
- Optional chest menu for common visibility toggles.
- Helper command for giving technical mapmaking blocks when Paper can represent them safely.

## Commands

- `/visiblebarrier toggle [all|everything|barriers|lights|structurevoids|bubblecolumns|movingpistons|air] [on|off]`
- `/visiblebarrier show`
- `/visiblebarrier menu`
- `/visiblebarrier reload`
- `/visiblebarrier give <barrier|light|structure_void|bubble_column|moving_piston|air|cave_air|void_air> [variant] [player]`

Aliases: `/vb`, `/visiblebarriers`.

## Display Options

- `labels` toggles packet-only text labels that show block names above overlays.
- `visibleair` allows `cave_air` and `void_air` overlays when `everything` mode is enabled. These render as full-block white stained glass markers so empty mapmaking spaces are easier to locate.
- `movingpistons` controls moving piston overlays. These render as full-block lime stained glass markers.

## Permissions

- `visiblebarrier.use` - use overlays and base commands
- `visiblebarrier.menu` - open the chest GUI
- `visiblebarrier.give` - give technical mapmaking blocks
- `visiblebarrier.reload` - reload plugin configuration

## Paper Port Notes

The Fabric mod changes client rendering with mixins and resource overrides. A pure Paper plugin cannot force vanilla clients to change block render shapes, FOV zoom, fullbright, or local-only weather/time in the same way. This plugin recreates the practical mapmaking visibility workflow with packet-only display overlays instead.

Exact translucent model parity would require an optional resource pack. The default implementation uses vanilla items, stained glass marker blocks, and text labels so it works on unmodified clients.

## Build

```powershell
mvn clean package
```

The plugin jar is written to `target/VisibleBarrier-0.1.0.jar`.