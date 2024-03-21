package cli;

import mazecore.Maze;
import mazecore.Position;
import playercore.MazeSolver;
import playercore.Movement;
import playercore.PastMoves;
import playercore.PlayerPosition;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static playercore.PastMoves.pastMovesMap;

/**
 * The CLI controller class.
 */
public class MazeText {

    /** A boolean toggle to determine whether to highlight the path or not. */
    private static Boolean highlightToggle = false;

    /**
     * The main method of the CLI controller which runs the game.
     */
    public static void gameLoop() {
        // Initialises the past move map, so it is ready for later use.
        pastMovesMap();

        // Starts the MazeSolver thread and tells it the game is being run through the CLI.
        MazeSolver mazeSolver = new MazeSolver();
        mazeSolver.setGuiSolve(false);
        Thread mazeSolverThread = new Thread(mazeSolver);
        // Sets the thread to be a background thread, so it stops when the main thread stops.
        mazeSolverThread.setDaemon(true);
        mazeSolverThread.start();

        // Starts a timer, waits and if the maze is not solved within 10 seconds, the program will
        // continue and assume the maze is not solvable.
        int mazeSolveTimer = 0;
        while (!mazeSolver.isMazeSolvable() && mazeSolveTimer < 10) {
            try {
                mazeSolveTimer++;
                System.out.println("Checking if maze is solvable...");
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                System.err.println("Something went wrong while waiting to see if"
                        + " the maze is solvable: " + ie.getMessage());
            }
        }

        // Prints a message if the maze is solvable and an error if it isn't.
        if (mazeSolver.isMazeSolvable()) {
            System.out.println("Maze is solvable.");
        } else {
            System.err.println("Maze is not solvable.");
        }

        // Sets and displays the player's position as the start position.
        PlayerPosition.set(Maze.getMazeStartPos());
        Display.redrawPlayer(Maze.getMazeEndPos(), Maze.getMazeStartPos());
        Display.displayMaze();

        // Starts a BufferReader instance to read the player's input.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            // Continue to ask for input until the player reaches the end of the maze.
            while (PlayerPosition.get() != Maze.getMazeEndPos()) {
                String direction = reader.readLine();
                char input = direction.toCharArray()[0];
                if (input == 'h') {
                    highlightToggle = !highlightToggle;
                } else if (input == 'w' || input == 'a' || input == 's' || input == 'd') {
                    textMove(input);
                } else if (input == 'q') {
                    mazeSolver.move();
                    Display.displayMaze();
                } else {
                    System.err.println("Invalid input."
                            + " Use WASD to navigate and the H key to toggle path highlighting");
                }
                Display.displayMaze();
            }
        } catch (Exception e) {
            // If BufferedReader throws any exceptions, it assumes something has gone wrong
            // and exits the game.
            System.err.println("Something went wrong: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Moves the player in the given direction in the CLI.
     *
     * @param direction The direction to move the player in.
     */
    private static void textMove(char direction) {
        int[] moveResults = Movement.move(direction);
        Display.redrawPlayer(new Position(moveResults[0], moveResults[1]),
                             new Position(moveResults[2], moveResults [3]));
    }

    /**
     * Getter method for the highlight toggle.
     *
     * @return The highlight toggle boolean value.
     */
    public static boolean getHighlightToggle() {
        return highlightToggle;
    }

    /**
     * Highlights the path that the player has taken by rewriting the 2D Maze Char Array with a H.
     *
     * @param position The position to highlight.
     */
    public static void highlightPath(Position position) {
        if (getHighlightToggle()) {
            if (!position.equals(Maze.getMazeStartPos())
                    && !position.equals(Maze.getMazeEndPos())
                    && PastMoves.checkMove(position)) {
                Maze.reWriteMaze(position, 'H');
            }
        }
    }
}
