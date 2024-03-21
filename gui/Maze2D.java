package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.Main;
import mazecore.Maze;
import mazecore.Position;
import playercore.MazeSolver;
import playercore.PastMoves;
import playercore.PlayerPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static playercore.PastMoves.pastMovesMap;

/**
 * The main class for the GUI, utilising JavaFX.
 */
public class Maze2D extends Application {
    /** The main grid pane that makes the maze. */
    private static GridPane mazeGrid = new GridPane();

    /** The main stack pane that stores the maze and allows for overlays. */
    private static StackPane gameWindow = new StackPane(mazeGrid);

    /** The main border pane that stores the menu bar and game stack. */
    private static BorderPane windowOrganiser = new BorderPane();

    /** The main scene that stores the border pane. */
    private static Scene game = new Scene(windowOrganiser);

    /** The height of the window. */
    private static int windowHeight;

    /** The height of the window. */
    private static int windowWidth;

    /** The height of the maze. */
    private static int MazeHeight = Maze.getMazeHeight();

    /** The width of the maze. */
    private static int MazeWidth = Maze.getMazeWidth();

    /** The 2D array of stack panes that make up the maze. */
    private static StackPane[][] cells = new StackPane[MazeWidth][MazeHeight];

    /** The height of each cell in the maze. */
    private static int cellHeight;

    /** The width of each cell in the maze. */
    private static int cellWidth;

    /** The toggle for highlighting the path. */
    private static boolean highlightToggle = false;

    /** The maze solver object. */
    private static MazeSolver mazeSolver = null;

    /** The boolean value to indicate whether the maze is solvable. */
    private static boolean mazeSolvable = false;

    /** The notification StackPane that displays whether the maze is solvable. */
    private static StackPane notification;

    /** The boolean value to indicate whether the game is running. */
    private static boolean gameStarted = false;

    /** The primary stage for the GUI. */
    private static Stage mainGame;

    /** The number of times the game has been loaded. */
    private static int gameCount = 0;

    /** The MenuBar for the GUI, which is at the top of the BorderPane. */
    private static MenuBar topFileMenu;

    /**
     * The main method for the GUI, initialises the GUI, starts the game loop,
     * and handles key presses.
     * @param stage The stage for the GUI.
     */
    @Override
    public void start(Stage stage) {
        mainGame = stage;
        initGame();
    }

    /**
     * The main method for the GUI, initialises the GUI, starts the game loop,
     * and handles key presses. Extracted from the start method to prevent issues when reloading.
     */
    public static void initGame() {
        gameCount++;

        // If this isn't first time loading the game, reloads the main elements of the GUI.
        if (gameCount != 1) {
            gameWindow.getChildren().remove(AssetLoader.getEndOverlay());
            gameWindow.getChildren().remove(AssetLoader.getRestartButton());
            mazeGrid = new GridPane();
            gameWindow = new StackPane(mazeGrid);
            windowOrganiser = new BorderPane();
            MazeHeight = Maze.getMazeHeight();
            MazeWidth = Maze.getMazeWidth();
            cells = new StackPane[MazeWidth][MazeHeight];
            gameStarted = false;
            game = new Scene(windowOrganiser);
            // Makes sure the Player is at the start position.
            PlayerPosition.set(Maze.getMazeStartPos());
        }

        // Loads the file menu MenuBar object.
        fileMenu();

        // Places the menuBar at the top of the borderPane, and the gameWindow at the bottom.
        windowOrganiser.setTop(topFileMenu);
        windowOrganiser.setBottom(gameWindow);

        // Sets the window size and cell size relative to the size of the screen.
        dynamicWindow();

        // Titles the game window.
        mainGame.setTitle("Maze2D");

        // Checks if assets are available then try load them.
        AssetLoader.checkAssets();
        AssetLoader.loadTextures();

        // Starts the MazeSolver thread in way that allows it to still work with the main
        // JavaFX application thread.
        mazeSolver = new MazeSolver();
        Platform.runLater(() -> {
            Thread mazeSolverThread = new Thread(mazeSolver);
            // Sets the thread to be a background thread, so it stops when the main thread stops.
            mazeSolverThread.setDaemon(true);
            mazeSolverThread.start();

            // Waits until the maze is solvable before continuing.
            // Starts a timer, if the maze is not solvable within 10 seconds, throws an exception.
            int autoSolveTimer = 0;
            while (!mazeSolver.isMazeSolvable() && autoSolveTimer < 10) {
                try {
                    autoSolveTimer++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Something went wrong while waiting to see if"
                            + " the maze is solvable: " + e.getMessage());
                }
            }

            // If the maze is solvable, set the maze solvable boolean to true, then load
            // the solvable notification.
            mazeSolvable = autoSolveTimer != 10;
            solvableNotification();
        });

