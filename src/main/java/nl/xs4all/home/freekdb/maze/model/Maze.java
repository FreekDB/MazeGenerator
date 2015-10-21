package nl.xs4all.home.freekdb.maze.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import nl.xs4all.home.freekdb.maze.view.MazeView;

/**
 */
public class Maze {
    // Four directions.
    private static final int LEFT = 0;
    private static final int RIGHT = 2;
    private static final int UP = 1;
    private static final int DOWN = 3;

    private static final boolean LOG_CROSS_POINTS = false;

    private final int cellHeight;
    private final int cellWidth;

    private final int mazeHeight;
    private final int mazeWidth;

    private final Point startPoint;
    private final Point endPoint;

    private long randomSeed;

    private final boolean[][] crossPoints;
    private final boolean[][] horizontalLines;
    private final boolean[][] verticalLines;

    private List<Point> visitedPoints;
    private Set<Point> deadEndPoints;

    public Maze(final MazeParameters mazeParameters) {
        this.cellHeight = mazeParameters.getCellHeight();
        this.cellWidth = mazeParameters.getCellWidth();

        this.mazeHeight = mazeParameters.getMazeHeight();
        this.mazeWidth = mazeParameters.getMazeWidth();

        this.startPoint = mazeParameters.getStartPoint();
        this.endPoint = mazeParameters.getEndPoint();

        this.randomSeed = mazeParameters.getRandomSeed();

        this.crossPoints = new boolean[mazeHeight + 1][mazeWidth + 1];
        this.horizontalLines = new boolean[mazeHeight + 1][mazeWidth];
        this.verticalLines = new boolean[mazeHeight][mazeWidth + 1];

        makeBorders();

        printCrossPoints();
    }

