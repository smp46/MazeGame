package mazecore;

/**
 * A record which stores the getX and getY coordinates of a position.
 *
 * @param x The X coordinate of the position.
 * @param y The Y coordinate of the position.
 */
public record Position(int x, int y) {

    /**
     * Gets the getX coordinate of the position.
     *
     * @return The getX coordinate of the position.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the getY coordinate of the position.
     *
     * @return The getY coordinate of the position.
     */
    public int getY() {
        return y;
    }

}