        // Loads basic textures for the maze
        AssetLoader.loadTextures();
        AssetLoader.loadSound();

        // Renders the maze.
        createMaze();

        // Handles key presses.
        game.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> Player2D.getPlayer().move('w');
                case A -> Player2D.getPlayer().move('a');
                case S -> Player2D.getPlayer().move('s');
                case D -> Player2D.getPlayer().move('d');
                case H -> highlightToggle = !highlightToggle;
                case Q -> mazeSolver.move();
            }
            // If the game has not started yet, starts the game, removes the solvable notification
            // and makes the gameWindow clickable (for the file menu).
            if (!gameStarted) {
                gameStarted = true;
                notification.setVisible(false);
                gameWindow.setMouseTransparent(false);
            }
        });

        // Sets the scene to green so its less obvious when textures don't cover the whole screen.
        mazeGrid.setStyle("-fx-background-color: #89b950;");

        // Loads the flags, sounds and player into the GUI.
        AssetLoader.loadFlags();
        AssetLoader.loadSound();
        AssetLoader.loadPlayer();

        // Sets the scene and shows the main game stage.
        mainGame.setScene(game);
        mainGame.show();

        // Ensures the window is centred on the screen. Especially necessary if game has just been
        // reloaded.
        centreWindow();
    }

    /**
     * Creates the GUI representation of the maze using the global Maze object.
     */
    private static void createMaze() {
        for (int y = 0; y < MazeHeight; y++) {
            for (int x = 0; x < MazeWidth; x++) {
                // Creates a new stack pane for the current cell, with a Square object
                // at the bottom of the stack pane.
                StackPane currentCellStack = new StackPane();
                GridPane.setRowIndex(currentCellStack, y);
                GridPane.setColumnIndex(currentCellStack, x);
                AssetLoader.addGrass(currentCellStack);

                // Adds the right sprite to the stack pane given the component in the maze array.
                switch (Maze.getComponentAt(new Position(x, y))) {
                    case '#' -> AssetLoader.addWall(new Position(x, y), currentCellStack);
                    case ' ', '.' -> currentCellStack
                            .getChildren().add(new Square(cellWidth, cellHeight, "path"));
                }
                // Adds the current stack pane to the mazeGrid and the cells array for later access.
                mazeGrid.getChildren().add(currentCellStack);
                cells[x][y] = currentCellStack;
            }
        }
        // Initialises the map of past moves, for helping solver methods.
        pastMovesMap();
    }

    /**
     * Restarts the game by resetting the player position,
     * removing the restart button and end overlay,
     * and resetting the map of past moves.
     */
    protected static void restartGame() {
        // Resets the player position.
        PlayerPosition.set(
                new Position(Maze.getMazeStartPos().getX(), Maze.getMazeStartPos().getY()));

        // Removes the player from the end position and adds it to the start position.
        cells[Maze.getMazeEndPos().getX()][Maze.getMazeEndPos().getY()]
                .getChildren().remove(Player2D.getPlayer());
        cells[Maze.getMazeStartPos().getX()][Maze.getMazeStartPos().getY()]
                .getChildren().add(Player2D.getPlayer());

        // Removes the restart button and end overlay.
        gameWindow.getChildren().remove(AssetLoader.getRestartButton());
        gameWindow.getChildren().remove(AssetLoader.getEndOverlay());

        // Uses PastMoves map to delete all highlighting.
        List<Node> highlightsToRemove = new ArrayList<>();
        boolean[][] pastMovesMap = PastMoves.getPastMovesMap();
        for (int y = 0; y < MazeHeight; y++) {
            for (int x = 0; x < MazeWidth; x++) {
                if (pastMovesMap[y][x]) {
                    List<Node> children = cells[x][y].getChildren();
                    if (!children.isEmpty()) {
                        for (Node child : children) {
                            if (child instanceof Square
                                    && ((Square) child).getType()
                                    .equals("highlightpath")) {
                                highlightsToRemove.add(child);
                            }
                        }
                    }
                }
            }
        }
        for (Node highlight : highlightsToRemove) {
            cells[GridPane.getColumnIndex(highlight.getParent())]
                    [GridPane.getRowIndex(highlight.getParent())].getChildren().remove(highlight);
        }

        // Removes highlighting component from the raw maze array.
        Maze.removeComponentType('H', ' ');

        // Resets the map of past moves.
        PastMoves.resetMap();
    }

    /**
     * Sets the window size relative to the size of the screen.
     */
    private static void dynamicWindow() {
        // Creates a Rectangle2D object to get the bounds (resolution) of the screen.
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenHeight = screenBounds.getHeight();
        double screenWidth = screenBounds.getWidth();

        // Sets the cell height and width to the default values (original size of textures).
        cellHeight = 32;
        cellWidth = 16;

        // Shrinks the cell height and width until the maze fits on the screen.
        while (MazeHeight * cellHeight > screenHeight + topFileMenu.getHeight()) {
            cellHeight = (int) (cellHeight * 0.99);
            while (MazeWidth * cellWidth > screenWidth) {
                cellWidth = (int) (cellWidth * 0.99);
            }
        }

        // Sets the window dimensions based on cell size and maze size.
        windowHeight = cellHeight * MazeHeight;
        windowWidth = cellWidth * MazeWidth;

        // Sets the preferred dimensions of the gameWindow.
        gameWindow.setPrefHeight(windowHeight);
        gameWindow.setPrefWidth(windowWidth);
    }

    /**
     * A simple method which gets the screen dimensions and centres the window on the screen.
     */
    private static void centreWindow() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        mainGame.setX((visualBounds.getWidth() - mainGame.getWidth()) / 2);
        mainGame.setY((visualBounds.getHeight() - mainGame.getHeight()) / 2);
    }

    /**
     * The Square class is used to represent the components of the maze.
     * It is a subclass of Rectangle, and adds a type field to store the type of component to
     * determine which fill Texture it should use.
     */
    protected static class Square extends Rectangle {
        /** The type of component the square represents. */
        private final String type;

        Square(int w, int h, String type) {
            super(w, h);
            this.type = type;

            // If assets are available, uses textures to set default fill, otherwise uses color.
            if (AssetLoader.assetCheck()) {
                switch (type) {
                    case "path", "grass" -> this.setFill(AssetLoader.getGrassTexture());
                    case "start", "end" -> this.setFill(AssetLoader.getFlagTexture());
                    case "wall" -> this.setFill(AssetLoader.getWallTexture());
                }
            } else {
                switch (type) {
                    case "path", "grass" -> this.setFill(Color.GREEN);
                    case "start", "end" -> this.setFill(Color.RED);
                    case "wall" -> this.setFill(Color.BLACK);
                }
            }

        }

        /**
         * Getter method for the type of the square.
         *
         * @return The type of the square.
         */
        public String getType() {
            return type;
        }
    }

    /**
     * Displays a notification to the user to indicate whether the maze is solvable.
     */
    private static void solvableNotification() {
        // Sets the text of the notification based on whether the maze is solvable.
        String notifyText;
        if (mazeSolvable) {
            notifyText = "Maze is solvable, good luck!";
        } else {
            notifyText = "Maze is not solvable! \nBut who am I to tell you what do to, go crazy.";
        }

        // Creates a new stack pane to store the notification and its text.
        notification = new StackPane();
        Text notificationText = new Text(notifyText);

        // Sets the width and height of the notification based on the width and height of the text.
        double notificationWidth = (notificationText.getLayoutBounds().getWidth() * 1.5);
        double notificationHeight = (notificationText.getLayoutBounds().getHeight() * 2);

        // Shrinks the notification until it fits on the screen.
        while (notificationWidth > windowWidth) {
            notificationWidth = notificationWidth * 0.99;
        }

        // Ensures the notification and its text is not too big for the screen and that text will be
        // centred in the notification stack pane.
        notification.setMaxSize(notificationWidth, notificationHeight);
        notificationText.setWrappingWidth(notificationWidth);
        notificationText.setTranslateX(notificationHeight / 2);

        // Creates a new Rectangle to contain the notificationText.
        Rectangle notificationRect = new Rectangle(notificationWidth, notificationHeight);
        notificationRect.setFill(Color.WHITE);

        // Sets preferred size of the notification, change opacity to make it semi-transparent,
        // makes it transparent to mouse events. Then adds the rectangle and text to the stack pane.
        notification.setPrefSize(notificationWidth, notificationHeight);
        notification.setOpacity(0.8);
        notification.setMouseTransparent(true);
        notification.getChildren().addAll(notificationRect, notificationText);

        // Adds the notification to the gameStack and set it to be transparent to mouse events so
        // objects underneath/nearby are still clickable.
        gameWindow.setMouseTransparent(true);
        gameWindow.getChildren().add(notification);

        // Centres the notification on the screen.
        notification.setTranslateY(-((double) windowHeight / 2) + (notificationHeight / 2));
    }

    /**
     * Plays the sound for when the player hits a wall (tries an invalid move).
     */
    protected static void invalidMove() {
        if (AssetLoader.assetCheck()) {
            try {
                // Fetches the wallHitSoundPlayer object.
                MediaPlayer wallHitSoundPlayer = AssetLoader.getWallHitSoundPlayer();
                // Resets the wall hit sound player to the start and plays it.
                wallHitSoundPlayer.seek(wallHitSoundPlayer.getStartTime());
                wallHitSoundPlayer.play();
            } catch (NullPointerException ignored) {
                // If the sound player is null, ignores the exception.
            }
        }
    }

    /**
     * Checks whether the player has completed the maze, if so loads sound and end overlay.
     * @param position The position that is being checked.
     */
    protected static void checkLevelComplete(int[] position) {
        // If the player is at the end position this plays the level
        // complete sound, and loads the restart screen.
        if (position[0] == Maze.getMazeEndPos().getX()
                && position[1] == Maze.getMazeEndPos().getY()) {
            if (AssetLoader.assetCheck()) {
                try {
                    MediaPlayer levelCompleteSoundPlayer = AssetLoader
                            .getLevelCompleteSoundPlayer();
                    levelCompleteSoundPlayer.seek(levelCompleteSoundPlayer.getStartTime());
                    levelCompleteSoundPlayer.play();
                } catch (NullPointerException ignored) {
                    // If the sound player is null, ignore the exception.
                }
            }
            AssetLoader.loadRestartScreen();
        }

    }

    /**
     * Allows modification of nodes in the cells array from outside the main application thread.
     *
     * @param position The position of the stack pane to modify in an int array, the first value
     *                 is the x position and the second value is the y position.
     * @param action The action to perform on the stack pane, either "add" or "remove".
     *               If the action is not "add" or "remove" the method will do nothing.
     * @param nodeToAction The nodeToAction to add or remove from the stack pane.
     */
    public static void cellsModify(int[] position, String action, Node nodeToAction) {
        Platform.runLater(() -> {
            try {
                switch (action) {
                    case "add" -> cells[position[0]][position[1]]
                            .getChildren().add(nodeToAction);
                    case "remove" -> cells[position[0]][position[1]]
                            .getChildren().remove(nodeToAction);
                }
            } catch (IndexOutOfBoundsException ignored) {
                // If the position is out of bounds, ignore the exception.
            }
        });
    }

    /**
     * Creates the file menu for the GUI.
     */
    private static void fileMenu() {
        // Initialises the fileMenu and its items.
        Menu fileMenu = new Menu("Change Maze");
        MenuItem smallMaze = new MenuItem("Small Maze");
        MenuItem mediumMaze = new MenuItem("Medium Maze");
        MenuItem largeMaze = new MenuItem("Large Maze");
        MenuItem customMaze = new MenuItem("Custom Maze");

        // Sets the actions for each menu item.
        // Calls the main method with the file path and GUI enable variables.
        smallMaze.setOnAction(e -> {
            String[] mainArgs = {"src/maps/maze001.txt", "true"};
            Main.main(mainArgs);
        });
        mediumMaze.setOnAction(e -> {
            String[] mainArgs = {"src/maps/maze002.txt", "true"};
            Main.main(mainArgs);
        });
        largeMaze.setOnAction(e -> {
            String[] mainArgs = {"src/maps/maze003.txt", "true"};
            Main.main(mainArgs);
        });
        customMaze.setOnAction(e -> {
            // Creates a text input customMazePopUp to get the file path from the user.
            TextInputDialog customMazePopUp = new TextInputDialog();
            customMazePopUp.setTitle("Custom Maze");
            customMazePopUp.setHeaderText(new StringBuilder()
                    .append("Enter file name for the maze.")
                    .append("\nIt must be in the src/maps folder.\n")
                    .append("Do not add the .txt extension").toString());
            Optional<String> customEntry = customMazePopUp.showAndWait();
            StringBuilder customArgs = new StringBuilder();
            customEntry.ifPresent(fileName -> customArgs
                    .append("src/maps/")
                    .append(fileName)
                    .append(".txt"));
            String[] mainArgs = {customArgs.toString(), "true"};
            Main.main(mainArgs);
        });

        // Adds the menu items to the file menu and the file menu to the final menu bar.
        fileMenu.getItems().addAll(smallMaze, mediumMaze, largeMaze, customMaze);
        topFileMenu = new MenuBar();
        topFileMenu.getMenus().add(fileMenu);
    }

    /**
     * Getter method for the stack pane array that stores each cell on the grid.
     *
     * @return The stack pane array.
     */
    protected static StackPane[][] getCells() {
        return cells;
    }

    /**
     * Getter method for the maze cell height.
     *
     * @return The height dimension of the maze cells.
     */
    protected static int getCellHeight() {
        return cellHeight;
    }

    /**
     * Getter method for the maze cell width.
     *
     * @return The height dimension of the maze cells.
     */
    protected static int getCellWidth() {
        return cellWidth;
    }

    /**
     * Getter method for the gameWindow StackPane.
     *
     * @return The gameWindow StackPane.
     */
    protected static StackPane getGameWindow() {
        return gameWindow;
    }

    /**
     * Getter method to check if highlighting is toggled.
     *
     * @return The highlight toggle boolean status.
     */
    protected static boolean getHighlightToggle() {
        return highlightToggle;
    }

}

