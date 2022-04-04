/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import java.util.concurrent.Callable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import paint.ImageTransformer;
import paintcanvas.ShapeMath;

/**
 * Tool to draw a regular star.
 * 
 * @author ericyager
 */
public class StarTool extends DrawTool {
    
    private double startx;
    private double starty;
    private Image initialCanvas;
    private Callable<Integer> vertexNumberGetter;

    public StarTool(Callable<Integer> vertexNumberGetter) {
        super();
        this.vertexNumberGetter = vertexNumberGetter;
    }
    
    public StarTool(Canvas canvas, Callable<Integer> vertexNumberGetter) {
        super(canvas);
        this.vertexNumberGetter = vertexNumberGetter;
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
        double[][] starPoints;
        try {
            starPoints  = ShapeMath.getStarPointsTwoArrays(e.getX(), e.getY(), startx, starty, vertexNumberGetter.call());
            super.getCanvas().getGraphicsContext2D().fillPolygon(starPoints[0], starPoints[1], vertexNumberGetter.call()*2);
            super.getCanvas().getGraphicsContext2D().strokePolygon(starPoints[0], starPoints[1], vertexNumberGetter.call()*2);
        } catch (Exception ex) {}
        
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
