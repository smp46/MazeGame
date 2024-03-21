package customexceptions;

/**
 * An exception thrown when the maze is malformed.
 * i.e. it doesn't match the format specified in the spec.
 */
public class MazeMalformedException extends Exception {

    /**
     * Constructs a new MazeMalformedException with the given detail message.
     *
     * @param message The detail message.
     */
    public MazeMalformedException(String message) {
        super(message);
    }
}