package nl.xs4all.home.freekdb.maze.main;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import nl.xs4all.home.freekdb.maze.model.Maze;
import nl.xs4all.home.freekdb.maze.model.MazeParameters;
import nl.xs4all.home.freekdb.maze.view.MazeView;
import nl.xs4all.home.freekdb.maze.utilities.Utilities;

/**
 * A small program to generate mazes. The mazes are created using a shape image that defines the region to use.
 *
 * @author Freek de Bruijn
 * @version 0.2 (September 2015)
 */
public class Main {
    /**
     * Main function to generate and solve a maze.
     *
     * @param arguments unused command-line arguments.
     */
    public static void main(final String[] arguments) {
        new Main().generateMaze(true);
    }

    /**
     * Generate (and possibly solve) a maze.
     *
     * @param solveMaze whether to solve the generated maze.
     */
    private void generateMaze(final boolean solveMaze) {
        final JFrame frame = new JFrame("Maze");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.WHITE);

        final MazeParameters mazeParameters = MazeParameters.getPreconfiguredParameters("Test");
        final Maze maze = new Maze(mazeParameters);

        maze.limitMazeAreaToShape(Utilities.toBufferedImage(mazeParameters.getShapeImage()));
        maze.printCrossPoints();
        maze.generateMaze();
        maze.printCrossPoints();

        final MazeView mazeView = new MazeView(maze, null);

        frame.getContentPane().add(new JScrollPane(mazeView));
        Utilities.positionFrame(frame, mazeView.getPreferredSize(), 40, 70);
        frame.setVisible(true);

        if (solveMaze)
            maze.solveMazeRecursive(mazeView, maze.getStartPoint());
    }
}
