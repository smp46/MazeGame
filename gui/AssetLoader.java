package gui;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import mazecore.CheckPosition;
import mazecore.Maze;
import mazecore.Position;
import playercore.PlayerPosition;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A large class used for loading assets all the assets required for the GUI display.
 * Contains three inner classes: TextureSelect, TextureBitMap and DirectoryHash.
 * Without the assets folder present, this class will mostly return null values.
 */
public class AssetLoader {
    /** The texture that is used for the walls, in ImagePattern format. */
    private static ImagePattern wallTexture;

    /** The texture that is used for the grass, in ImagePattern format. */
    private static ImagePattern grassTexture;

    /** The default texture that is used as default for the flags, in ImagePattern format. */
    private static ImagePattern flagTexture;
    /** The first texture that is used for the start flag, as an ImagePattern object. */
    private static ImagePattern startFlagTexture0;
    /** The second texture that is used for the start flag, as an ImagePattern object. */
    private static ImagePattern startFlagTexture1;
    /** The third texture that is used for the start flag, as an ImagePattern object. */
    private static ImagePattern startFlagTexture2;
    /** The fourth texture that is used for the start flag, as an ImagePattern object. */
    private static ImagePattern startFlagTexture3;
    /** The first texture that is used for the end flag, as an ImagePattern object. */
    private static ImagePattern endFlagTexture0;
    /** The second texture that is used for the end flag, as an ImagePattern object. */
    private static ImagePattern endFlagTexture1;
    /** The third texture that is used for the end flag, as an ImagePattern object. */
    private static ImagePattern endFlagTexture2;
    /** The fourth texture that is used for the end flag, as an ImagePattern object. */
    private static ImagePattern endFlagTexture3;

    /** The texture that is used for the player, when facing left, as an ImagePattern object. */
    private static ImagePattern playerLeftTexture;
    /** The texture that is used for the player, when facing right, as an ImagePattern object. */
    private static ImagePattern playerRightTexture;

    /** The texture that is used for the restart button, as an Image object. */
    private static ImageView restartTexture;
    /** The button that allows the player to restart the game, as a Button object. */
    private static Button restartButton;
    /** The overlay that appears when the player completes the maze, as a Rectangle Object. */
    private static Rectangle endOverlay;

    /** The sound that is played when the player hits a wall, as a MediaPlayer object. */
    private static MediaPlayer wallHitSoundPlayer;
    /** The sound that is played when the player completes the maze, as a MediaPlayer object. */
    private static MediaPlayer levelCompleteSoundPlayer;
    /** The background music that is played during the game, as a MediaPlayer object. Declared
     * as class variable to prevent garbage collection terminating it early. */
    private static MediaPlayer musicPlayer;

    /** A boolean toggle to check if the assets are missing, defaults to true. */
    private static boolean assetsMissing = true;

    /** The expected MD5 Checksum of the assets' folder. */
    private static final String EXPECTED_ASSETS_HASH = "4e59760ca1e70e242ab2cd81c711d640";

    /**
     * Uses the hashDirectory method to compare the MD5 Checksum of the current assets folder
     * to the expected checksum. If they match, the assetsMissing toggle is set to false.
     */
    public static void checkAssets() {
        if (DirectoryHash.hashDirectory("assets").equals(EXPECTED_ASSETS_HASH)) {
            assetsMissing = false;
        }
    }

    /**
     * Adds a wall to the stack pane at the given position.
     *
     * @param position The position to add the wall at.
     * @param stackPane The stack pane to add the wall to.
     */
    static void addWall(Position position, StackPane stackPane) {
        // If assets are present, loads the wall texture. Otherwise, sets it to null.
        if (!assetsMissing) {
            // Gets the correct texture for the wall at the given position.
            wallTexture = TextureSelect.loadImage(position, "wall");
        } else {
            wallTexture = null;
        }

        // Creates a new square with the wall texture and adds it to the given stack pane.
        Maze2D.Square newWall = new Maze2D.Square(
                Maze2D.getCellWidth(), Maze2D.getCellHeight(), "wall");

        // If the wallTexture isn't null, fills newWall with it. Otherwise, fills it with black.
        newWall.setFill(Objects.requireNonNullElse(wallTexture, Color.BLACK));

        // Adds the newWall to the given stackPane.
        stackPane.getChildren().add(newWall);
    }

