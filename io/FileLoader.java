package io;

import customexceptions.MazeMalformedException;
import customexceptions.MazeSizeMissmatchException;
import customexceptions.UnexpectedCharException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Loads the maze from a file according to the FileInterface.
 */
public class FileLoader implements FileInterface {

    @Override
    public char[][] load(String filename) throws MazeMalformedException, MazeSizeMissmatchException,
            IllegalArgumentException, FileNotFoundException {

        // Initializes the maze array as an empty 2D char array.
        char[][] maze = new char[0][];

        String currentLine;
        int lineCount = 0;
        int startCount = 0;
        int endCount = 0;
        int height = 0;
        int width = 0;

        try (BufferedReader mazeFile = new BufferedReader(new java.io.FileReader(filename))) {
            while ((currentLine = mazeFile.readLine()) != null) {
                lineCount++;

                // Reads the first two integers in the file
                // and sets them as the height and width of the maze.
                if (lineCount == 1) {
                    try {
                        String[] dimension = currentLine.split(" ");
                        height = Integer.parseInt(dimension[0]);
                        width = Integer.parseInt(dimension[1]);
                        maze = new char[height][width];
                    } catch (NumberFormatException nfe) {
                        throw new MazeMalformedException("Maze malformed. The first line must "
                                + "be two integers separated by a space.");
                    }
                } else if (lineCount >= 2 && lineCount <= height + 1) {
                    currentLine = currentLine.trim();
                    int y = lineCount - 2;
                    if (currentLine.length() != width) {
                        throw new MazeSizeMissmatchException("Maze size mismatch.");
                    }
                    for (int x = 0; x < width; x++) {
                        char currentChar = currentLine.charAt(x);
                        if (currentChar == '#' || currentChar == ' ' || currentChar == '.') {
                            maze[y][x] = currentChar;
                        } else if (currentChar == 'S') {
                            maze[y][x] = currentChar;
                            startCount++;
                        } else if (currentChar == 'E') {
                            maze[y][x] = currentChar;
                            endCount++;
                        } else {
                            // Throw an exception if the character is not a valid maze character.
                            throw new UnexpectedCharException("Maze malformed. "
                                    + "Unexpected character: " + currentChar);
                        }
                    }
                }
            }
        } catch (UnexpectedCharException uce) {
            throw new UnexpectedCharException(uce.getMessage());
        } catch (MazeMalformedException mme) {
            throw new MazeMalformedException(mme.getMessage());
        } catch (MazeSizeMissmatchException msme) {
            // Uses StringBuilder to create an exception message detailing the mismatch.
            StringBuilder errorMessage = new StringBuilder().append("Maze size mismatch. ")
                    .append("Expected dimensions do not match actual dimensions. ")
                    .append("Expected: ").append(height).append("x").append(width)
                    .append("Actual: ").append(maze.length).append("x").append(maze[0].length);
            throw new MazeSizeMissmatchException(errorMessage.toString());
        } catch (IOException ioe) {
            throw new FileNotFoundException("The following file could not be found: " + filename);
        }

        // Only after the maze has been read, checks last row of maze is as expected.
        StringBuilder currentRow = new StringBuilder();
        StringBuilder expectedLastRow = new StringBuilder();
        for (char component : maze[height - 1]) {
            currentRow.append(component);
            expectedLastRow.append('#');
        }
        // If the last row isn't made up of only wall components, assume there is a size mismatch.
        if (currentRow.compareTo(expectedLastRow) != 0) {
            throw new MazeSizeMissmatchException("Maze size mismatch"
                    + "The given height does not match the actual maze height");
        }

        // Throw an exception if there is more than one start or end positions.
        if (startCount != 1 || endCount != 1) {
            throw new MazeMalformedException("Maze malformed."
                    + " There must and can only be one start and one end position");
        }

        // Throw an exception if height and width are even numbers.
        if (height % 2 == 0 || width % 2 == 0) {
            throw new MazeMalformedException("Maze malformed."
                    + " Height and width must be odd numbers.");
        }

        return maze;
    }
}
