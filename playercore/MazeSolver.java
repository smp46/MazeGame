package playercore;

import cli.Display;
import gui.Player2D;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import mazecore.CheckPosition;
import mazecore.Maze;
import mazecore.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A class which solves the maze automatically using the breadth first search algorithm, implements
 * Runnable so the maze can be solved (almost instantly).
 */
public class MazeSolver implements Runnable {
    /** A boolean value used to determine whether to move by the GUI or CLI method.*/
    private boolean guiSolve = true;

    /** A list of positions that make up the quickest path through the maze, initially empty. */
    private List<Position> quickestPath = new ArrayList<>();

    /** The start position of the maze converted to a BfsPosition object. */
    private final BfsPosition mazeStart = new BfsPosition(Maze.getMazeStartPos(), null);

    /** The end position of the maze converted to a BfsPosition object. */
    private final BfsPosition mazeEnd = new BfsPosition(Maze.getMazeEndPos(), null);

    /**
     * Solves the maze using breadth first search by creating a queue of BfsPositions and
     * exploring the maze until the end position is reached or every path has been explored.
     * If the end position is reached, the quickest path is reconstructed based on the parent of
     * each position.
     */
    public void bfsSolve() {

        // Create a queue of BfsPositions to navigate the maze and find the quickest path.
        Queue<BfsPosition> queue = new LinkedList<>();

        // Mark the start position as visited and adds it to the queue.
        PastMoves.addMove(mazeStart.position);
        queue.add(mazeStart);

        // Continue until the queue is empty.
        while (!queue.isEmpty()) {
            // Get the current position from the head of the queue.
            BfsPosition currentPos = queue.poll();

            // If the current position is the end position, reconstruct the path and return.
            if (currentPos.position.equals(mazeEnd.position)) {
                reconstructPath(currentPos, null);
                return;
            }

            // Explore neighbors of the current position.
            for (BfsPosition neighbor : checkNewNeighbours(currentPos)) {
                // Check if the neighbor has been visited.
                if (!PastMoves.checkMove(neighbor.position)) {
                    // Mark neighbor as visited.
                    PastMoves.addMove(neighbor.position);
                    // Set the current position as the parent of its neighbor.
                    neighbor.parent = currentPos;
                    // Add the neighbor to the queue.
                    queue.add(neighbor);
                }
            }
        }

        // If the queue is empty and the end position has not been reached, the maze is
        // assumed to be unsolvable.
        System.err.println("Maze is unsolvable.");
    }

    /**
     * Checks cells around the given position to see if they are valid moves, if they are, they are
     * added to the list of neighbors to be returned.
     *
     * @param current The position to check around.
     * @return A list of neighbour cells that can be reach with one valid move.
     */
    private List<BfsPosition> checkNewNeighbours(BfsPosition current) {
        // Create a list of neighbours, stored as BfsPositions, to be returned.
        List<BfsPosition> neighbors = new ArrayList<>();

        // Use the CheckPosition class to check if the cells around the current position are valid
        // moves.
        boolean[] validNeighbours = CheckPosition.get(current.position, 4);

        // Iterate through the validNeighbours array and add the cells that can be moved to,
        // to the list of neighbours to be returned.
        for (int i = 0; i < validNeighbours.length; i++) {
            BfsPosition pos = current;
            if (i == 0 && validNeighbours[i]) {
                pos = new BfsPosition(
                        new Position(current.position.getX(), current.position.getY() - 1), current);
            } else if (i == 1 && validNeighbours[i]) {
                pos = new BfsPosition(
                        new Position(current.position.getX() - 1, current.position.getY()), current);
            } else if (i == 2 && validNeighbours[i]) {
                pos = new BfsPosition(
                        new Position(current.position.getX(), current.position.getY() + 1), current);
            } else if (i == 3 && validNeighbours[i]) {
                pos = new BfsPosition(
                        new Position(current.position.getX() + 1, current.position.getY()), current);
            }
            neighbors.add(pos);
        }
        return neighbors;
    }

