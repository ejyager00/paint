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
 * Tool to draw a circle.
 * 
 * @author ericyager
 */
public class CircleTool extends DrawTool {
    
    private double startx;
    private double starty;
    private Image initialCanvas;

    public CircleTool() {
        super();
    }
    
    public CircleTool(Canvas canvas) {
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
        double radius = Math.sqrt((e.getX() - startx)*(e.getX() - startx)+(e.getY() - starty)*(e.getY() - starty));
        super.getCanvas().getGraphicsContext2D().fillOval(startx-radius, starty-radius,radius*2,radius*2);
        super.getCanvas().getGraphicsContext2D().strokeOval(startx-radius, starty-radius,radius*2,radius*2);
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
