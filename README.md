# MazeGame
This is a game I completed in my Intro to Java class. I went far beyond what they asked of us, so here it is.

A goose named Sir Wobbleton is stuck in a dynamically loaded maze (of your choosing), you must get him out (or simply press 'Q' and allow the maze solver to solve the maze). Mazes can be loaded from any file that matches the given format (Theoretically can be infinitely big). The game also can be loaded in three different ways: CLI mode, GUI no-asset mode, and GUI asset mode. Due to licensing, I cannot include the asset folder in the source code, however do not worry I implemented an MD5 checksum check to verify all assets before displaying. If the assets don't match the checksum, the game will work just fine. Checkout below.

![Demo](https://github.com/smp46/MazeGame/assets/27676057/dceeba19-7593-4905-91eb-74e6a583d862)


### Notable Features:
- Breadth First Solver, because any maze (matching the spec) can be loaded, BFS is utilised to autosolve any possible maze.
- Dynamic texture loading, using MD5 hashing the game can identify if assets are missing and/or incorrect and switch to an asset-free mode.
- Dynamic texture picking, I pain stakingly matched every possible maze wall position to the right texture so Sir Wobbleton and his maze will always look good.
- Object Oriented design, both the GUI and CLI utilise the very same game framework for modularity and ease of use.
- Multithreaded optimisation, the BFS maze solver runs on a dedicated thread to prevent slowdowns when launching the game.

### Game Controls:
- _WASD_ for movement.
- _Q_ to autosolve maze.
- _H_ to enable path highlighting.

## Three Exciting Game Modes:

### CLI Mode

![cli medium maze](https://github.com/smp46/MazeGame/assets/27676057/6e31c199-3cec-4fe3-b70a-fe20f878b584)

### GUI No-Assets Mode

![gui medium maze no-asset](https://github.com/smp46/MazeGame/assets/27676057/a5c518b3-bedf-4280-8d80-39b4e76bb672)

### GUI Assets Mode

![gui medium maze](https://github.com/smp46/MazeGame/assets/27676057/d72c932d-f3d8-46e7-b211-2a4b71875e0a)


## Launching the Game
- JavaFX library is required.
- Developed and tested with JDK 17.

Command line arguments should be passed to the Launcher.class file in this format:

```java Launcher [CLI or GUI] [relative dir of maze file]```

So for example to load the GUI with the medium maze:

```java Launcher GUI src/maps/maze002.txt```

## Creating Your Own Mazes

If you want to make your own mazes, here is the specification the maze files must match:

- The maze's dimensions must be in the file's first line in the form of two integers, both must be odd numbers to ensure the maze has an external wall and internal paths.
- The remaining maze attributes have to be provided in the following char configurations:
  - Walls - `#`
  - Traversable Paths - `' '` or `.`
  - Start Point - `S`
  - End Point - `E`

So the text file of the small maze shown above looks like this:
```
7 7
#######
#S#   #
# ### #
# #   #
# # # #
#   #E#
#######
```

Feel free to contribute, suggest improvements, or report bugs!

If anyone is interested, I'd be happy figure out how to release a compiled jar including the assets.
