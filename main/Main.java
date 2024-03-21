package main;

import gui.Maze2D;
import javafx.application.Application;
import mazecore.Maze;
import playercore.MoveMap;


/**
 * The main class of the program. This class is responsible for initiating the model then launching
 * the GUI or CLI controller.
 */
public class Main {

    /** The number of times the main function has been run, needed to help with hot reloads. */
    private static int runCount = 0;

    /**
     * The main method of the program.
     *
     * @param args The command line arguments as a String array.
     */
    public static void main(String[] args) {
        runCount++;
        String filePath = args[0];
        String enableGui = args[1];

        // Creates the maze and move map.
        Maze.makeMaze(filePath);
        MoveMap.makeMoveMap();

        // Launches GUI controller the first time the program is run, then reloads the controller
        // on later runs.
        if (enableGui.equals("true")) {
            if (runCount == 1) {
                Application.launch(Maze2D.class);
            } else {
                Maze2D.initGame();
            }
        } else {
            // Launch CLI controller.
            cli.MazeText.gameLoop();
        }
    }

}
