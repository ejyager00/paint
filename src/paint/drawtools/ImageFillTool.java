/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import paint.ImageTransformer;

/**
 * Tool to fill an arbitrary size space of like color with an image.
 * 
 * @author ericyager
 */
public class ImageFillTool extends DrawTool {
    
    private double originalX;
    private double originalY;
    private Callable<Image> imageGetter;
    private Paint tempColor;

    public ImageFillTool(Callable<Image> imageGetter) {
        super();
        this.imageGetter = imageGetter;
    }

    public ImageFillTool(Canvas canvas, Callable<Image> imageGetter) {
        super(canvas);
        this.imageGetter = imageGetter;
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        tempColor = super.getCanvas().getGraphicsContext2D().getFill();
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        //DO NOTHING
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        //DO NOTHING
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int oldArgb = ImageTransformer.canvasToImage(super.getCanvas()).getPixelReader().getArgb((int) e.getX(), (int) e.getY());
        try {
            imageFill(ImageTransformer.canvasToImage(super.getCanvas()), (int) e.getX(), (int) e.getY(), oldArgb, super.getCanvas(), imageGetter.call());
        } catch (Exception ex) {}
        super.getCanvas().getGraphicsContext2D().setFill(tempColor);
    }
    
    /**
     * Do nothing, needed for some draw tools but not all.
     */
    @Override
    public void cancelAction() {
        //DO NOTHING
    }
    
    /**
     * Fills all of an area that shares a color with an image.
     *
     * @param canvasImage WritableImage containing snapshot of canvas
     * @param x int x coordinate of click point
     * @param y int y coordinate of click point
     * @param originalColor int argb color of original click point
     * @param canvas Canvas on which to operate fill.
     * @param image image to fill with
     */
    private void imageFill(WritableImage canvasImage, int x, int y, int originalColor, Canvas canvas, Image image) {
        
        ArrayList<int[]> pixels = new ArrayList<>(); //keeps track of which pixels' neighbors must be checked
        WritableImage tempCanvasImage = new WritableImage(canvasImage.getPixelReader(), (int)canvasImage.getWidth(), (int)canvasImage.getHeight());
        int smallX = x;
        int smallY = y;
        int largeX = x;
        int largeY = y;
        originalX = x;
        originalY = y;
        pixels.add(new int[]{x, y}); //add initial point
        for (int i = 0; i < pixels.size(); i++) { //until we've exausted potential pixels
            x = pixels.get(i)[0]; //get current pixel
            y = pixels.get(i)[1];
            for (int j = 0; j < 9; j++) { //for all neighbors
                if (x - 1 + j % 3 >= 0 && x - 1 + j % 3 < canvas.getWidth() // within x bounds of canvas
                        && y - 1 + j / 3 >= 0 && y - 1 + j / 3 < canvas.getHeight() //with y bounds of canvas
                        && tempCanvasImage.getPixelReader().getArgb(x - 1 + j % 3, y - 1 + j / 3) == originalColor) { //matches old color
                    tempCanvasImage.getPixelWriter().setArgb(x - 1 + j % 3, y - 1 + j / 3, originalColor + 2); //update pixel on image
                    pixels.add(new int[]{x - 1 + j % 3, y - 1 + j / 3}); //add to pixels to check neighbors
                    if (x<smallX) {
                        smallX = x;
                    } else if (x>largeX) {
                        largeX = x;
                    }
                    if (y<smallY) {
                        smallY = y;
                    } else if (y>largeY) {
                        largeY = y;
                    }
                }
            }
        }
        image = ImageTransformer.bilinearScale(image, largeX-smallX+1, largeY-smallY+1);
        pixels.clear();
        pixels.add(new int[]{(int)originalX, (int)originalY});
        for (int i = 0; i < pixels.size(); i++) { //until we've exausted potential pixels
            x = pixels.get(i)[0]; //get current pixel
            y = pixels.get(i)[1];
            for (int j = 0; j < 9; j++) { //for all neighbors
                if (x - 1 + j % 3 >= 0 && x - 1 + j % 3 < canvas.getWidth() // within x bounds of canvas
                        && y - 1 + j / 3 >= 0 && y - 1 + j / 3 < canvas.getHeight() //with y bounds of canvas
                        && canvasImage.getPixelReader().getArgb(x - 1 + j % 3, y - 1 + j / 3) == originalColor) { //matches old color
                    Color color = image.getPixelReader().getColor(x-smallX, y-smallY);
                    canvas.getGraphicsContext2D().setFill(color);
                    canvas.getGraphicsContext2D().fillRect(x - 1 + j % 3, y - 1 + j / 3, 1, 1); //update pixel on canvas
                    canvasImage.getPixelWriter().setArgb(x - 1 + j % 3, y - 1 + j / 3, originalColor + 2); //update pixel on image
                    pixels.add(new int[]{x - 1 + j % 3, y - 1 + j / 3}); //add to pixels to check neighbors
                }
            }
        }
        
    }
    
}
