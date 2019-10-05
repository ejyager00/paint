/*
 * Eric Yager
 */
package paint;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class with methods for modifying images.
 *
 * @author ericyager
 */
public class ImageTransformer {

    /**
     * Scales an image from a PixelReader to the given new dimensions. Uses a
     * bilinear image scaling algorithm.
     *
     * @param picture Image to scale
     * @param w2 new width
     * @param h2 new height
     * @return Scaled version of the image from the pixels
     */
    public static WritableImage bilinearScale(Image picture, int w2, int h2) {

        PixelReader pixels = picture.getPixelReader();
        int w = (int) picture.getWidth();
        int h = (int) picture.getHeight();
        WritableImage image = new WritableImage(w2, h2); //image variable where the final image is saved
        int x, y; //current pixel coordinates
        Color a, b, c, d;
        double x_ratio = ((double) (w - 1)) / w2; //ratios for scaling 
        double y_ratio = ((double) (h - 1)) / h2;
        double x_diff, y_diff, blue, red, green;
        int offset = 0;
        for (int i = 0; i < h2; i++) { //row
            for (int j = 0; j < w2; j++) { //column
                x = (int) (x_ratio * j);
                y = (int) (y_ratio * i);
                x_diff = (x_ratio * j) - x;
                y_diff = (y_ratio * i) - y;
                a = pixels.getColor(x, y);
                b = pixels.getColor(x + 1, y);
                c = pixels.getColor(x, y + 1);
                d = pixels.getColor(x + 1, y + 1);

                // blue element
                blue = a.getBlue() * (1 - x_diff) * (1 - y_diff) + b.getBlue() * (x_diff) * (1 - y_diff)
                        + c.getBlue() * (y_diff) * (1 - x_diff) + d.getBlue() * (x_diff * y_diff);

                // green element
                green = a.getGreen() * (1 - x_diff) * (1 - y_diff) + b.getGreen() * (x_diff) * (1 - y_diff)
                        + c.getGreen() * (y_diff) * (1 - x_diff) + d.getGreen() * (x_diff * y_diff);

                // red element
                red = a.getRed() * (1 - x_diff) * (1 - y_diff) + b.getRed() * (x_diff) * (1 - y_diff)
                        + c.getRed() * (y_diff) * (1 - x_diff) + d.getRed() * (x_diff * y_diff);

                //correct for annoying errors with doubles
                if (red > 1) {
                    red = 1;
                }
                if (green > 1) {
                    green = 1;
                }
                if (blue > 1) {
                    blue = 1;
                }

                image.getPixelWriter().setColor(offset % w2, offset / w2, new Color(red, green, blue, 1));
                offset++;
            }
        }
        return image;
    }

    /**
     * Scales an image from a PixelReader to the given new dimensions. Uses a
     * bicubic image scaling algorithm. Work in progress, not operational as of
     * current version.
     *
     * @param picture Image to scale
     * @param w2 new width
     * @param h2 new height
     * @return Scaled version of the image from the pixels
     */
    private static WritableImage bicubicScale(Image picture, int w2, int h2) {

        return null;

    }

    /**
     * Convert a canvas to a writable image.
     *
     * @param canvas canvas to convert
     * @return writable image containing a snapshot of the canvas
     */
    public static WritableImage canvasToImage(Canvas canvas) {

        //create image to save to
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        //save to image
        canvas.snapshot(null, snapshot);
        return snapshot;

    }

    /**
     * Convert whitespace to transparent.
     * 
     * @param image WritableImage containing image to convert
     * @param transparentPixels 2d boolean array indicating which pixels could be transparent
     * @return image with transparency instead of whitespace
     */
    public static WritableImage convertToTransparent(WritableImage image, boolean[][] transparentPixels) {

        PixelReader pixels = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                //if the pixel is white and should be transparent
                if (pixels.getArgb(i, j) == -1 && transparentPixels[i][j]) {
                    image.getPixelWriter().setArgb(i, j, 0);
                }
            }
        }
        return image;

    }

    /**
     * Checks for transparent pixels and returns an ArrayList of the coordinates
     * of any transparent pixels. Returns null if there are none.
     *
     * @param image Image to check for transparent pixels
     * @return array of boolean arrays containing coordinates of transparent
     * pixels
     */
    public static boolean[][] getTransparentPixels(Image image) {

        boolean[][] array = new boolean[(int) image.getWidth()][(int) image.getHeight()];
        boolean returnNull = true;

        PixelReader pixels = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                //if the pixel is transparent
                if (pixels.getArgb(i, j) == 0) {
                    array[i][j] = true;
                    //if there is a transparent pixel, don't return null
                    returnNull = false;
                } else {
                    array[i][j] = false;
                }
            }
        }
        //if there were no transparent pixels
        if (returnNull) {
            return null;
        }
        return array;

    }

}
