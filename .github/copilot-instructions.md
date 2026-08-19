# Game-2018 Copilot Instructions

This is a Pokemon-inspired tile-based game engine built with Java/Maven and Swing. The codebase is organized into distinct modules with clear separation of concerns.

## Architecture Overview

**Three-Module Structure:**
- `game-engine`: Core game logic (maps, entities, movement) - no UI dependencies
- `game-ui`: Swing-based rendering and keyboard input handling
- `pokemon-clone-core`: Application entry point that wires game-engine + game-ui

**Data Flow:** Main.java → GameGUI (initializes MapCreator/MenuCreator) → GameManager singleton holds shared state

## Module Responsibilities

### game-engine (Core Logic)
- **Map System**: `GameMap` holds a 2D grid of `Unit` objects (coordinate-based, bounds-checked)
- **Entity Hierarchy**: `Unit` base class; `Player`, `Space`, `Obstacle`, `Door`, `NPC` are subclasses
- **Player Movement**: `Player.move(Direction)` validates moves, throws `InvalidMoveException` on collisions
- **Configuration**: `MapCreator` parses world files (see map format below)
- **Menu System**: `MenuCreator` builds hierarchical menus; `Menu` holds `MenuItem` objects

### game-ui (Rendering & Input)
- **GameScreen**: Swing `JPanel` that paints maps/menus; delegates input to `InputHandler`
- **InputHandler**: Encapsulates all keyboard input processing and game mode state management (handles movement, menu navigation, mode transitions)
- **Camera System**: Player-fixed viewport shows 5x5 tile area around player (not full map)
- **Image Resources**: `ImageResources` class manages sprite loading (playerFront.png, brick.png, etc.)
- **Rendering**: `paintMapPlayerFixed()`, `paintMenu()`, `paintBattleMenu()` handle visual output

## Critical Patterns

### Singleton Pattern
`GameManager`, `MapCreator`, `MenuCreator` all use static getInstance() with lazy initialization. This means:
- State persists across the app lifetime
- No dependency injection - access via static calls
- **Thread-unsafe** if ever multi-threaded

### Map File Format (world3.txt)
Each line is a command: `[type][mapId][x][y][optional...]`
```
m0[xy]        # Create map with ID 0, dimensions x*y
s0xy          # Add Space (walkable) at (x,y) in map 0
o0xy          # Add Obstacle at (x,y)
d0xy[targetMapId][targetX][targetY]  # Door to another map
p0xy          # Player starting position
n0xy          # NPC at (x,y)
```

### Exception Strategy
- `InvalidMoveException`: Thrown by `Player.move()` on collision
- `InvalidMapSetupException`: Thrown by `GameMap.add()` when position already occupied

### Testing Conventions
- Use JUnit 4 (@Before, @Test, @Test(expected=...))
- Tests in `game-engine/src/test/` parallel source structure
- MapTest/DoorTest verify movement rules and boundary conditions
- Don't test rendering (GameScreen) - complex Swing initialization

## Build & Run

```bash
# Build all modules
mvn clean package

# Run from pokemon-clone-core
java -jar pokemon-clone-core/target/pokemon-clone-core-0.0.1-SNAPSHOT.jar
# (Or directly: Main.java loads world3.txt from resources)
```

**Maven Versions:** Java 11 compiler, JUnit 4.13, Commons Lang 3.10

## Integration Points

1. **GameGUI Constructor**: Calls `MapCreator.getInstance().generateMap(world)` then `MenuCreator.getInstance().generateMenu()` - these populate GameManager singleton
2. **Input Processing**: `GameScreen.TAdapter` → `InputHandler.handleInput(keyCode)` processes keyboard events and updates game state (movement, mode switching)
3. **InputHandler State**: Manages `gameMode`, `activeMenu`, and returns display messages for GameScreen to render
4. **Cross-Module References**: pokemon-clone-core → game-ui → game-engine (unidirectional dependency)

## Common Tasks

**Adding a new entity type**: 
- Create class extending `Unit` in game-engine/map/
- Add to `MapCreator.generateMap()` parser (e.g., `line.charAt(0) == 'x'`)
- Add image resource to game-ui resources/

**Adding menu items**:
- Edit `MenuCreator.generateMenu()` to call `mainMenu.add("Label", new MenuItem())`
- Menu hierarchy is built recursively (Menu.add can take another Menu)

**Fixing movement bugs**:
- Check `Player.move()` logic and `Unit.getMap()` state
- Verify `GameMap.get(x, y)` bounds checking (returns null outside bounds, not exception)
- Test with MapTest/DoorTest patterns

## Game Mode System

**Three Modes (managed by InputHandler):**
- **WORLD_MAP**: Explore the world with arrow keys (UP/DOWN/LEFT/RIGHT), press SPACE to engage NPCs, P to open menu, B for battle mode
- **MENU**: Navigate main menu with arrow keys, RIGHT to open submenus, SPACE to select, LEFT to go back, P to return to world
- **BATTLE**: Similar controls to MENU mode with submenus, but visually distinct (displays "=== BATTLE ===" header), B key to exit back to world

**Input Mapping by Mode (defined in InputHandler):**
```
WORLD_MAP:
  Arrow Keys       → Player movement (UP/DOWN/LEFT/RIGHT via player.moveUp/Down/Left/Right)
  SPACE            → Engage with NPC/object (player.engage())
  P                → Open main menu (enterMenuMode())
  B                → Enter battle mode (enterBattleMode())

MENU/BATTLE:
  UP/DOWN          → Navigate menu items (activeMenu.up/down())
  LEFT/RIGHT       → Go back / open submenu (returnFromMenu / activeMenu.accessSelected)
  SPACE            → Select/trigger menu item (activeMenu.triggerSelected())
  P (MENU only)    → Return to world map
  B (BATTLE only)  → Return to world map
  LEFT at root     → Auto-return to world map (returns to WORLD_MAP mode)
```

**Mode Switching Logic (InputHandler methods):**
- `enterMenuMode()` / `enterBattleMode()` reset to main menu via `resetMenuToMain()`
- `returnFromMenu()` safely handles navigating back through menu hierarchy
- Left arrow at root menu level automatically returns to WORLD_MAP (throws `InvalidMoveException` which is caught)

## Known Quirks

- No game loop; repainting triggered by keyboard input only (AWT event-driven)
- World files use single-digit coordinates (0-9) and map IDs
- TextGame2/ folder is an earlier iteration - reference only, not maintained
