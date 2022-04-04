/*
 * Eric Yager
 */
package paintcanvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

/**
 * Abstract class which is a model for the tools a PaintCanvas can use.
 * 
 * @author ericyager
 */
public abstract class DrawTool {
    
    //Canvas to operate on
    private Canvas canvas;
    
    /**
     * Default constructor.
     */
    public DrawTool() {
        this.canvas = null;
    }
    
    /**
     * Constructor to instantiate canvas.
     * 
     * @param canvas 
     */
    public DrawTool(Canvas canvas) {
        this.canvas = canvas;
    }
    
    /**
     * Returns the canvas this particular tool is operating on.
     * 
     * @return canvas this tool is operating on
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    /**
     * Switch the tool to operate on a new canvas.
     * 
     * @param canvas new canvas
     */
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
    
    /**
     * Action to do when the mouse is pressed on the canvas.
     * 
     * @param e mouse event on press
     */
    public abstract void onMousePressed(MouseEvent e);
    
    /**
     * Action to do when the mouse is dragged on the canvas.
     * 
     * @param e mouse event on drag
     */
    public abstract void onMouseDragged(MouseEvent e);
    
    /**
     * Action to do when the mouse is released off the canvas.
     * 
     * @param e mouse event on release
     */
    public abstract void onMouseReleased(MouseEvent e);
    
    /**
     * Action to do when the mouse is clicked on the canvas.
     * 
     * @param e mouse event on click
     */
    public abstract void onMouseClicked(MouseEvent e);
    
    /**
     * Abort changes in the event 
     */
    public abstract void cancelAction();
    
}
