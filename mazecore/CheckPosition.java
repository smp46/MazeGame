package mazecore;

import playercore.MoveMap;

/**
 * Creates an array of possible moves for the given position and whether they are valid.
 * Can return two possible size of array depending on the numToCheck int.
 */
public class CheckPosition {

    /**
     * Returns an array of possible moves for the position and whether they are valid.
     * If numToCheck is 4, the array will be [up, left, down, right].
     * If numToCheck is 8, the array will be [up, top left, left, bottom left, down, bottom right,
     * right, top right].
     *
     * @param positionToCheck The position to check.
     * @param numToCheck The number of positions to check.
     * @return An array of possible moves for the position and whether they are valid.
     */
    public static boolean[] get(Position positionToCheck, int numToCheck) {
        // Gets the X and Y coordinates of the position to check.
        int x = positionToCheck.getX();
        int y = positionToCheck.getY();

        // Creates an array of booleans to store whether the moves are valid.
        boolean[] possibleMoves = new boolean[8];

        // Up.
        possibleMoves[0] = MoveMap.validMove(new Position(x, (y - 1)));
        // Top Left.
        possibleMoves[1] = MoveMap.validMove(new Position((x - 1), (y - 1)));
        // Left.
        possibleMoves[2] = MoveMap.validMove(new Position((x - 1), y));
        // Bottom Left.
        possibleMoves[3] = MoveMap.validMove(new Position((x - 1), (y + 1)));
        // Down.
        possibleMoves[4] = MoveMap.validMove(new Position(x, (y + 1)));
        // Bottom Right.
        possibleMoves[5] = MoveMap.validMove(new Position((x + 1), (y + 1)));
        // Right.
        possibleMoves[6] = MoveMap.validMove(new Position((x + 1), y));
        // Top Right.
        possibleMoves[7] = MoveMap.validMove(new Position((x + 1), (y - 1)));

        if (numToCheck == 4) {
            boolean[] reducedPossibleMoves = new boolean[4];
            // Up.
            reducedPossibleMoves[0] = possibleMoves[0];
            // Left.
            reducedPossibleMoves[1] = possibleMoves[2];
            // Down.
            reducedPossibleMoves[2] = possibleMoves[4];
            // Right.
            reducedPossibleMoves[3] = possibleMoves[6];
            return reducedPossibleMoves;
        }

        return possibleMoves;
    }

}
