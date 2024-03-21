package customexceptions;

/**
 * An exception thrown when the maze size is wrong.
 * i.e. it doesn't match the dimensions specified at the beginning of the file.
 */
public class MazeSizeMissmatchException extends Exception {

    /**
     * Constructs a new MazeSizeMissmatchException with the given detail message.
     *
     * @param message The detail message.
     */
    public MazeSizeMissmatchException(String message) {
        super(message);
    }
}
