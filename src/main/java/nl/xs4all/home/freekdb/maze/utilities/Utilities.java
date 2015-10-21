package nl.xs4all.home.freekdb.maze.utilities;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 */
public class Utilities {
    /**
     * Converts an image into a buffered image.
     *
     * @param image the image to be converted.
     * @return the converted buffered image.
     */
    public static BufferedImage toBufferedImage(final Image image) {
        final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                                                              BufferedImage.TYPE_INT_ARGB);

        // Draw the image onto the buffered image.
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();

        return bufferedImage;
    }

    /**
     * Set the size and location of the frame.
     *
     * @param frame    the frame to position.
     * @param viewSize the preferred size of the scrollable view.
     * @param addX     the amount of width to add to the view.
     * @param addY     the amount of height to add to the view.
     */
    public static void positionFrame(final JFrame frame, final Dimension viewSize, final int addX, final int addY) {
        final GraphicsDevice screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final DisplayMode displayMode = screenDevice.getDisplayMode();
        final Dimension screenSize = new Dimension(displayMode.getWidth(), displayMode.getHeight());

        final Dimension frameSize = new Dimension(Math.min(viewSize.width + addX, screenSize.width),
                                                  Math.min(viewSize.height + addY, screenSize.height));

        final int frameX = Math.max((screenSize.width - frameSize.width) / 2, 0);
        final int frameY = Math.max((screenSize.height - frameSize.height) / 2, 0);

        frame.setSize(frameSize);
        frame.setLocation(frameX, frameY);
    }
}
