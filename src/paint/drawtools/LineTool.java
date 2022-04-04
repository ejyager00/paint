/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import paint.ImageTransformer;

/**
 * Tool to draw line.
 * 
 * @author ericyager
 */
public class LineTool extends DrawTool {
    
    private double startx;
    private double starty;
    private Image initialCanvas;

    public LineTool() {
        super();
    }
    
    public LineTool(Canvas canvas) {
        super(canvas);
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
        super.getCanvas().getGraphicsContext2D().strokeLine(startx, starty, e.getX(), e.getY());
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
