/*
 * Eric Yager
 */
package paint.drawtools;

import javafx.scene.canvas.Canvas;
import paintcanvas.DrawTool;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import paint.ImageTransformer;

/**
 * Tool to copy and paste.
 * 
 * @author ericyager
 */
public class SelectAndMoveTool extends DrawTool {

    private double startx;
    private double starty;
    private Image initialCanvas;
    private boolean imageSelected = false;
    private Image selectedImage;
    private double imagex;
    private double imagey;

    public SelectAndMoveTool() {
        super();
    }

    public SelectAndMoveTool(Canvas canvas) {
        super(canvas);
    }
    
    @Override
    public void onMousePressed(MouseEvent e) {
        if (!imageSelected) {
            startx = e.getX();
            starty = e.getY();
            initialCanvas = ImageTransformer.canvasToImage(super.getCanvas());
        } else {
            startx = e.getX() - imagex;
            starty = e.getY() - imagey;
        }
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
        if (!imageSelected) {
            if (e.getX() < startx && e.getY() < starty) { //If the user dragged left after clicking
                drawOutline(e.getX(), e.getY(), startx - e.getX(), starty - e.getY());
            } else if (e.getX() < startx && e.getY() > starty) {
                drawOutline(e.getX(), starty, startx - e.getX(), e.getY() - starty);
            } else if (e.getX() > startx && e.getY() < starty) {
                drawOutline(startx, e.getY(), e.getX() - startx, starty - e.getY());
            } else { //e.getX() > startx && e.getY() > starty
                drawOutline(startx, starty, e.getX() - startx, e.getY() - starty);
            }
        } else {
            imagex = e.getX() - startx;
            imagey = e.getY() - starty;
            super.getCanvas().getGraphicsContext2D().drawImage(selectedImage, imagex, imagey);
            drawOutline(imagex, imagey, selectedImage.getWidth(), selectedImage.getHeight());
        }
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        if (!imageSelected) {
            super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
            if (e.getX() < startx && e.getY() < starty) { //If the user dragged left after clicking
                selectedImage = new WritableImage(ImageTransformer.canvasToImage(super.getCanvas()).getPixelReader(),
                        (int) e.getX(), (int) e.getY(), (int) (startx - e.getX()), (int) (starty - e.getY()));
                imagex = e.getX() - 5;
                imagey = e.getY() - 5;
            } else if (e.getX() < startx && e.getY() > starty) {
                selectedImage = new WritableImage(ImageTransformer.canvasToImage(super.getCanvas()).getPixelReader(),
                        (int) e.getX(), (int) starty, (int) (startx - e.getX()), (int) (e.getY() - starty));
                imagex = e.getX() - 5;
                imagey = starty - 5;
            } else if (e.getX() > startx && e.getY() < starty) {
                selectedImage = new WritableImage(ImageTransformer.canvasToImage(super.getCanvas()).getPixelReader(),
                        (int) startx, (int) e.getY(), (int) (e.getX() - startx), (int) (starty - e.getY()));
                imagex = startx - 5;
                imagey = e.getY() - 5;
            } else { //e.getX() > startx && e.getY() > starty
                selectedImage = new WritableImage(ImageTransformer.canvasToImage(super.getCanvas()).getPixelReader(),
                        (int) startx, (int) starty, (int) (e.getX() - startx), (int) (e.getY() - starty));
                imagex = startx - 5;
                imagey = starty - 5;
            }
            imageSelected = true;
            super.getCanvas().getGraphicsContext2D().drawImage(selectedImage, imagex, imagey);
            drawOutline(imagex, imagey, selectedImage.getWidth(), selectedImage.getHeight());
        } else {
            super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
            super.getCanvas().getGraphicsContext2D().drawImage(selectedImage, imagex, imagey);
            imageSelected = false;
            selectedImage = null;
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        //DO NOTHING
    }
    
    /**
     * Restores original canvas if needed.
     */
    @Override
    public void cancelAction() {
        super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
    }

    /**
     * Tool to draw an outline around the image.
     * 
     * @param x left most edge
     * @param y top most edge
     * @param width width of image
     * @param height height of image
     */
    private void drawOutline(double x, double y, double width, double height) {

        Paint tempColor = super.getCanvas().getGraphicsContext2D().getStroke();
        double tempwidth = super.getCanvas().getGraphicsContext2D().getLineWidth();
        super.getCanvas().getGraphicsContext2D().setLineDashes(6.0, 4.0);
        super.getCanvas().getGraphicsContext2D().setStroke(Color.DARKGRAY);
        super.getCanvas().getGraphicsContext2D().setLineWidth(2);

        super.getCanvas().getGraphicsContext2D().strokeRect(x, y, width, height);

        super.getCanvas().getGraphicsContext2D().setLineDashes(null);
        super.getCanvas().getGraphicsContext2D().setStroke(tempColor);
        super.getCanvas().getGraphicsContext2D().setLineWidth(tempwidth);

    }

}
