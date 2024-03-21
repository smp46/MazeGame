package playercore;

import mazecore.Maze;
import mazecore.Position;

import java.util.Arrays;

/**
 * A class which stores and provides access to the player's past moves in the maze.
 * PastMoves is stored in the form of a 2D array of booleans.
 */
public class PastMoves {

    /** The player's past moves in the maze. */
    private static boolean[][] map;

    /**
     * Initialises the player's past moves in the maze, by creating a new 2D Boolean Array that
     * matches the size of the original maze array, except all the positions are marked as false to
     * indicate no moves have been made yet.
     */
    public static void pastMovesMap() {
        map = new boolean[Maze.getMazeHeight()][Maze.getMazeWidth()];
        for (int y = 0; y < Maze.getMazeHeight(); y++) {
            for (int x = 0; x < Maze.getMazeWidth(); x++) {
                map[y][x] = false;
            }
        }
    }

    /**
     * Adds a move to the player's past moves map. If an invalid move is added, it is ignored.
     *
     * @param position The position of the move.
     */
    public static void addMove(Position position) {
        try {
            map[position.getY()][position.getX()] = true;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // If the move is invalid, it is ignored.
        }
    }

    /**
     * Resets the player's past moves map.
     */
    public static void resetMap() {
        for (boolean[] booleans : map) {
            Arrays.fill(booleans, false);
        }
    }

    /**
     * Checks if the given position has already been visited.
     *
     * @param position The position to check.
     * @return True if the position has been visited before, false otherwise.
     */
    public static boolean checkMove(Position position) {
        try {
            return map[position.getY()][position.getX()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Getter method that returns the past moves map as a 2D Boolean Array.
     *
     * @return A 2D Boolean Array that represents the past moves map.
     */
    public static boolean[][] getPastMovesMap() {
        return map;
    }
}


