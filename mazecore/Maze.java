package mazecore;

import customexceptions.MazeMalformedException;
import customexceptions.MazeSizeMissmatchException;
import io.FileLoader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class which stores and provides access to the maze and its attributes.
 */
public class Maze {

    /** The maze array. */
    private static char[][] maze;

    /** The width of the maze. */
    private static int mazeWidth;

    /** The height of the maze. */
    private static int mazeHeight;

    /** The position of the start of the maze. */
    private static Position mazeStartPos;

    /** The position of the end of the maze. */
    private static Position mazeEndPos;

    /** An ArrayList of all the allowed component types. */
    private static final ArrayList<Character> allowedTypes = new ArrayList<>(
            Arrays.asList('S', 'E', '#', ' ', '.', 'H'));

    /**
     * Initialises the maze.
     * @param filename The name of the file from which the maze is loaded.
     */
    public static void makeMaze(String filename) {
        // Instantiates file loader for loading the maze.
        FileLoader loadMap = new FileLoader();

        // Catches any exceptions that may occur when loading the maze,
        // then print as error and exit.
        try {
            maze = loadMap.load(filename);
            mazeWidth = (maze[0].length);
            mazeHeight = (maze.length);
            mazeAnalyser(maze);
        } catch (MazeMalformedException | MazeSizeMissmatchException
                 | IllegalArgumentException | FileNotFoundException e) {
            System.err.println("Maze cannot be loaded because: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Analyzes the maze to find and store the start and end positions.
     *
     * @param maze The maze 2D char array.
     */
    private static void mazeAnalyser(char[][] maze) {
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                if (maze[y][x] == 'S') {
                    mazeStartPos = (new Position(x, y));
                } else if (maze[y][x] == 'E') {
                    mazeEndPos = (new Position(x, y));
                }
            }
        }
    }

    /**
     * Getter method that returns the component at the specified position.
     *
     * @param position The position to get the component at.
     * @return The component at the specified position.
     */
    public static char getComponentAt(Position position) {
        // Gets the getX and getY coordinates of the position once.
        int x = position.getX();
        int y = position.getY();

        // If the position is within the bounds of the maze, returns the component at that position.
        if (x >= 0 && x <= getMazeWidth() && y >= 0 && y <= getMazeHeight()) {
            return maze[y][x];
        } else {
            // Else prints an error and return a placeholder space character.
            System.err.println("Error in getting component at position, position out of bounds.");
            return ' ';
        }
    }

    /**
     * Setter method that rewrites the given position in the maze with the given component (char).
     *
     * @param position The position to rewrite.
     * @param component The component (char) to rewrite the position with.
     */
    public static void reWriteMaze(Position position, char component) {
        maze[position.getY()][position.getX()] = component;
    }

    /**
     * Removes all instances of the given component type from the maze.
     *
     * @param componentType The component type (char) to remove.
     * @param replacementType The type (char) to replace the removed component with.
     */
    public static void removeComponentType(char componentType, char replacementType) {

        // If the component type is valid, iterates through the maze and replace all instances of
        // that component type with the replacement type. Else, prints an error message.
        if (allowedTypes.contains(componentType) && allowedTypes.contains(replacementType)) {
            for (int y = 0; y < mazeHeight; y++) {
                for (int x = 0; x < mazeWidth; x++) {
                    if (getComponentAt(new Position(x, y)) == componentType) {
                        maze[y][x] = replacementType;
                    }
                }
            }
        } else {
            System.err.println("Invalid component type.");
        }
    }

    /**
     * Getter method that returns the width of the maze.
     *
     * @return The maze width as an int.
     */
    public static int getMazeWidth() {
        return mazeWidth;
    }

    /**
     * Getter method that returns the height of the maze.
     *
     * @return The maze height as an int.
     */
    public static int getMazeHeight() {
        return mazeHeight;
    }


    /**
     * Getter method the start position of the maze.
     *
     * @return The start position record.
     */
    public static Position getMazeStartPos() {
        return mazeStartPos;
    }


    /**
     * Getter method that returns the end position of the maze.
     *
     * @return The end position record.
     */
    public static Position getMazeEndPos() {
        return mazeEndPos;
    }
}