    /**
     * A recursive method to reconstruct the quickest path from the end of the maze to the start,
     * using the parent positions of each position. Exits when the start position is reached.
     *
     * @param current The position that is currently being processed.
     * @param path The list of positions that make up the quickest path, passed in recursively for
     *             continuity.
     */
    private void reconstructPath(BfsPosition current, List<Position> path) {
        // If the path is null, create a new list.
        if (path == null) {
            path = new ArrayList<>();
        }

        // Add current position to the beginning of the path list.
        path.add(0, current.position);

        // If the current position is the start position or the current position has no parent,
        // set the quickest path to the path list and return, finishing the recursion.
        if (current.equals(mazeStart) || current.parent == null) {
            quickestPath = path;
            return;
        }

        // Call the method again with the current position's parent and the path list.
        reconstructPath(current.parent, path);
    }

    /**
     * An extension of the Position record that adds a parent position, which is needed to implement
     * breadth first search.
     */
    private static class BfsPosition {
        /** The position record for this BfsPosition. */
        private final Position position;

        /** The parent position of this BfsPosition. */
        private BfsPosition parent;

        public BfsPosition(Position position, BfsPosition parent) {
            this.position = position;
            this.parent = parent;
        }
    }

    /**
     * Uses the positions in the quickest path list to move the player through the maze. Only
     * works if player is already on the quickest path, for example at the starting position.
     */
    public void move() {
        // Checks if the player is on the quickest path, if not return.
        boolean onPath = false;
        for (Position p : quickestPath) {
            if (PlayerPosition.get().equals(p)) {
                onPath = true;
                break;
            }
        }
        if (!onPath) {
            return;
        }

        // Iterates through the quickest path list and move the player in the appropriate direction.
        for (int i = 0; i < quickestPath.size() - 1; i++) {
            Position currentPos = quickestPath.get(i);
            Position nextPos = quickestPath.get(i + 1);

            // Determines direction based on current and next positions.
            if (nextPos.getX() == currentPos.getX()
                    && nextPos.getY() == currentPos.getY() - 1) {
                movePlayer('w');
            } else if (nextPos.getX() == currentPos.getX() - 1
                    && nextPos.getY() == currentPos.getY()) {
                movePlayer('a');
            } else if (nextPos.getX() == currentPos.getX()
                    && nextPos.getY() == currentPos.getY() + 1) {
                movePlayer('s');
            } else if (nextPos.getX() == currentPos.getX() + 1
                    && nextPos.getY() == currentPos.getY()) {
                movePlayer('d');
            }
        }
    }

    /**
     * Helper method that moves the player in the given direction, the way the player is moved
     * depends on if the GUISolve variable is true or false.
     *
     * @param direction The direction to move the player in.
     */
    private void movePlayer(char direction) {
        if (guiSolve) {
            Player2D.getPlayer().move(direction);
        } else {
            int[] moveResults = Movement.move(direction);
            Display.redrawPlayer(new Position(moveResults[0], moveResults[1]),
                    new Position(moveResults[2], moveResults [3]));
        }
    }


    @Override
    public void run() {
        // Starts solving the maze as soon as the thread is started.
        bfsSolve();
    }

    /**
     * Checks if the maze is solvable by checking if the quickest path is empty.
     *
     * @return True if the maze is solvable (the quickest path is not empty), false otherwise.
     */
    public boolean isMazeSolvable() {
        return !quickestPath.isEmpty();
    }

    /**
     * Setter method that sets the guiSolve variable, so the solver knows whether to move the player
     * in the GUI or CLI.
     *
     * @param guiSolve True if the solver should move the player via the GUI, false otherwise.
     */
    public void setGuiSolve(boolean guiSolve) {
        this.guiSolve = guiSolve;
    }
}

