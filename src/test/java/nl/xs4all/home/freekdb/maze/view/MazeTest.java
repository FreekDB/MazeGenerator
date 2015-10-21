package nl.xs4all.home.freekdb.maze.view;

import nl.xs4all.home.freekdb.maze.model.Maze;
import nl.xs4all.home.freekdb.maze.model.MazeParameters;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the <code>Maze</code> class.
 */
public class MazeTest {
    @Test
    public void testConstructor() {
        final int cellSize = 6;
        final Maze maze = new Maze(new MazeParameters("etc/unit-test.png", cellSize, cellSize, null, null, 654321));

        assertEquals(cellSize, maze.getCellWidth());
        assertEquals(cellSize, maze.getCellHeight());
    }
}
