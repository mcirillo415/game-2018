# TODO

## World Map Storage

- [ ] Consider splitting each world into multiple map files instead of storing all maps in one file.
  - Add a world-level configuration or registry describing the maps in the world.
  - Give maps meaningful names, such as `forest`, `town`, or `cave`.
  - Update `MapCreator` to load the map files and register them with `GameManager`.
  - Update `Door` references so doors can target map names rather than only numeric map IDs.
  - Preserve the current `GameMap` runtime model so `Player` and `GameScreen` need minimal changes.
  - Keep the existing single-file format working until the multi-file loader is complete.
