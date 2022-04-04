/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import java.util.concurrent.Callable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.StrokeLineCap;
import paint.ImageTransformer;

/**
 * Tool to draw a square.
 * 
 * @author ericyager
 */
public class SquareTool extends DrawTool {

    private double startx;
    private double starty;
    private Image initialCanvas;
    private Callable<Double> arcWidthGetter;
    
    public SquareTool(Callable<Double> arcWidthGetter) {
        super();
        this.arcWidthGetter = arcWidthGetter;
    }
    
    public SquareTool(Canvas canvas, Callable<Double> arcWidthGetter) {
        super(canvas);
        this.arcWidthGetter = arcWidthGetter;
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        startx = e.getX();
        starty = e.getY();
        initialCanvas = ImageTransformer.canvasToImage(super.getCanvas());
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
        boolean dragMoreHoriz = Math.abs(e.getX() - startx) > Math.abs(e.getY() - starty);
        if (e.getX() < startx && e.getY() < starty) {
            if (dragMoreHoriz) drawSquare(e.getX(), starty-startx+e.getX(), startx-e.getX());
            else drawSquare(startx-starty+e.getY(), e.getY(), starty-e.getY());
        } else if (e.getX() < startx && e.getY() > starty) {
            if (dragMoreHoriz) drawSquare(e.getX(), starty, startx-e.getX());
            else drawSquare(startx+starty-e.getY(), starty, e.getY()-starty);
        } else if (e.getX() > startx && e.getY() < starty) {
            if (dragMoreHoriz) drawSquare(startx, starty-e.getX()+startx, e.getX()-startx);
            else drawSquare(startx, e.getY(), starty-e.getY());
        } else { //e.getX() > startx && e.getY() > starty
            if (dragMoreHoriz) drawSquare(startx, starty, e.getX()-startx);
            else drawSquare(startx, starty, e.getY()-starty);
        }
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        //DO NOTHING
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        //DO NOTHING
    }
    
    /**
     * Do nothing, needed for some draw tools but not all.
     */
    @Override
    public void cancelAction() {
        //DO NOTHING
    }
    
    /**
     * Draw a square at specified location.
     * 
     * @param x left most edge
     * @param y top most edge
     * @param side side length
     */
    private void drawSquare(double x, double y, double side) {
        
        if (super.getCanvas().getGraphicsContext2D().getLineCap().equals(StrokeLineCap.ROUND)) {
            try {
                super.getCanvas().getGraphicsContext2D().fillRoundRect(x, y, side, side, arcWidthGetter.call(), arcWidthGetter.call());
                super.getCanvas().getGraphicsContext2D().strokeRoundRect(x, y, side, side, arcWidthGetter.call(), arcWidthGetter.call());
            } catch (Exception ex) {
                super.getCanvas().getGraphicsContext2D().fillRect(x, y, side, side);
                super.getCanvas().getGraphicsContext2D().strokeRect(x, y, side, side);
            }
        } else {
            super.getCanvas().getGraphicsContext2D().fillRect(x, y, side, side);
            super.getCanvas().getGraphicsContext2D().strokeRect(x, y, side, side);
        }
        
    }
    
}
