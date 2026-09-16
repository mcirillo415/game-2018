# Game-2018 Copilot Instructions

This is a Pokemon-inspired tile-based game engine built with Java/Maven and Swing. The codebase is organized into distinct modules with clear separation of concerns.

## Architecture Overview

**Three-Module Structure:**
- `game-engine`: Core game logic (maps, entities, movement, battles) - no UI dependencies
- `game-ui`: Swing-based rendering (`GameScreen`) and keyboard input handling (`GameController`)
- `pokemon-clone-core`: Application entry point that wires game-engine + game-ui

**Data Flow:** Main.java → GameGUI (initializes MapCreator/MenuCreator, then adds a GameScreen) → GameManager singleton holds shared state

## Module Responsibilities

### game-engine (Core Logic)
- **Map System**: `GameMap` holds a 2D grid of `Unit` objects (coordinate-based, bounds-checked)
- **Entity Hierarchy**: `Unit` base class; `Player`, `Space`, `Obstacle`, `Door`, `WildGrass`, `NPC` are subclasses
- **Player Movement**: `Player.move(Direction)` validates moves, throws `InvalidMoveException` on collisions
- **Battle System**: `Battle` runs a turn-based exchange between two `Combatant`s (name, HP, attack power, defense); `AttackMenu` offers 4 differentiated moves (`AttackMoveItem`: damage multiplier, recoil, lifesteal, evade chance)
- **Battle Triggers**: `GameManager.triggerBattle()`/`isBattlePending()`/`clearPendingBattle()` is a generic pending-battle flag set by `WildGrass.approach()` (chance-based) and `NPC.prompt()` (when `initiatesBattle` is true, the default); `GameController` picks it up after any world-map action
- **Configuration**: `MapCreator` parses world files (see map format below)
- **Menu System**: `MenuCreator` builds hierarchical menus; `Menu` holds `MenuItem` objects

### game-ui (Rendering & Input)
- **GameScreen**: Swing `JPanel` that only paints — the world map, menus, battle screen, and game-over screen. Holds no game state itself; reads everything from `GameController` via getters each `paint()` call
- **GameController**: `KeyAdapter` that owns all game session state (`gameMode`, `activeMenu`, `battle`, `player`, `message`) and processes key presses. Runs an `onChange` callback (`GameScreen::repaint`) after each key press
- **KeyBindings / GameAction**: `KeyBindings` (singleton) loads `keybindings.properties` from the classpath and resolves a raw key code to a `GameAction` enum value (`MOVE_UP/DOWN/LEFT/RIGHT`, `CONFIRM`, `MENU`, `BATTLE`); `GameController` branches on the resolved action, never on raw `KeyEvent` codes
- **Camera System**: Player-fixed viewport shows 5x5 tile area around player (not full map)
- **Image Resources**: `ImageResources` maps `Unit` subclasses to sprites, and `EnemyType` values to battle sprites (`getBattleEnemyImage(EnemyType)`)
- **Rendering**: `paintMapPlayerFixed()`, `paintMenu()`, `paintBattleGraphics()`, `paintBattleMenu()`, `paintGameOver()` handle visual output; layout uses fixed pixel constants (not `getHeight()`-relative math) so content can't overlap or clip, and the frame is `pack()`ed around `GameScreen.getPreferredSize()` rather than a hardcoded outer size

## Critical Patterns

### Singleton Pattern
`GameManager`, `MapCreator`, `MenuCreator`, `KeyBindings` all use static getInstance() with lazy initialization. This means:
- State persists across the app lifetime
- No dependency injection - access via static calls
- **Thread-unsafe** if ever multi-threaded

### Map File Format (world3.txt)
The current format is comma-separated (supports multi-digit map IDs/coordinates); a legacy single-digit fixed-width format is still parsed for backward compatibility but is no longer written to any world file.

```
m,<mapId>,<width>,<height>            # Create map
s,<mapId>,<x>,<y>                     # Add Space (walkable)
o,<mapId>,<x>,<y>                     # Add Obstacle
w,<mapId>,<x>,<y>                     # Add WildGrass (chance to trigger a battle on entry)
d,<mapId>,<x>,<y>,<targetMapId>,<targetX>,<targetY>   # Door to another map
p,<mapId>,<x>,<y>                     # Player starting position
n,<mapId>,<x>,<y>[,<initiatesBattle>] # NPC; trailing bool is optional, defaults to true
```

