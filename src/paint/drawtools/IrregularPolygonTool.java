/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import paint.ImageTransformer;

/**
 * Tool to draw an irregular polygon.
 * 
 * @author ericyager
 */
public class IrregularPolygonTool extends DrawTool {

    private Image initialCanvas;
    private Callable<Integer> vertexNumberGetter;
    private boolean polygonStarted = false;
    private ArrayList<Double> points = new ArrayList<>();
    private Paint tempFillColor;

    public IrregularPolygonTool(Callable<Integer> vertexNumberGetter) {
        super();
        this.vertexNumberGetter = vertexNumberGetter;
    }

    public IrregularPolygonTool(Canvas canvas, Callable<Integer> vertexNumberGetter) {
        super(canvas);
        this.vertexNumberGetter = vertexNumberGetter;
    }
    
    @Override
    public void onMousePressed(MouseEvent e) {
        if (!polygonStarted) {
            initialCanvas = ImageTransformer.canvasToImage(super.getCanvas());
            tempFillColor = super.getCanvas().getGraphicsContext2D().getFill();
            super.getCanvas().getGraphicsContext2D().setFill(super.getCanvas().getGraphicsContext2D().getStroke());
            polygonStarted = true;
        }
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
        try {
            double size = super.getCanvas().getGraphicsContext2D().getLineWidth();
            super.getCanvas().getGraphicsContext2D().fillOval(e.getX()-size/2, e.getY()-size/2,size,size);
            points.add(e.getX());
            points.add(e.getY());
            if (points.size() == 2 * vertexNumberGetter.call()) {
                super.getCanvas().getGraphicsContext2D().setFill(tempFillColor);
                double[] xValues = new double[vertexNumberGetter.call()];
                for (int i = 0; i < vertexNumberGetter.call(); i++) {
                    xValues[i] = points.get(i * 2);
                }
                double[] yValues = new double[vertexNumberGetter.call()];
                for (int i = 0; i < vertexNumberGetter.call(); i++) {
                    yValues[i] = points.get(i * 2 + 1);
                }
                super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
                super.getCanvas().getGraphicsContext2D().fillPolygon(xValues, yValues, vertexNumberGetter.call());
                super.getCanvas().getGraphicsContext2D().strokePolygon(xValues, yValues, vertexNumberGetter.call());
                points.clear();
                polygonStarted = false;
            }
        } catch (Exception ex) {}
    }
    
    /**
     * Restores original canvas if needed.
     */
    @Override
    public void cancelAction() {
        super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
    }
    
    
    
}