    /**
     * Adds a grass square to the stack pane at the given position.
     * Uses Math.random to randomly pick one of the 9 available grass textures.
     *
     * @param stackPane The stack pane to add the grass to.
     */
    protected static void addGrass(StackPane stackPane) {
        // Randomly selects the texture for the path at the given position.
        if (!assetsMissing) {
            grassTexture = new ImagePattern(
                    new Image("file:assets/textures/grass/grass0"
                            + (int) (Math.random() * 9) + ".png"));
        } else {
            grassTexture = null;
        }

        // Creates a new square with the wall texture and adds it to the given stack pane.
        Maze2D.Square grass = new Maze2D.Square(
                Maze2D.getCellWidth(), Maze2D.getCellHeight(), "grass");

        // If the grassTexture isn't null, fills grass with it. Otherwise, fills it with green.
        grass.setFill(Objects.requireNonNullElse(grassTexture, Color.GREEN));

        // Adds the grass to the given stackPane.
        stackPane.getChildren().add(grass);
    }

    /**
     * Loads the textures used for the maze.
     */
    protected static void loadTextures() {
        // If the assets aren't missing, loads the textures.
        if (!assetsMissing) {
            try {
                wallTexture = new ImagePattern(
                        new Image("file:assets/textures/wall.png"));
                playerLeftTexture = new ImagePattern(new Image(
                        "file:assets/textures/player_left.png"));
                playerRightTexture = new ImagePattern(new Image(
                        "file:assets/textures/player_right.png"));
                startFlagTexture0 = new ImagePattern(new Image(
                        "file:assets/textures/flag/sFlag0.png"));
                startFlagTexture1 = new ImagePattern(new Image(
                        "file:assets/textures/flag/sFlag1.png"));
                startFlagTexture2 = new ImagePattern(new Image(
                        "file:assets/textures/flag/sFlag2.png"));
                startFlagTexture3 = new ImagePattern(new Image(
                        "file:assets/textures/flag/sFlag3.png"));
                endFlagTexture0 = new ImagePattern(new Image(
                        "file:assets/textures/flag/eFlag0.png"));
                endFlagTexture1 = new ImagePattern(new Image(
                        "file:assets/textures/flag/eFlag1.png"));
                endFlagTexture2 = new ImagePattern(new Image(
                        "file:assets/textures/flag/eFlag2.png"));
                endFlagTexture3 = new ImagePattern(new Image(
                        "file:assets/textures/flag/eFlag3.png"));
                restartTexture = new ImageView(new Image(
                        "file:assets/textures/restart.png"));
            } catch (Exception e) {
                // If any errors occur while loading the textures,
                // sets the assetsMissing flag to true and call the method again.
                assetsMissing = true;
                loadTextures();
            }
        } else {
            // If the assets are missing, sets all the textures to null to prevent errors.
            wallTexture = null;
            playerLeftTexture = null;
            playerRightTexture = null;
            startFlagTexture0 = null;
            flagTexture = null;
            startFlagTexture1 = null;
            startFlagTexture2 = null;
            startFlagTexture3 = null;
            endFlagTexture0 = null;
            endFlagTexture1 = null;
            endFlagTexture2 = null;
            endFlagTexture3 = null;
            restartTexture = null;
        }
    }