### Exception Strategy
- `InvalidMoveException`: Thrown by `Player.move()` on collision, and by `Menu.up()/down()/back()` at list bounds; `GameController` catches it and just logs (`ex.getError()`), no state change
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

1. **GameGUI Constructor**: Calls `MapCreator.getInstance().generateMap(world)`, `MenuCreator.getInstance().generateMenu()`, `MenuCreator.getInstance().generateBattleMenu()` - these populate the GameManager singleton - then adds a `GameScreen` and `pack()`s the frame
2. **Input Processing**: `GameScreen` constructs a `GameController` and registers it directly as its `KeyListener`; `GameController.keyPressed(KeyEvent)` resolves the key via `KeyBindings`, updates state, and triggers a repaint - there is no separate `handleInput()`/`InputHandler` indirection
3. **GameController State**: Owns `gameMode`, `activeMenu`, `battle`, `player`, `message`; `GameScreen.paint()` reads all of it through getters
4. **Cross-Module References**: pokemon-clone-core → game-ui → game-engine (unidirectional dependency)

## Common Tasks

**Adding a new entity type**:
- Create class extending `Unit` in game-engine/map/
- Add a case to the comma-format branch of `MapCreator.generateMap()` (e.g., `type == 'x'`)
- Add image resource to game-ui resources/ and register it in `ImageResources`

**Adding menu items**:
- Edit `MenuCreator.generateMenu()` to call `mainMenu.add("Label", new MenuItem())`
- Menu hierarchy is built recursively (Menu.add can take another Menu)

**Adding a new attack move**:
- Moves are per-`Combatant` (`Combatant.getMoves()`/`setMoves()`), not global — `AttackMenu` is rebuilt fresh from `battle.getPlayer().getMoves()` each time "Attack" is selected (`AttackItem`)
- Create a `new Move(name, elementType, damageMultiplier, recoilFraction, lifestealFraction, evadeChance)` and add it to a species' moveset (e.g. `EnemyType`'s per-constant move list) or the player's starter moveset (`GameManager.getParty()`)
- `AttackMoveItem` applies `TypeChart.getMultiplier(move.getElementType(), enemy.getElementType())` to damage automatically

**Fixing movement bugs**:
- Check `Player.move()` logic and `Unit.getMap()` state
- Verify `GameMap.get(x, y)` bounds checking (returns null outside bounds, not exception)
- Test with MapTest/DoorTest patterns

## Game Mode System

**Four Modes (managed by GameController):**
- **WORLD_MAP**: Explore the world with arrow keys, CONFIRM to engage NPCs/doors, MENU to open the main menu, BATTLE to start a battle. Also checks for a pending battle (from `WildGrass`/`NPC`) after every action
- **MENU**: Navigate main menu with arrow keys, MOVE_RIGHT to open submenus, CONFIRM to select, MOVE_LEFT to go back, MENU key to return to world
- **BATTLE**: Same navigation as MENU, rendered inline in the battle screen's menu box (header reads "BATTLE" or "SELECT ATTACK" depending on the active submenu). The MENU key returns to the world map; the BATTLE key just reopens the top-level battle menu, it does not exit
- **GAME_OVER**: Entered when the player's HP hits 0; CONFIRM heals the player and returns to WORLD_MAP

**Key bindings are configurable**, not hardcoded: `game-ui/src/main/resources/com/d3games/gameui/keybindings.properties` maps `move.up`/`move.down`/`move.left`/`move.right`/`confirm`/`menu`/`battle` to `KeyEvent.VK_*` names. Defaults are UP/DOWN/LEFT/RIGHT/SPACE/P/B.

## Known Quirks

- No game loop; repainting triggered by keyboard input only (AWT event-driven)
- Pressing MOVE_LEFT at the root menu (no parent) throws `InvalidMoveException`, which is just logged - it does **not** auto-return to WORLD_MAP
- `TextGame2/` is an earlier iteration; it is now an empty directory with nothing left in it
