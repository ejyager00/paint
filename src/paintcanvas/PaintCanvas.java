/*
 * Eric Yager
 */
package paintcanvas;

import java.util.HashMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import undoredo.UndoRedo;

/**
 * Canvas that can be programmed to do particular actions with DrawTools.
 * 
 * @author ericyager
 */
public class PaintCanvas extends Canvas {

    //private Draw draw;
    private UndoRedo<Image> undo;
    private String currentTool;
    private HashMap<String, DrawTool> tools;
    private Runnable changeAction;

    /**
     * Default constructor.
     */
    public PaintCanvas() {
        super();
        //draw = new Draw(this);
        undo = new UndoRedo<>();
        currentTool = null;
        tools = new HashMap<>();
        try {
            this.setOnMousePressed(e -> {
                if (changeAction != null) {
                    changeAction.run();
                }
                tools.get(currentTool).onMousePressed(e);
            });
            this.setOnMouseDragged(e -> {
                tools.get(currentTool).onMouseDragged(e);
            });
            this.setOnMouseReleased(e -> {
                tools.get(currentTool).onMouseReleased(e);
            });
            this.setOnMouseClicked(e -> {
                tools.get(currentTool).onMouseClicked(e);
            });
        } catch (NullPointerException e) {}
    }

    /**
     * Constructor with dimensions.
     * 
     * @param width canvas width
     * @param height canvas height
     */
    public PaintCanvas(double width, double height) {
        this();
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * Constructor with dimensions and adding tools.
     * 
     * @param width canvas width
     * @param height canvas height
     * @param firstTool string name of first tool to operate with
     * @param tools hashmap of tool names and tools
     */
    public PaintCanvas(double width, double height, String firstTool, HashMap<String, DrawTool> tools) {
        this(width, height);
        this.tools = tools;
        if (tools.containsKey(firstTool)) {
            currentTool = firstTool;
        }
    }

    /**
     * Update the current tool to use.
     * 
     * @param currentTool String tool name
     */
    public void setCurrentTool(String currentTool) {
        if (this.currentTool != null && tools.get(this.currentTool) != null) {
            tools.get(this.currentTool).cancelAction();
        }
        this.currentTool = currentTool;
    }

    /**
     * Add a tool for the canvas to use.
     * 
     * @param name String name of tool
     * @param tool DrawTool to add
     */
    public void addTool(String name, DrawTool tool) {
        tools.put(name, tool);
    }

    /**
     * Add a whole hashmap of tools.
     * 
     * @param tools hashmap of names and tools
     */
    public void addAll(HashMap<String, DrawTool> tools) {
        tools.forEach((name, tool) -> {
            this.addTool(name, tool);
        });
    }

    /**
     * Get the name of the current tool being used.
     * 
     * @return String name of tool
     */
    public String getCurrentTool() {
        return currentTool;
    }
    
    /**
     * Tell the canvas a function to run when it makes a change.
     * 
     * @param changeAction Runnable action
     */
    public void setChangeAction(Runnable changeAction) {
        this.changeAction = changeAction;
    }

}