    public void makeBorders() {
        for (int x = 0; x < (mazeWidth + 1); x++) {
            crossPoints[0][x] = true;
            crossPoints[mazeHeight][x] = true;

            if (x < mazeWidth) {
                horizontalLines[0][x] = true;
                horizontalLines[mazeHeight][x] = true;
            }
        }

        for (int y = 0; y < (mazeHeight + 1); y++) {
            crossPoints[y][0] = true;
            crossPoints[y][mazeWidth] = true;

            if (y < mazeHeight) {
                verticalLines[y][0] = true;
                verticalLines[y][mazeWidth] = true;
            }
        }
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public boolean[][] getCrossPoints() {
        return crossPoints;
    }

    public boolean[][] getHorizontalLines() {
        return horizontalLines;
    }

    public boolean[][] getVerticalLines() {
        return verticalLines;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public List<Point> getVisitedPoints() {
        return visitedPoints;
    }

    public Set<Point> getDeadEndPoints() {
        return deadEndPoints;
    }

    /**
     * Fill all areas that are not black in the shape, so the maze will be generated in the black areas of the shape.
     *
     * @param shapeImage the shape image to determine which areas will be used for maze generation.
     */
    public void limitMazeAreaToShape(final BufferedImage shapeImage) {
        try {
            final int blackRGB = Color.BLACK.getRGB();

            for (int y = 0; y < (mazeHeight + 1); y++) {
                for (int x = 0; x < (mazeWidth + 1); x++) {
                    final int captureRGB = shapeImage.getRGB(cellWidth * (x + 1), cellHeight * (y + 1));
                    crossPoints[y][x] |= blackRGB != captureRGB;
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        printCrossPoints();

        // Draw the circumference of the shape with horizontal and vertical lines.
        for (int x = 0; x < mazeWidth; x++) {
            boolean previousLineOutsideShape = true;

            for (int y = 1; y < (mazeHeight + 1); y++) {
                boolean lineOutsideShape = crossPoints[y][x] && crossPoints[y][x + 1];

                if (previousLineOutsideShape && !lineOutsideShape)
                    horizontalLines[y - 1][x] = true;
                else if (!previousLineOutsideShape && lineOutsideShape)
                    horizontalLines[y][x] = true;

                previousLineOutsideShape = lineOutsideShape;
            }
        }

        for (int y = 0; y < mazeHeight; y++) {
            boolean previousLineOutsideShape = true;

            for (int x = 1; x < (mazeWidth + 1); x++) {
                boolean lineOutsideShape = crossPoints[y][x] && crossPoints[y + 1][x];

                if (previousLineOutsideShape && !lineOutsideShape)
                    verticalLines[y][x - 1] = true;
                else if (!previousLineOutsideShape && lineOutsideShape)
                    verticalLines[y][x] = true;

                previousLineOutsideShape = lineOutsideShape;
            }
        }
    }

    public void printCrossPoints() {
        if (LOG_CROSS_POINTS) {
            System.out.println();
            System.out.println("Row count: " + crossPoints.length + "; column count: " + crossPoints[0].length);
            System.out.println();
            for (boolean[] crossPointRow : crossPoints) {
                for (boolean crossPoint : crossPointRow)
                    System.out.print(crossPoint ? '*' : ' ');
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
    }

    /**
     * Generate a random maze.
     */
    public void generateMaze() {
        if (randomSeed == 0) {
            randomSeed = System.currentTimeMillis();

            System.out.println("Maze.generateMaze - randomSeed: " + randomSeed);
        }

        final Random randomGenerator = new Random(randomSeed);

        for (int curlyLineIndex = 0; curlyLineIndex < (mazeHeight * mazeWidth); curlyLineIndex++) {
            final Point startPointCurlyLine = getStartPointCurlyLine(randomGenerator);

            if (startPointCurlyLine != null)
                createCurlyLine(randomGenerator, startPointCurlyLine);
        }

        fillRemainingHoles(randomGenerator);
    }

    /**
     * Try to find a new starting point for a curly line.
     *
     * @param randomGenerator the random number generator to use.
     * @return the start point of the curly line or null if the initial point was not empty.
     */
    private Point getStartPointCurlyLine(final Random randomGenerator) {
        final Point startPoint;

        // Find an empty point to start searching.
        int x = 1 + randomGenerator.nextInt(mazeWidth - 1);
        int y = 1 + randomGenerator.nextInt(mazeHeight - 1);

        if (crossPoints[y][x])
            startPoint = null;
        else {
            final int direction = randomGenerator.nextInt(4);
            int xStep = (direction == LEFT) ? -1 : ((direction == RIGHT) ? 1 : 0);
            int yStep = (direction == UP) ? -1 : ((direction == DOWN) ? 1 : 0);

            // Find a point to connect to.
            while (!crossPoints[y][x]) {
                x += xStep;
                y += yStep;
            }

            x -= xStep;
            y -= yStep;

            switch (direction) {
                case LEFT:
                    horizontalLines[y][x - 1] = true;
                    break;

                case RIGHT:
                    horizontalLines[y][x] = true;
                    break;

                case UP:
                    verticalLines[y - 1][x] = true;
                    break;

                case DOWN:
                    verticalLines[y][x] = true;
                    break;
            }

            crossPoints[y][x] = true;

            startPoint = new Point(x, y);
        }

        return startPoint;
    }

    /**
     * Create a curly line.
     *
     * @param randomGenerator     the random number generator to use.
     * @param startPointCurlyLine the start point for this curly line.
     */
    private void createCurlyLine(final Random randomGenerator, final Point startPointCurlyLine) {
        int x = startPointCurlyLine.x;
        int y = startPointCurlyLine.y;

        boolean leftPossible = x > 0 && !crossPoints[y][x - 1];
        boolean rightPossible = x < mazeWidth && !crossPoints[y][x + 1];
        boolean upPossible = y > 0 && !crossPoints[y - 1][x];
        boolean downPossible = y < mazeHeight && !crossPoints[y + 1][x];

        while (leftPossible || rightPossible || upPossible || downPossible) {
            final Point nextPoint = addCurlyLineSegment(randomGenerator, x, y, leftPossible, rightPossible, upPossible,
                                                        downPossible);

            x = nextPoint.x;
            y = nextPoint.y;

            leftPossible = x > 0 && !crossPoints[y][x - 1];
            rightPossible = x < mazeWidth && !crossPoints[y][x + 1];
            upPossible = y > 0 && !crossPoints[y - 1][x];
            downPossible = y < mazeHeight && !crossPoints[y + 1][x];
        }
    }

    /**
     * Add a curly line segment.
     *
     * @param randomGenerator the random number generator to use.
     * @param x               the current x coordinate: start of this segment.
     * @param y               the current y coordinate: start of this segment.
     * @param leftPossible    whether moving left is possible.
     * @param rightPossible   whether moving right is possible.
     * @param upPossible      whether moving up is possible.
     * @param downPossible    whether moving down is possible.
     * @return the next point in the curly line (end of this segment).
     */
    private Point addCurlyLineSegment(final Random randomGenerator, int x, int y, final boolean leftPossible,
                                      final boolean rightPossible, final boolean upPossible,
                                      final boolean downPossible) {
        switch (getNextDirection(randomGenerator, leftPossible, rightPossible, upPossible, downPossible)) {
            case LEFT:
                horizontalLines[y][x - 1] = true;
                x--;
                break;

            case RIGHT:
                horizontalLines[y][x] = true;
                x++;
                break;

            case UP:
                verticalLines[y - 1][x] = true;
                y--;
                break;

            case DOWN:
                verticalLines[y][x] = true;
                y++;
                break;
        }

        crossPoints[y][x] = true;

        return new Point(x, y);
    }

    /**
     * Find the direction for the next segment of the curly line.
     *
     * @param randomGenerator the random number generator to use.
     * @param leftPossible    whether moving left is possible.
     * @param rightPossible   whether moving right is possible.
     * @param upPossible      whether moving up is possible.
     * @param downPossible    whether moving down is possible.
     * @return the direction.
     */
    private int getNextDirection(final Random randomGenerator, final boolean leftPossible, final boolean rightPossible,
                                 final boolean upPossible, final boolean downPossible) {
        boolean directionFound = false;
        int direction = -1;

        while (!directionFound) {
            direction = randomGenerator.nextInt(4);

            switch (direction) {
                case LEFT:
                    directionFound = leftPossible;
                    break;

                case RIGHT:
                    directionFound = rightPossible;
                    break;

                case UP:
                    directionFound = upPossible;
                    break;

                case DOWN:
                    directionFound = downPossible;
                    break;
            }
        }

        return direction;
    }

    /**
     * Fill the remaining holes in the maze.
     *
     * @param randomGenerator the random number generator to use.
     */
    private void fillRemainingHoles(final Random randomGenerator) {
        for (int y = 1; y < (mazeHeight + 1); y++)
            for (int x = 1; x < (mazeWidth + 1); x++)
                if (!crossPoints[y][x]) {
                    crossPoints[y][x] = true;

                    if (randomGenerator.nextBoolean())
                        horizontalLines[y][x - 1] = true;
                    else
                        verticalLines[y - 1][x] = true;
                }
    }

    public boolean solveMazeRecursive(final MazeView mazeView, final Point currentPoint) {
        if (visitedPoints == null) {
            visitedPoints = new ArrayList<>();
            deadEndPoints = new HashSet<>();
        }

        final int MAXIMUM_STEP_COUNT = 6420;

        final int x = currentPoint.x;
        final int y = currentPoint.y;

        visitedPoints.add(currentPoint);

        if (visitedPoints.size() % 50 == 0)
            mazeView.repaint();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentPoint.equals(getEndPoint())) {
            mazeView.repaint();

            return true;
        } else if (visitedPoints.size() < MAXIMUM_STEP_COUNT) {
            boolean leftPossible = !verticalLines[y][x];
            boolean leftPossibleNew = leftPossible && !visitedPoints.contains(new Point(x - 1, y))
                                         && !deadEndPoints.contains(new Point(x - 1, y));
            boolean rightPossible = !verticalLines[y][x + 1];
            boolean rightPossibleNew = rightPossible && !visitedPoints.contains(new Point(x + 1, y))
                                          && !deadEndPoints.contains(new Point(x + 1, y));
            boolean upPossible = !horizontalLines[y][x];
            boolean upPossibleNew = upPossible && !visitedPoints.contains(new Point(x, y - 1))
                                         && !deadEndPoints.contains(new Point(x, y - 1));
            boolean downPossible = !horizontalLines[y + 1][x];
            boolean downPossibleNew = downPossible && !visitedPoints.contains(new Point(x, y + 1))
                                         && !deadEndPoints.contains(new Point(x, y + 1));

            if (leftPossibleNew && solveMazeRecursive(mazeView, new Point(x - 1, y)))
                return true;

            if (rightPossibleNew && solveMazeRecursive(mazeView, new Point(x + 1, y)))
                return true;

            if (upPossibleNew && solveMazeRecursive(mazeView, new Point(x, y - 1)))
                return true;

            if (downPossibleNew && solveMazeRecursive(mazeView, new Point(x, y + 1)))
                return true;
        }

        deadEndPoints.add(currentPoint);

        // Remove the last visited point.
        if (visitedPoints.size() > 0)
            visitedPoints.remove(visitedPoints.size() - 1);

        return false;
    }
}


//    public void randomWalkInMaze(final MazeView mazeView) {
//        final int MAXIMUM_STEP_COUNT = 100;
//
//        if (startPoint.x < Integer.MAX_VALUE) {
//            randomSeed = System.currentTimeMillis();
//
//            System.out.println("Maze.randomWalkInMaze - randomSeed: " + randomSeed);
//        } else
//            randomSeed = 1082906317765L;
//
//        Random randomGenerator = new Random(randomSeed);
//
//        int x = startPoint.x;
//        int y = startPoint.y;
//
//        int stepIndex = 0;
//
//        visitedPoints.add(new Point(x, y));
//        stepIndex++;
//
//        while (stepIndex < MAXIMUM_STEP_COUNT) {
//            boolean leftPossible = !verticalLines[y][x];
//            boolean rightPossible = !verticalLines[y][x + 1];
//            boolean upPossible = !horizontalLines[y][x];
//            boolean downPossible = !horizontalLines[y + 1][x];
//
//            switch (getNextDirection(randomGenerator, leftPossible, rightPossible, upPossible, downPossible)) {
//                case LEFT:
//                    x--;
//                    break;
//
//                case RIGHT:
//                    x++;
//                    break;
//
//                case UP:
//                    y--;
//                    break;
//
//                case DOWN:
//                    y++;
//                    break;
//            }
//
//            visitedPoints.add(new Point(x, y));
//
//            mazeView.repaint();
//
//            stepIndex++;
//        }
//    }
