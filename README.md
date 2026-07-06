# VisibleBarrier

VisibleBarrier is a PaperMC plugin for visualizing technical and invisible mapmaking blocks with packet-only display entities. Overlays are only sent to players who enable them, and no real display entities are spawned in the world.

## Requirements

- Paper or Folia compatible server, Java 21
- PacketEvents installed as a plugin

## Features

- Per-player overlays for barriers, light blocks, structure voids, bubble columns, moving pistons, cave air, and void air.
- Packet-only `BlockDisplay`, `ItemDisplay`, and `TextDisplay` overlays through PacketEvents and EntityLib.
- Command toggles and a chest menu for per-player visibility control.
- Optional chest menu for common visibility toggles.

## Commands

- `/visiblebarrier toggle [all|everything|barriers|lights|structurevoids|bubblecolumns|movingpistons|air] [on|off]`
- `/visiblebarrier show`
- `/visiblebarrier menu`
- `/visiblebarrier reload`

Alias: `/vb`.

## Display Options

- `labels` toggles packet-only text labels that show block names above overlays.
- `visibleair` allows `cave_air` and `void_air` overlays when `everything` mode is enabled. These render as full-block white stained glass markers so empty mapmaking spaces are easier to locate.
- `movingpistons` controls moving piston overlays. These render as full-block lime stained glass markers.

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

The plugin jar is written to `target/VisibleBarrier-0.1.0.jar`.