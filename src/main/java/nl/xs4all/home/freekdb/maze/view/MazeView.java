package nl.xs4all.home.freekdb.maze.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import nl.xs4all.home.freekdb.maze.model.Maze;

/**
 */
public class MazeView extends JComponent {
    private static final boolean SHOW_START_END_POINTS = false;
    private static final boolean SHOW_DEAD_END_PARTS = false;
    private static final boolean TEST_MODE = false;

    private static final Color COLOR_VISITED_POINTS = Color.MAGENTA;

    private Maze maze;
    private Dimension size;

    private Image image;

    public MazeView(final Maze maze, final Image image) {
        this.maze = maze;
        this.image = image;

        this.size = new Dimension(maze.getCellWidth() * (maze.getMazeWidth() + 2),
                                  maze.getCellHeight() * (maze.getMazeHeight() + 2));

        addMouseListener(new MouseAdapter() {
            public void mouseReleased(final MouseEvent mouseEvent) {
                final int row = (mouseEvent.getPoint().y / maze.getCellHeight()) - 1;
                final int column = (mouseEvent.getPoint().x / maze.getCellWidth()) - 1;

                System.out.println("row: " + row + ", column: " + column);
            }
        });
    }

    public Dimension getMinimumSize() {
        return size;
    }

    public Dimension getPreferredSize() {
        return size;
    }

    protected void paintComponent(final Graphics graphics) {
        if (image == null) {
            if (SHOW_START_END_POINTS) {
                graphics.setColor(Color.YELLOW);
                graphics.fillOval(maze.getCellWidth() * maze.getStartPoint().x,
                                  maze.getCellHeight() * maze.getStartPoint().y, 10, 10);
                graphics.fillOval(maze.getCellWidth() * maze.getEndPoint().x,
                                  maze.getCellHeight() * maze.getEndPoint().y, 10, 10);
            }

            drawLines(graphics);

            if (maze.getVisitedPoints() != null) {
                int pointWidth = maze.getCellWidth() - ((maze.getCellWidth() > 6) ? 5 : 1);
                int pointHeight = maze.getCellHeight() - ((maze.getCellWidth() > 6) ? 5 : 1);
                int offsetWidth = maze.getCellWidth() > 6 ? 3 : 1;
                int offsetHeight = maze.getCellWidth() > 6 ? 3 : 1;

                drawVisitedPoints(graphics, pointWidth, pointHeight, offsetWidth, offsetHeight);

                if (SHOW_DEAD_END_PARTS)
                    if (maze.getDeadEndPoints() != null)
                        drawDeadEndPoints(graphics, pointWidth, pointHeight, offsetWidth, offsetHeight);
            }
        } else
            graphics.drawImage(image, 10, 10, null);
    }

    private void drawLines(final Graphics graphics) {
        final Color colorRegularLines = Color.BLACK;
//        final Color colorStartEndPoint = Color.MAGENTA;
//        final Color colorRegularLines = Color.LIGHT_GRAY;
//        final Color colorStartEndPoint = Color.BLACK;

        graphics.setColor(colorRegularLines);

//        System.out.println("maze.getMazeHeight(): " + maze.getMazeHeight());

        for (int y = 0; y < (maze.getMazeHeight() + 1); y++)
            for (int x = 0; x < (maze.getMazeWidth() + 1); x++) {
                if (TEST_MODE)
                    if (maze.getCrossPoints()[y][x])
                        graphics.drawLine(maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 1),
                                          maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 1));

                final boolean startPoint = (x == maze.getStartPoint().x) && (y == maze.getStartPoint().y);
                final boolean endPoint = ((x - 1) == maze.getEndPoint().x) && (y == maze.getEndPoint().y);
                final boolean regularPoint = !startPoint && !endPoint;

//                if (y == 98 && !regularPoint)
//                    System.out.println("x: " + x + "; y: " + y + "; start point x: " + maze.getStartPoint().x + ", y: "
//                                       + maze.getStartPoint().y + " - startPoint: " + startPoint
//                                       + "; endPoint: " + endPoint);

                if (x < maze.getMazeWidth() && maze.getHorizontalLines()[y][x] && regularPoint)
                    graphics.drawLine(maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 1),
                                      maze.getCellWidth() * (x + 2), maze.getCellHeight() * (y + 1));
//                else if (!regularPoint) {
//                    System.out.println("Draw start or end point [1] - x: " + x + "; y: " + y);
//                    graphics.setColor(colorStartEndPoint);
//                    graphics.drawLine(maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 1),
//                                      maze.getCellWidth() * (x + 2), maze.getCellHeight() * (y + 1));
//                    graphics.setColor(colorRegularLines);
//                }

                if (y < maze.getMazeHeight() && maze.getVerticalLines()[y][x] && regularPoint)
                    graphics.drawLine(maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 1),
                                      maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 2));
//                else if (!regularPoint) {
//                    System.out.println("Draw start or end point [2] - x: " + x + "; y: " + y);
//                    graphics.setColor(colorStartEndPoint);
//                    graphics.drawLine(maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 1),
//                                      maze.getCellWidth() * (x + 1), maze.getCellHeight() * (y + 2));
//                    graphics.setColor(colorRegularLines);
//                }
            }
    }

    private void drawVisitedPoints(final Graphics graphics, final int pointWidth, final int pointHeight,
                                   final int offsetWidth, final int offsetHeight) {
        graphics.setColor(COLOR_VISITED_POINTS);

        Point previousPoint = null;
        final List<Point> visitedPoints = new ArrayList<>(maze.getVisitedPoints());
        for (final Point visitedPoint : visitedPoints) {
            graphics.fillRect(maze.getCellWidth() * (visitedPoint.x + 1) + offsetWidth,
                              maze.getCellHeight() * (visitedPoint.y + 1) + offsetHeight,
                              pointWidth,
                              pointHeight);

            if (previousPoint != null) {
                int xLeft = Math.min(maze.getCellWidth() * (previousPoint.x + 1) + offsetWidth,
                                     maze.getCellWidth() * (visitedPoint.x + 1) + offsetWidth);
                int xRight = Math.max(maze.getCellWidth() * (previousPoint.x + 1) + offsetWidth + pointWidth - 1,
                                      maze.getCellWidth() * (visitedPoint.x + 1) + offsetWidth + pointWidth - 1);
                int yTop = Math.min(maze.getCellHeight() * (previousPoint.y + 1) + offsetHeight,
                                    maze.getCellHeight() * (visitedPoint.y + 1) + offsetHeight);
                int yBottom = Math.max(maze.getCellHeight() * (previousPoint.y + 1) + offsetHeight + pointHeight - 1,
                                       maze.getCellHeight() * (visitedPoint.y + 1) + offsetHeight + pointHeight - 1);

                graphics.fillRect(xLeft, yTop, xRight - xLeft + 1, yBottom - yTop + 1);
            }

            previousPoint = visitedPoint;
        }
    }

    private void drawDeadEndPoints(final Graphics graphics, final int pointWidth, final int pointHeight,
                                   final int offsetWidth, final int offsetHeight) {
        graphics.setColor(Color.RED);

        for (final Point deadEndPoint : maze.getDeadEndPoints())
            graphics.fillRect(maze.getCellWidth() * (deadEndPoint.x + 1) + offsetWidth,
                              maze.getCellHeight() * (deadEndPoint.y + 1) + offsetHeight,
                              pointWidth,
                              pointHeight);
    }
}
