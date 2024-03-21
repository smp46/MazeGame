package playercore;

import mazecore.Position;

import java.util.Arrays;


/**
 * Class used to move the player around the maze.
 */
public class Movement {

    /**
     * Moves the player in the given direction. If the move is valid the global Player Position is
     * updated, the move is added to the past moves map and the old and new positions are returned.
     * If the move is invalid, the old position is returned twice to represent the player's
     * new position is equal to their old position.
     *
     * @param direction The direction to move the player in.
     * @return An array of integers representing the player's old and new positions.
     */
    public static int[] move(char direction) {
        // Creates a copy of the player's current position and stores it in two arrays for
        // easy access.
        int[] oldPosition = new int[]{
                PlayerPosition.get().getX(), PlayerPosition.get().getY()};
        int[] newPosition = Arrays.copyOf(oldPosition, 2);

        // Updates the new position based on the direction provided.
        switch (direction) {
            case 'w' -> newPosition[1] = PlayerPosition.get().getY() - 1;
            case 'a' -> newPosition[0] = PlayerPosition.get().getX() - 1;
            case 's' -> newPosition[1] = PlayerPosition.get().getY() + 1;
            case 'd' -> newPosition[0] = PlayerPosition.get().getX() + 1;
            // If an invalid direction is given, print an error.
            default -> System.err.println("Unexpected value in Movement class: " + direction);
        }
        // If the move is valid, updates the player's position, adds the move to the past moves map
        // and returns the old position and the new position. Else returns the old position twice.
        if (MoveMap.validMove(new Position(newPosition[0], newPosition[1]))) {
            PastMoves.addMove(new Position(newPosition[0], newPosition[1]));
            PlayerPosition.set(new Position(newPosition[0], newPosition[1]));
            return new int[]{oldPosition[0], oldPosition[1], newPosition[0], newPosition[1]};
        } else {
            return new int[]{oldPosition[0], oldPosition[1], oldPosition[0], oldPosition[1]};
        }
    }

}
