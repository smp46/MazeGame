package gui;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import mazecore.Maze;
import mazecore.Position;
import playercore.Movement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A part of the GUI Controller which stores and provides access to the player object in the GUI.
 */
public class Player2D {

    /** The player object. */
    private static Player player;

    /** The player object. An extension of Rectangle to distinguish it from other sprites. */
    public static class Player extends Rectangle {

        /**
         * Constructor for the player object.
         *
         * @param w The width of the player.
         * @param h The height of the player.
         * @param color The color of the player.
         */
        Player(int w, int h, Color color) {
            super(w, h, color);
        }

        /**
         * Moves the player in the specified direction.
         *
         * @param direction The direction to move the player. Must be 'w', 'a', 's' or 'd'.
         */
        public void move(char direction) {
            // Checks if the direction is valid, and only continues if it is.
            ArrayList<Character> validDirections = new ArrayList<>(
                    Arrays.asList('w', 'a', 's', 'd'));
            int[] movement;
            if (validDirections.contains(direction)) {
                movement = Movement.move(direction);
            } else {
                System.err.println("Player2D.move received invalid direction: " + direction);
                return;
            }

            // Extracts the old and new positions from the movement array.
            int[] oldPosition = new int[]{movement[0],
                                                  movement[1]};
            int[] newPosition = new int[]{movement[2],
                                                  movement[3]};

            // Checks if the player has moved, if not call the invalidMove method.
            if (Arrays.equals(oldPosition, newPosition)) {
                Maze2D.invalidMove();
            } else {
                // Changes the player texture based on direction of movement.
                if (AssetLoader.getPlayerLeftTexture() != null
                    && AssetLoader.getPlayerRightTexture() != null) {
                    if (direction == 'a') {
                        this.setFill(AssetLoader.getPlayerLeftTexture());
                    } else if (direction == 'd') {
                        this.setFill(AssetLoader.getPlayerRightTexture());
                    }
                }

                // Highlights the path the player has taken if highlighting is on.
                highlightPath(new Position(oldPosition[0], oldPosition[1]));

                // Remove the player from the old position and add it to the new position.
                Maze2D.cellsModify(oldPosition, "remove", Player2D.getPlayer());
                Maze2D.cellsModify(newPosition, "add", Player2D.getPlayer());

                // Checks if the player has reached the end of the maze.
                Maze2D.checkLevelComplete(newPosition);
            }
        }

    }

    /**
     * Initializes the player object.
     */
    protected static void initPlayer() {
        player = new Player2D.Player(Maze2D.getCellWidth(), Maze2D.getCellHeight(), null);
    }

    /**
     * Getter method for the player object.
     * @return The player object.
     */
    public static Player getPlayer() {
        return player;
    }

    /**
     * Highlights the path at the given highlightPos.
     *
     * @param highlightPos The highlightPos to highlight the path at.
     */
    static void highlightPath(Position highlightPos) {
        // If the highlight toggle is on, attempts to highlight the path at the given highlightPos.
        if (Maze2D.getHighlightToggle()) {
            // Doesn't highlight the start or end positions.
            if (!highlightPos.equals(Maze.getMazeStartPos())
                    && !highlightPos.equals(Maze.getMazeEndPos())) {

                // Gets cell sizes once, so they don't need to be retrieved again.
                int cellHeight = Maze2D.getCellHeight();
                int cellWidth = Maze2D.getCellWidth();

                // Gets the texture for the path at the given highlightPos if it exists.
                ImagePattern pathPattern;
                if (AssetLoader.assetCheck()) {
                    pathPattern = AssetLoader.TextureSelect.loadImage(highlightPos, "path");
                } else {
                    pathPattern = null;
                }

                // Creates a new square with the highlight path texture and adds it to the stack
                // pane at the given highlightPos.
                Maze2D.Square highlightPath =
                        new Maze2D.Square(cellWidth, cellHeight, "highlightpath");

                // Sets the fill of the highlight path to the path texture if it exists, or white
                // if it doesn't.
                if (AssetLoader.assetCheck()) {
                    highlightPath.setFill(pathPattern);
                } else {
                    highlightPath.setFill(Color.WHITE);
                }

                // If the highlightPos is already highlighted, darkens the highlight path to indicate
                // back tracking.
                if (Maze.getComponentAt(highlightPos) == 'H') {
                    // Use ColorAdjust to darken the ImagePattern before applying.
                    ColorAdjust colorAdjust = new ColorAdjust();
                    colorAdjust.setBrightness(-0.5); // adjust to make darker or lighter
                    highlightPath.setEffect(colorAdjust);
                }
                Maze.reWriteMaze(highlightPos, 'H');

                // Finally adds the highlightPath object to the stack pane in the old highlightPos.
                Maze2D.cellsModify(new int[]{
                        highlightPos.getX(), highlightPos.getY()}, "add", highlightPath);
            }
        }
    }
}
