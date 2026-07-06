# VisibleBarrier

VisibleBarrier helps players easily see and manage hidden technical blocks like barriers, light blocks, structure voids, moving pistons, and special air blocks.

## Requirements

- Paper or Folia compatible server, Java 21
- PacketEvents installed as a plugin

## Features

- Shows clear visual markers for barriers, light blocks, structure voids, bubble columns, moving pistons, cave air, and void air.
- Lets each player choose which hidden blocks they want to see.
- Includes simple commands and a chest menu for quick control.
- Shows the correct light level for light blocks.

## Commands

- `/visiblebarrier toggle [all|barriers|lights|structurevoids|bubblecolumns|movingpistons|air] [on|off]`
- `/visiblebarrier show`
- `/visiblebarrier menu`
- `/visiblebarrier reload`

Alias: `/vb`.

## Display Options

- `labels` shows block names above markers.
- `visibleair` shows `cave_air` and `void_air` with full-block white stained glass markers.
- `movingpistons` shows moving pistons with full-block lime stained glass markers.

## Menu

`/visiblebarrier menu` opens a 27-slot chest menu with one row of toggles:

- `Barriers`
- `Lights`
- `Structure Voids`
- `Bubble Columns`
- `Moving Pistons`
- `Cave/Void Air`
- `Labels`

Each menu item shows whether that option is enabled or disabled.

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

Overlay and menu permissions default to server operators. Grant them with a permissions plugin if regular players should use VisibleBarrier.

## License

VisibleBarrier is licensed under the MIT License. See [LICENSE](LICENSE).

## Build

```powershell
mvn clean package
```

The plugin jar is written to `target/VisibleBarrier-1.0.0.jar`.