    /**
     * Loads the start and end flags into the maze and starts a continuous AnimationTimer to
     * change the texture after a certain number of frames to mimic wind blowing the flags.
     */
    protected static void loadFlags() {
        // Defines some predetermined frame intervals for use in the flag animations.
        final int frame_interval_0 = 20;
        final int frame_interval_1 = 30;
        final int frame_interval_2 = 50;
        final int frame_interval_3 = 70;

        // Gets the start and end positions of the maze.
        Position startPos = Maze.getMazeStartPos();
        Position endPos = Maze.getMazeEndPos();

        // Creates the flag objects and adds them to the Stack Panes at the start and end positions.
        Rectangle startFlag = new Maze2D.Square(
                Maze2D.getCellWidth(), Maze2D.getCellHeight(), "startFlag");
        Rectangle endFlag = new Maze2D.Square(
                Maze2D.getCellWidth(), Maze2D.getCellHeight(), "endFlag");
        Maze2D.cellsModify(new int[]{startPos.getX(), startPos.getY()}, "add", startFlag);
        Maze2D.cellsModify(new int[]{endPos.getX(), endPos.getY()}, "add", endFlag);

        // If the assets aren't missing, sets the flag fill based on an AnimationTimer.
        // Otherwise, sets the flags to red.
        if (!assetsMissing) {
            final int[] frameCount = {0};
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (frameCount[0] % frame_interval_0 == 0) {
                        startFlag.setFill(startFlagTexture0);
                        endFlag.setFill(endFlagTexture0);
                    } else if (frameCount[0] % frame_interval_1 == 0) {
                        startFlag.setFill(startFlagTexture1);
                        endFlag.setFill(endFlagTexture1);
                    } else if (frameCount[0] % frame_interval_2 == 0) {
                        startFlag.setFill(startFlagTexture2);
                        endFlag.setFill(endFlagTexture2);
                    } else if (frameCount[0] % frame_interval_3 == 0) {
                        startFlag.setFill(startFlagTexture3);
                        endFlag.setFill(endFlagTexture3);
                    }
                    frameCount[0] += 1;
                }
            };
            timer.start();
        } else {
            startFlag.setFill(Color.RED);
            endFlag.setFill(Color.RED);
        }
    }

    /**
     * Creates a new Player2D.getPlayer() object, sets its fill
     * and adds it to the stack pane at the start position.
     */
    protected static void loadPlayer() {
        // Initialises the Player object.
        Player2D.initPlayer();

        // If the assets aren't missing, sets the player's fill to the player right texture.
        // Otherwise, sets it to blue.
        if (!assetsMissing) {
            Player2D.getPlayer().setFill(AssetLoader.getPlayerRightTexture());
        } else {
            Player2D.getPlayer().setFill(Color.BLUE);
        }

        // Adds the player to the stack pane at the start position.
        Maze2D.getCells()[
                PlayerPosition.get().getX()][PlayerPosition.get().getY()]
                .getChildren().add(Player2D.getPlayer());
    }

    /**
     * Loads the sound files and starts the background music.
     */
    protected static void loadSound() {
        if (!assetsMissing) {
            File musicFile = new File("assets/sound/music.mp3");
            File invalidFile = new File("assets/sound/invalid.mp3");
            File winFile = new File("assets/sound/win.mp3");

            // Load the sound files as MediaPlayer objects.
            wallHitSoundPlayer = new MediaPlayer(new Media(invalidFile.toURI().toString()));
            levelCompleteSoundPlayer = new MediaPlayer(new Media(winFile.toURI().toString()));

            // Loads then sets the volume and cycle count for the background music, and play it if
            // not already playing.
            if (musicPlayer == null) {
                musicPlayer = new MediaPlayer(new Media(musicFile.toURI().toString()));
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.setVolume(0.2);
                musicPlayer.play();
            }
        } else {
            wallHitSoundPlayer = null;
            levelCompleteSoundPlayer = null;
            musicPlayer = null;
        }
    }

    /**
     * Loads the restart screen, which appears when the Player2D.getPlayer() completes the maze.
     */
    protected static void loadRestartScreen() {
        double windowWidth = Maze2D.getGameWindow().getWidth();
        double windowHeight = Maze2D.getGameWindow().getHeight();

        // If assets aren't missing, adjusts and loads the restart button texture, loads the button
        // then uses CSS to make the button appear as only the texture.
        if (!assetsMissing) {
            restartTexture.setFitWidth(windowWidth * 0.2);
            restartTexture.setFitHeight(windowHeight * 0.2);


            restartButton = new Button("Restart", restartTexture);
            restartButton.setStyle("-fx-background-color: transparent;"
                    + " -fx-padding: 0; -fx-border-width: 0;"
                    + " -fx-background-radius: 0; -fx-border-radius: 0;");
        } else {
            restartButton = new Button("Restart");
        }
        restartButton.setOnAction(e -> Maze2D.restartGame());

        // Creates the end overlay, which is a black rectangle with 50% opacity.
        endOverlay = new Rectangle(windowWidth, windowHeight, Color.BLACK);
        endOverlay.setOpacity(0.5);

        // Adds the end overlay and restart button to the root stack pane.
        Maze2D.getGameWindow().getChildren().add(endOverlay);
        Maze2D.getGameWindow().getChildren().add(restartButton);
    }

    /**
     * A getter method for the boolean assetsMissing.
     *
     * @return True if assetsMissing is false, false otherwise.
     */
    public static boolean assetCheck() {
        return !assetsMissing;
    }

    /**
     * Getter method for the player left texture.
     *
     * @return The player left texture.
     */
    public static ImagePattern getPlayerLeftTexture() {
        return playerLeftTexture;
    }

    /**
     * Getter method for the player right texture.
     *
     * @return The player right texture.
     */
    public static ImagePattern getPlayerRightTexture() {
        return playerRightTexture;
    }

    /**
     * Getter method for the wall texture.
     *
     * @return The wall texture.
     */
    public static ImagePattern getWallTexture() {
        return wallTexture;
    }

    /**
     * Getter method for the grass texture.
     *
     * @return The grass texture.
     */
    public static ImagePattern getGrassTexture() {
        return grassTexture;
    }

    /**
     * Getter method for the default flag texture.
     *
     * @return The flag texture.
     */
    public static ImagePattern getFlagTexture() {
        return flagTexture;
    }

    /**
     * Getter method for the end overlay, in the form of a semi-transparent Rectangle object.
     *
     * @return The end overlay rectangle object.
     */
    public static Rectangle getEndOverlay() {
        return endOverlay;
    }

    /**
     * Getter method for the restart button object.
     *
     * @return The restart button.
     */
    public static Button getRestartButton() {
        return restartButton;
    }

    /**
     * Getter method for the wall hit sound, in a MediaPlayer object.
     *
     * @return The wall hit sound MediaPlayer.
     */
    public static MediaPlayer getWallHitSoundPlayer() {
        return wallHitSoundPlayer;
    }

    /**
     * Getter method for the level complete sound, in a MediaPlayer object.
     *
     * @return The level complete sound MediaPlayer.
     */
    public static MediaPlayer getLevelCompleteSoundPlayer() {
        return levelCompleteSoundPlayer;
    }

    /**
     * A class used to select the correct texture for a given position. Behaves differently
     * depending on the type of texture needed.
     */
    protected static class TextureSelect {
        /**
         * Takes in a boolean array and returns a string of 1s and 0s that represent
         * the matching texture file. If type is "wall", the boolean array is inverted and
         * the wall texture is selected and returned based on the matching bitMap. Otherwise,
         * the boolean is just converted to a string and returned.
         *
         * @param possibleMoves The boolean array to be converted.
         * @param type The type of texture to be returned (as a String).
         */
        private static String selectTexture(boolean[] possibleMoves, String type) {
            // Initialises the matchingTexture variable so if no match is found an empty string
            // is returned.
            String matchingTexture = "";

            // If type is "wall", inverts the possibleMoves array, then converts it to a string of
            // 1s and 0s, then checks if it matches any of the wallBitMaps. If it does, sets
            // matchingTexture to the texture name of the matching bitMap.
            if (type.equals("wall")) {
                for (int i = 0; i < possibleMoves.length; i++) {
                    possibleMoves[i] = !possibleMoves[i];
                }
                int[] ruleMatch = new int[8];
                Arrays.fill(ruleMatch, 0);
                for (int i = 0; i < possibleMoves.length; i++) {
                    if (possibleMoves[i]) {
                        ruleMatch[i] = 1;
                    } else {
                        ruleMatch[i] = 0;
                    }
                }
                for (TextureBitMap bitMap : wallBitMaps) {
                    for (int i = 0; i < 8; i++) {
                        if (Objects.equals(bitMap.conditions[i], ruleMatch[i])
                                || -1 == bitMap.conditions[i]) {
                            if (i == 7) {
                                matchingTexture = bitMap.textureName;
                            }
                        } else {
                            break;
                        }
                    }
                }
                return matchingTexture;
            } else {
                boolean[] reducedMoves = new boolean[4];
                for (int i = 0; i < possibleMoves.length; i++) {
                    if (i % 2 == 0) {
                        reducedMoves[(i / 2)] = possibleMoves[i];
                    }
                }
                return arrayToString(reducedMoves);
            }
        }

        /**
         * A helper method to convert a boolean array to a string.
         *
         * @param array The boolean array that will be converted.
         * @return A string representation of the given boolean array.
         */
        private static String arrayToString(boolean[] array) {
            StringBuilder result = new StringBuilder();
            for (boolean b : array) {
                if (b) {
                    result.append("1");
                } else {
                    result.append("0");
                }
            }
            return result.toString();
        }

        /**
         * Loads the texture for the given position and type.
         *
         * @param position The position to load the texture at.
         * @param type The type of texture to load.
         * @return The ImagePattern for the texture.
         */
        protected static ImagePattern loadImage(Position position, String type) {
            String textureNum = selectTexture(CheckPosition.get(position, 8), type);
            StringBuilder textureFilePath = new StringBuilder("file:assets/textures");
            textureFilePath.append("/").append(type).append("/").append(textureNum).append(".png");

            try {
                return new ImagePattern(new Image(textureFilePath.toString()));
            } catch (Exception e) {
                // If any exceptions occur while trying to load, assume the assets are missing
                // and return null.
                assetsMissing = true;
                return null;
            }
        }

        /**
         * A record used to store the texture name and bitmaps for each wall texture. The order of
         * the bitmaps is:
         * [top, top left, left, bottom left, bottom, bottom right, right, top right].
         *
         * @param textureName The file name of the texture.
         * @param conditions  The bitmap for the texture, an integer array representing rules
         *                    for each texture. 1 represents another wall, 0 represents a path and
         *                    -1 is a "doesn't matter" rule i.e. can be matched with 1 or 0.
         */
        private record TextureBitMap(String textureName, int[] conditions) {
        }

        /**
         * An array of TextureBitMaps to store the placement rules for every available wall texture.
         */
        private static final TextureBitMap[] wallBitMaps =  {
            new TextureBitMap("hedge00", new int[] {0, 0, 0, 0, 0, 0, 0, 0}),
            new TextureBitMap("hedge01", new int[] {0, -1, 0, -1, 0, -1, 1, -1}),
            new TextureBitMap("hedge02", new int[] {0, -1, 1, -1, 1, 1, 1, -1}),
            new TextureBitMap("hedge03", new int[] {0, -1, 1, 1, 1, -1, 0, -1}),
            new TextureBitMap("hedge04", new int[] {0, -1, 0, -1, 1, 1, 1, -1}),
            new TextureBitMap("hedge05", new int[] {0, -1, 1, 1, 1, 1, 1, -1}),
            new TextureBitMap("hedge07", new int[] {0, -1, 1, -1, 0, -1, 0, -1}),
            new TextureBitMap("hedge08", new int[] {0, -1, 0, -1, 1, -1, 1, -1}),
            new TextureBitMap("hedge09", new int[] {0, -1, 1, 0, 1, 0, 1, -1}),
            new TextureBitMap("hedge10", new int[] {1, 0, 1, 0, 1, 0, 1, 1}),
            new TextureBitMap("hedge11", new int[] {1, 1, 1, 0, 1, 1, 1, 0}),
            new TextureBitMap("hedge12", new int[] {1, 0, 1, 1, 1, 1, 1, 1}),
            new TextureBitMap("hedge13", new int[] {1, 1, 1, 1, 1, 1, 1, 1}),
            new TextureBitMap("hedge14", new int[] {1, 1, 1, 1, 1, 0, 1, 0}),
            new TextureBitMap("hedge15", new int[] {0, -1, 1, -1, 1, -1, 0, -1}),
            new TextureBitMap("hedge16", new int[] {1, -1, 0, -1, 1, 1, 1, 0}),
            new TextureBitMap("hedge17", new int[] {1, 0, 1, 1, 1, 0, 1, 0}),
            new TextureBitMap("hedge18", new int[] {1, 0, 1, 0, 1, 0, 1, 0}),
            new TextureBitMap("hedge19", new int[] {1, 0, 1, 0, 1, 1, 1, 1}),
            new TextureBitMap("hedge20", new int[] {1, 1, 1, 1, 1, 0, 1, 1}),
            new TextureBitMap("hedge21", new int[] {1, 1, 1, 0, 1, 0, 1, 1}),
            new TextureBitMap("hedge22", new int[] {1, 1, 1, 0, 1, -1, 0, -1}),
            new TextureBitMap("hedge23", new int[] {1, -1, 0, -1, 0, -1, 0, -1}),
            new TextureBitMap("hedge24", new int[] {1, -1, 0, -1, 1, 0, 1, 1}),
            new TextureBitMap("hedge25", new int[] {1, 1, 1, 0, 1, 0, 1, 0}),
            new TextureBitMap("hedge26", new int[] {1, 0, 1, 0, 1, -1, 0, -1}),
            new TextureBitMap("hedge27", new int[] {1, -1, 0, -1, 1, 1, 1, 1}),
            new TextureBitMap("hedge28", new int[] {1, 1, 1, 1, 1, 1, 1, 0}),
            new TextureBitMap("hedge29", new int[] {1, 0, 1, 1, 1, 1, 1, 0}),
            new TextureBitMap("hedge30", new int[] {1, 0, 1, 1, 1, -1, 0, -1}),
            new TextureBitMap("hedge31", new int[] {0, -1, 0, -1, 1, -1, 0, -1}),
            new TextureBitMap("hedge32", new int[] {1, -1, 0, -1, 1, 0, 1, 0}),
            new TextureBitMap("hedge33", new int[] {1, 0, 1, -1, 0, -1, 1, 0}),
            new TextureBitMap("hedge34", new int[] {1, 0, 1, 0, 1, 1, 1, 0}),
            new TextureBitMap("hedge35", new int[] {1, 0, 1, 1, 1, 0, 1, 1}),
            new TextureBitMap("hedge36", new int[] {1, 1, 1, 0, 1, 1, 1, 1}),
            new TextureBitMap("hedge38", new int[] {1, 1, 1, 1, 1, -1, 0, -1}),
            new TextureBitMap("hedge39", new int[] {1, -1, 0, -1, 1, -1, 0, -1}),
            new TextureBitMap("hedge40", new int[] {1, -1, 0, -1, 0, -1, 1, 0}),
            new TextureBitMap("hedge41", new int[] {0, -1, 1, -1, 0, -1, 1, -1}),
            new TextureBitMap("hedge42", new int[] {1, 0, 1, -1, 0, 1, 1, 1}),
            new TextureBitMap("hedge43", new int[] {1, 1, 1, -1, 0, -1, 0, -1}),
            new TextureBitMap("hedge44", new int[] {1, -1, 0, -1, 0, -1, 1, 1}),
            new TextureBitMap("hedge45", new int[] {1, 1, 1, -1, 0, -1, 1, 1}),
            new TextureBitMap("hedge46", new int[] {1, 1, 1, -1, 0, -1, 1, 0}),
            new TextureBitMap("hedge47", new int[] {1, -1, 1, -1, 0, -1, 0, -1})};
    }

    /**
     * A class used to hash every file in a folder, then hash the concatenation of all the hashes.
     */
    private static class DirectoryHash {
        /**
         * Takes in a path to a directory and returns the MD5 Checksum of a concatenation of
         * all the hashes of the files in the given directory.
         *
         * @param path The path to the directory to be hashed.
         * @return The MD5 Checksum of the directory.
         */
        private static String hashDirectory(String path) {
            ArrayList<String> hashes = new ArrayList<>();
            File directory = new File(path);
            if (directory.isDirectory()) {
                ingestDirectory(directory.listFiles(), hashes);
                return hashList(hashes);
            }
            System.err.println("Error: Assets directory not found.");
            return "";
        }

        /**
         * Takes in an array of files and an ArrayList of strings, and adds the MD5 Checksum of
         * each file to the ArrayList recursively. Recursive method is necessary to handle folders
         * within folders, recursive method only terminates when every file is not a directory
         * and has been hashed.
         *
         * @param directory The array of files to be hashed.
         * @param hashes The ArrayList to add the hashes to.
         */
        private static void ingestDirectory(File[] directory, ArrayList<String> hashes) {
            for (File file : directory) {
                // If the file is a directory, recursively call ingestDirectory on it.
                if (file.isDirectory()) {
                    ingestDirectory(file.listFiles(), hashes);
                } else {
                    // If the file is not a directory, hash it and add the hash to the hashes list.
                    try {
                        byte[] fileData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                        byte[] fileHash = MessageDigest.getInstance("MD5").digest(fileData);
                        String checksum = new BigInteger(1, fileHash).toString(16);
                        hashes.add(checksum);
                    } catch (IOException | NoSuchAlgorithmException ignored) {
                        // If any exceptions occur while hashing, ignore them and continue hashing.
                    }
                }
            }
        }

        /**
         * Takes in an ArrayList of strings and returns the MD5 Checksum of the concatenation of
         * all the strings in the ArrayList.
         *
         * @param hashes The ArrayList of strings to be hashed.
         * @return The MD5 Checksum that of the hashes ArrayList.
         */
        private static String hashList(ArrayList<String> hashes) {
            // Checks hashes are not null to prevent errors.
            if (hashes != null) {
                // Creates a StringBuilder and adds every string in the hashes ArrayList to it.
                StringBuilder hashList = new StringBuilder();
                for (String hash : hashes) {
                    hashList.append(hash);
                }
                // Converts the StringBuilder to a byte array, hashes it and returns
                // the MD5 Checksum.
                byte[] hashesData = hashList.toString().getBytes();
                try {
                    byte[] fileHash = MessageDigest.getInstance("MD5").digest(hashesData);
                    return new BigInteger(1, fileHash).toString(16);
                } catch (NoSuchAlgorithmException e) {
                    // If any exceptions occur while hashing, returns null.
                    return null;
                }
            }
            // If the method hasn't already returned, assumes errors have occurred and returns null.
            return null;
        }
    }
}
