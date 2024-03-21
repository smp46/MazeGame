package customexceptions;

/**
 * A custom exception to be thrown when an unexpected character is found in a maze file.
 */
public class UnexpectedCharException extends MazeMalformedException {
    /**
     * Constructs a new UnexpectedCharException with the given detail message.
     *
     * @param message The detail message.
     */
    public UnexpectedCharException(String message) {
        super(message);
    }
}
