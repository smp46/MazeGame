package playercore;

import mazecore.Maze;
import mazecore.Position;

/**
 * A 2D array of booleans that represents the maze.
 * Takes the maze array and converts it to a moveMap array, wherein everything but the walls are
 * marked as true (valid moves).
 */
public class MoveMap {
    /** The move map that stores where valid moves. */
    private static boolean[][] map;

    /**
     * Creates the move map by analysing the maze, creating
     * a matching 2D boolean array and marking any position that isn't a wall as a valid move.
     */
    public static void makeMoveMap() {
        map = new boolean[Maze.getMazeHeight()][Maze.getMazeWidth()];
        for (int y = 0; y < Maze.getMazeHeight(); y++) {
            for (int x = 0; x < Maze.getMazeWidth(); x++) {
                // Everything but the walls, represented by '#' are valid moves.
                if (Maze.getComponentAt(new Position(x, y)) != '#') {
                    map[y][x] = true;
                }
            }
        }
    }

    /**
     * Checks if the move is valid.
     *
     * @param position The position to check.
     * @return True if the move is valid, false otherwise.
     */
    public static boolean validMove(Position position) {
        // Tries to return the boolean at the given position, if the position is out of bounds it
        // catches the exception and returns false.
        try {
            return map[position.getY()][position.getX()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

}
