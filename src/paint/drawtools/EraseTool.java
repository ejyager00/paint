/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Tool to erase.
 * 
 * @author ericyager
 */
public class EraseTool extends DrawTool {
    
    private double startx;
    private double starty;
    private Paint strokeColor;

    public EraseTool() {
        super();
    }
    
    public EraseTool(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        strokeColor = super.getCanvas().getGraphicsContext2D().getStroke();
        super.getCanvas().getGraphicsContext2D().setStroke(Color.WHITE);
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
        super.getCanvas().getGraphicsContext2D().setStroke(strokeColor);
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
