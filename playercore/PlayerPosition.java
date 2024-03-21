package playercore;

import mazecore.Maze;
import mazecore.Position;

/**
 * A global class which stores and provides access to the player's position in the maze.
 * PlayerPosition is stored in the form of a Position object.
 */
public class PlayerPosition {

    /** The player's position in the maze. */
    private static Position PlayerPosition = Maze.getMazeStartPos();

    /**
     * Gets the player's position in the maze.
     *
     * @return The player's position in the maze.
     */
    public static Position get() {
        return PlayerPosition;
    }

    /**
     * Sets the player's position in the maze.
     *
     * @param position The position to set the player's position to.
     */
    public static void set(Position position) {
        PlayerPosition = position;
    }

}
