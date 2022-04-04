/*
 * Eric Yager
 */
package paint.drawtools;

import paintcanvas.Saveable;
import paintcanvas.DrawTool;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import paint.ImageTransformer;

/**
 * Tool to grab a color from the canvas.
 * 
 * @author ericyager
 */
public class GrabColorTool extends DrawTool {

    Saveable<Color> setColor;

    public GrabColorTool(Saveable<Color> setColor) {
        super();
        this.setColor = setColor;
    }

    public GrabColorTool(Canvas canvas, Saveable<Color> setColor) {
        super(canvas);
        this.setColor = setColor;
    }
    
    @Override
    public void onMousePressed(MouseEvent e) {
        //DO NOTHING
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
        setColor.save(ImageTransformer.canvasToImage(super.getCanvas()).getPixelReader().getColor((int) e.getX(), (int) e.getY()));
    }
    
    /**
     * Do nothing, needed for some draw tools but not all.
     */
    @Override
    public void cancelAction() {
        //DO NOTHING
    }
    
    
    
}
