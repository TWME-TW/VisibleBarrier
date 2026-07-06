# VisibleBarrier

VisibleBarrier is a PaperMC plugin for visualizing technical and invisible mapmaking blocks with packet-only display entities. Overlays are only sent to players who enable them, and no real display entities are spawned in the world.

## Requirements

- Paper or Folia compatible server, Java 21
- PacketEvents installed as a plugin

## Features

- Per-player overlays for barriers, light blocks, structure voids, bubble columns, moving pistons, cave air, and void air.
- Packet-only `BlockDisplay`, `ItemDisplay`, and `TextDisplay` overlays through PacketEvents and EntityLib.
- Command toggles and a chest menu for per-player visibility control.
- Exact light block levels are shown through the light item display.

## Commands

- `/visiblebarrier toggle [all|barriers|lights|structurevoids|bubblecolumns|movingpistons|air] [on|off]`
- `/visiblebarrier show`
- `/visiblebarrier menu`
- `/visiblebarrier reload`

Alias: `/vb`.

## Display Options

- `labels` toggles packet-only text labels that show block names above overlays.
- `visibleair` controls `cave_air` and `void_air` overlays. These render as full-block white stained glass markers so empty mapmaking spaces are easier to locate.
- `movingpistons` controls moving piston overlays. These render as full-block lime stained glass markers.

## Menu

`/visiblebarrier menu` opens a 27-slot chest menu with one row of toggles:

- `Barriers`
- `Lights`
- `Structure Voids`
- `Bubble Columns`
- `Moving Pistons`
- `Cave/Void Air`
- `Labels`

Every menu item has a stack size of 1. The item name shows whether that option is enabled or disabled.

## Defaults

New players start with overlays disabled globally. When enabled, the default visible categories are:

- Barriers: on
- Lights: on
- Structure voids: on
- Moving pistons: on
- Bubble columns: off
- Cave/Void Air: off
- Labels: off

## Permissions

- `visiblebarrier.use` - use overlays and base commands
- `visiblebarrier.menu` - open the chest GUI
- `visiblebarrier.reload` - reload plugin configuration

## License

VisibleBarrier is licensed under the MIT License. See [LICENSE](LICENSE).

## Build

```powershell
mvn clean package
```

The plugin jar is written to `target/VisibleBarrier-1.0.0.jar`.