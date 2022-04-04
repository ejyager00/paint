/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

/**
 * Tool to free draw.
 * 
 * @author ericyager
 */
public class FreeDrawTool extends DrawTool {
    
    private double startx;
    private double starty;

    public FreeDrawTool() {
        super();
    }
    
    public FreeDrawTool(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        startx = e.getX();
        starty = e.getY();
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        super.getCanvas().getGraphicsContext2D().strokeLine(startx, starty, e.getX(), e.getY());
        startx = e.getX();
        starty = e.getY();
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        //DO NOTHING
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        super.getCanvas().getGraphicsContext2D().strokeLine(startx, starty, e.getX(), e.getY());
    }
    
    /**
     * Do nothing, needed for some draw tools but not all.
     */
    @Override
    public void cancelAction() {
        //DO NOTHING
    }
    
}
