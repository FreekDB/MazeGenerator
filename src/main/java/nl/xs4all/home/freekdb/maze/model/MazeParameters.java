package nl.xs4all.home.freekdb.maze.model;

import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Class with all parameters to generate a maze.
 */
public class MazeParameters {
    private static final Map<String, MazeParameters> PRECONFIGURED_PARAMETERS = getPreconfiguredParametersMap();

    private final Image shapeImage;
    private final int cellHeight;
    private final int cellWidth;
    private final int mazeHeight;
    private final int mazeWidth;
    private final Point startPoint;
    private final Point endPoint;
    private final long randomSeed;

    public MazeParameters(final String imagePath, final int cellHeight, final int cellWidth, final Point startPoint,
                          final Point endPoint, final long randomSeed) {
        this.shapeImage = new ImageIcon(imagePath).getImage();
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.mazeHeight = (this.shapeImage.getHeight(null) - 6) / cellHeight;
        this.mazeWidth = (this.shapeImage.getWidth(null) - 6) / cellWidth;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.randomSeed = randomSeed;
    }

    public Image getShapeImage() {
        return shapeImage;
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

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    private static Map<String, MazeParameters> getPreconfiguredParametersMap() {
        final Map<String, MazeParameters> map = new HashMap<>();

        add(map, "Test", "etc/unit-test.png", 2, 2, new Point(6, 18), new Point(230, 45), 654321);

        return map;
    }

    private static void add(final Map<String, MazeParameters> parametersMap, final String key, final String imagePath,
                            final int cellHeight, final int cellWidth, final Point startPoint, final Point endPoint,
                            final int randomSeed) {
        parametersMap.put(key, new MazeParameters(imagePath, cellHeight, cellWidth, startPoint, endPoint, randomSeed));
    }

    public static MazeParameters getPreconfiguredParameters(final String key) {
        return PRECONFIGURED_PARAMETERS.get(key);
    }
}
