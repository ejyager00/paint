/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.DrawTool;
import java.util.concurrent.Callable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import paint.ImageTransformer;

/**
 * Tool to write text.
 * 
 * @author ericyager
 */
public class TextTool extends DrawTool {
    
    private Image initialCanvas;
    private Callable<String> textGetter;
    double tempLineWidth;

    public TextTool(Callable<String> textGetter) {
        super();
        this.textGetter = textGetter;
    }
    
    public TextTool(Canvas canvas, Callable<String> textGetter) {
        super(canvas);
        this.textGetter = textGetter;
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        initialCanvas = ImageTransformer.canvasToImage(super.getCanvas());
        super.getCanvas().getGraphicsContext2D().setFont(new Font(super.getCanvas().getGraphicsContext2D().getLineWidth()));
        tempLineWidth = super.getCanvas().getGraphicsContext2D().getLineWidth();
        super.getCanvas().getGraphicsContext2D().setLineWidth(1);
        try {
            super.getCanvas().getGraphicsContext2D().fillText(textGetter.call(), e.getX(), e.getY());
            super.getCanvas().getGraphicsContext2D().strokeText(textGetter.call(), e.getX(), e.getY());
        } catch (Exception ex) {}
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        super.getCanvas().getGraphicsContext2D().drawImage(initialCanvas, 0, 0);
        try {
            super.getCanvas().getGraphicsContext2D().fillText(textGetter.call(), e.getX(), e.getY());
            super.getCanvas().getGraphicsContext2D().strokeText(textGetter.call(), e.getX(), e.getY());
        } catch (Exception ex) {}
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        super.getCanvas().getGraphicsContext2D().setLineWidth(tempLineWidth);
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
