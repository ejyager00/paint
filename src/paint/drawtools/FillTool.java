/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import paint.ImageTransformer;

/**
 * Tool to fill a space of like color.
 * 
 * @author ericyager
 */
public class FillTool extends DrawTool {

    public FillTool() {
        super();
    }

    public FillTool(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        //DO NOTHING
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
        fill(ImageTransformer.canvasToImage(super.getCanvas()), (int) e.getX(), (int) e.getY(), oldArgb, super.getCanvas());
    }
    
    /**
     * Do nothing, needed for some draw tools but not all.
     */
    @Override
    public void cancelAction() {
        //DO NOTHING
    }
    
    /**
     * Fills all of an area that shares a color with a new color.
     *
     * @param image WritableImage containing snapshot of canvas
     * @param x int x coordinate of click point
     * @param y int y coordinate of click point
     * @param originalColor int argb color of original click point
     * @param canvas Canvas on which to operate fill.
     */
    private void fill(WritableImage image, int x, int y, int originalColor, Canvas canvas) {

        ArrayList<int[]> pixels = new ArrayList<>(); //keeps track of which pixels' neighbors must be checked
        pixels.add(new int[]{x, y}); //add initial point
        for (int i = 0; i < pixels.size(); i++) { //until we've exausted potential pixels
            x = pixels.get(i)[0]; //get current pixel
            y = pixels.get(i)[1];
            for (int j = 0; j < 9; j++) { //for all neighbors
                if (x - 1 + j % 3 >= 0 && x - 1 + j % 3 < canvas.getWidth() // within x bounds of canvas
                        && y - 1 + j / 3 >= 0 && y - 1 + j / 3 < canvas.getHeight() //with y bounds of canvas
                        && image.getPixelReader().getArgb(x - 1 + j % 3, y - 1 + j / 3) == originalColor) { //matches old color
                    canvas.getGraphicsContext2D().fillRect(x - 1 + j % 3, y - 1 + j / 3, 1, 1); //update pixel on canvas
                    image.getPixelWriter().setArgb(x - 1 + j % 3, y - 1 + j / 3, originalColor + 2); //update pixel on image
                    pixels.add(new int[]{x - 1 + j % 3, y - 1 + j / 3}); //add to pixels to check neighbors
                }
            }
        }
    }
    
}
