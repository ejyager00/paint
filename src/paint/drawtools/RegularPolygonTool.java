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
 * Tool to draw regular polygon.
 * 
 * @author ericyager
 */
public class RegularPolygonTool extends DrawTool {
    
    private double startx;
    private double starty;
    private Image initialCanvas;
    private Callable<Integer> vertexNumberGetter;

    public RegularPolygonTool(Callable<Integer> vertexNumberGetter) {
        super();
        this.vertexNumberGetter = vertexNumberGetter;
    }
    
    public RegularPolygonTool(Canvas canvas, Callable<Integer> vertexNumberGetter) {
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
        double[][] polygonPoints;
        try {
            polygonPoints  = ShapeMath.getPolygonPointsTwoArrays(e.getX(), e.getY(), startx, starty, vertexNumberGetter.call());
            super.getCanvas().getGraphicsContext2D().fillPolygon(polygonPoints[0], polygonPoints[1], vertexNumberGetter.call());
            super.getCanvas().getGraphicsContext2D().strokePolygon(polygonPoints[0], polygonPoints[1], vertexNumberGetter.call());
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
