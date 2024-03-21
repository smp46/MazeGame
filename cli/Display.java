package cli;

import mazecore.Maze;
import mazecore.Position;

/**
 * The display class for the CLI controller.
 * Takes the maze array and prints it to the console as a grid.
 */
public class Display {
    /**
     * Retrieves the maze array and prints it to the console as a grid.
     */
    public static void displayMaze() {

        // Define the ANSI escape codes for the different colours and the reset code to
        // switch between them.
        final String ansiReset = "\u001B[0m";
        final String ansiBlack = "\u001B[30m";
        final String ansiRed = "\u001B[31m";
        final String ansiYellow = "\u001B[33m";
        final String ansiBlue = "\u001B[34m";
        final String ansiPurple = "\u001B[35m";
        final String ansiWhite = "\u001B[37m";

        // Loops through the maze array and prints each component.
        for (int y = 0; y < Maze.getMazeHeight(); y++) {
            for (int x = 0; x < Maze.getMazeWidth(); x++) {
                char component = Maze.getComponentAt(new Position(x, y));
                switch (component) {
                    case '#' -> System.out.print(ansiBlack + "\u2588" + ansiReset);
                    case ' ' -> System.out.print(ansiWhite + "\u2588" + ansiReset);
                    case 'E' -> System.out.print(ansiRed + "\u2588" + ansiReset);
                    case 'S' -> System.out.print(ansiBlue + "\u2588" + ansiReset);
                    case 'H' -> System.out.print(ansiYellow + "\u2588" + ansiReset);
                    case 'P' -> System.out.print(ansiPurple + "\u2588" + ansiReset);
                }
            }
            // Prints a new line after each row.
            System.out.println();
        }
    }

    /**
     * Redraws the player in the maze by removing the 'P' char from the old position and
     * writing it at the new position, with a few conditions.
     *
     * @param oldPosition The old Position of the player.
     * @param newPosition The new Position of the player.
     */
    public static void redrawPlayer(Position oldPosition, Position newPosition) {

        // If the old position is the start position, redraws it as a start instead of blank.
        if (oldPosition == Maze.getMazeStartPos()) {
            Maze.reWriteMaze(oldPosition, 'S');
        } else if (oldPosition == Maze.getMazeEndPos()) {
            Maze.reWriteMaze(oldPosition, 'E');
        } else if (Maze.getComponentAt(oldPosition) == 'P'
                && Maze.getComponentAt(oldPosition) != 'H'
                && Maze.getComponentAt(oldPosition) != 'J') {
            Maze.reWriteMaze(oldPosition, ' ');
        }

        // Call highLightPath on the position the player has just moved from.
        MazeText.highlightPath(oldPosition);

        // Redraws the player at the new position.
        Maze.reWriteMaze(newPosition, 'P');

        // Checks if the player has reached the end of the maze.
        if (newPosition.equals(Maze.getMazeEndPos())) {
            System.out.println("Congratulations! You have reached the end of the maze!");
        }
    }

}
