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
 * Tool to draw rectangle.
 * 
 * @author ericyager
 */
public class RectangleTool extends DrawTool {

    private double startx;
    private double starty;
    private Image initialCanvas;
    private Callable<Double> arcWidthGetter;

    public RectangleTool(Callable<Double> arcWidthGetter) {
        super();
        this.arcWidthGetter = arcWidthGetter;
    }
    
    public RectangleTool(Canvas canvas, Callable<Double> arcWidthGetter) {
        super(canvas);
        this.arcWidthGetter = arcWidthGetter;
    }

    /**
     * 
     * @param e 
     */
    @Override
    public void onMousePressed(MouseEvent e) {
        startx = e.getX();
        starty = e.getY();
        initialCanvas = ImageTransformer.canvasToImage(super.getCanvas());
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
        if (e.getX() < startx && e.getY() < starty) { //If the user dragged left after clicking
            if (super.getCanvas().getGraphicsContext2D().getLineCap().equals(StrokeLineCap.ROUND)) {
                try {
                    super.getCanvas().getGraphicsContext2D().fillRoundRect(e.getX(), e.getY(), startx-e.getX(), starty-e.getY(), arcWidthGetter.call(), arcWidthGetter.call());
                    super.getCanvas().getGraphicsContext2D().strokeRoundRect(e.getX(), e.getY(), startx-e.getX(), starty-e.getY(), arcWidthGetter.call(), arcWidthGetter.call());
                } catch (Exception ex) {
                    super.getCanvas().getGraphicsContext2D().fillRect(e.getX(), e.getY(), startx-e.getX(), starty-e.getY());
                    super.getCanvas().getGraphicsContext2D().strokeRect(e.getX(), e.getY(), startx-e.getX(), starty-e.getY());
                }
            } else {
                super.getCanvas().getGraphicsContext2D().fillRect(e.getX(), e.getY(), startx-e.getX(), starty-e.getY());
                super.getCanvas().getGraphicsContext2D().strokeRect(e.getX(), e.getY(), startx-e.getX(), starty-e.getY());
            }
        } else if (e.getX() < startx && e.getY() > starty) {
            if (super.getCanvas().getGraphicsContext2D().getLineCap().equals(StrokeLineCap.ROUND)) {
                try {
                    super.getCanvas().getGraphicsContext2D().fillRoundRect(e.getX(), starty, startx-e.getX(), e.getY()-starty, arcWidthGetter.call(), arcWidthGetter.call());
                    super.getCanvas().getGraphicsContext2D().strokeRoundRect(e.getX(), starty, startx-e.getX(), e.getY()-starty, arcWidthGetter.call(), arcWidthGetter.call());
                } catch (Exception ex) {
                    super.getCanvas().getGraphicsContext2D().fillRect(e.getX(), starty, startx-e.getX(), e.getY()-starty);
                    super.getCanvas().getGraphicsContext2D().strokeRect(e.getX(), starty, startx-e.getX(), e.getY()-starty);
                }
            } else {
                super.getCanvas().getGraphicsContext2D().fillRect(e.getX(), starty, startx-e.getX(), e.getY()-starty);
                super.getCanvas().getGraphicsContext2D().strokeRect(e.getX(), starty, startx-e.getX(), e.getY()-starty);
            }
        } else if (e.getX() > startx && e.getY() < starty) {
            if (super.getCanvas().getGraphicsContext2D().getLineCap().equals(StrokeLineCap.ROUND)) {
                try {
                    super.getCanvas().getGraphicsContext2D().fillRoundRect(startx, e.getY(), e.getX()-startx, starty-e.getY(), arcWidthGetter.call(), arcWidthGetter.call());
                    super.getCanvas().getGraphicsContext2D().strokeRoundRect(startx, e.getY(), e.getX()-startx, starty-e.getY(), arcWidthGetter.call(), arcWidthGetter.call());
                } catch (Exception ex) {
                    super.getCanvas().getGraphicsContext2D().fillRect(startx, e.getY(), e.getX()-startx, starty-e.getY());
                    super.getCanvas().getGraphicsContext2D().strokeRect(startx, e.getY(), e.getX()-startx, starty-e.getY());
                }
            } else {
                super.getCanvas().getGraphicsContext2D().fillRect(startx, e.getY(), e.getX()-startx, starty-e.getY());
                super.getCanvas().getGraphicsContext2D().strokeRect(startx, e.getY(), e.getX()-startx, starty-e.getY());
            }
        } else { //e.getX() > startx && e.getY() > starty
            if (super.getCanvas().getGraphicsContext2D().getLineCap().equals(StrokeLineCap.ROUND)) {
                try {
                    super.getCanvas().getGraphicsContext2D().fillRoundRect(startx, starty, e.getX()-startx, e.getY()-starty, arcWidthGetter.call(), arcWidthGetter.call());
                    super.getCanvas().getGraphicsContext2D().strokeRoundRect(startx, starty, e.getX()-startx, e.getY()-starty, arcWidthGetter.call(), arcWidthGetter.call());
                } catch (Exception ex) {
                    super.getCanvas().getGraphicsContext2D().fillRect(startx, starty, e.getX()-startx, e.getY()-starty);
                    super.getCanvas().getGraphicsContext2D().strokeRect(startx, starty, e.getX()-startx, e.getY()-starty);
                }
            } else {
                super.getCanvas().getGraphicsContext2D().fillRect(startx, starty, e.getX()-startx, e.getY()-starty);
                super.getCanvas().getGraphicsContext2D().strokeRect(startx, starty, e.getX()-startx, e.getY()-starty);
            }
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
    
}
