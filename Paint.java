/*
 * Eric Yager
 */
package paint;

//import java.util.concurrent.ArrayBlockingQueue;
//import java.io.File;
import javafx.scene.image.Image;

/**
 * Main class to launch application.
 *
 * @author ericyager
 */
public class Paint {

    private static final GUI GUI = new GUI();
    private static final FileManager FILE_MANAGER = new FileManager();
    private static final UndoRedo<Image> UNDO_REDO = new UndoRedo<>();
    private static final DrawPreviews PREVIEWS = new DrawPreviews(GUI.getRoot(), GUI.getCanvas().getGraphicsContext2D());
    private static final Draw DRAW = new Draw();
    private static final TimeAccumulator<String> logger = new TimeAccumulator(GUI.getMouseTools()[0],GUI.getMouseTools(),new Paint().getClass().getClassLoader().getResource("log.txt").getFile());
    //private static final ArrayBlockingQueue<Image> queue = new ArrayBlockingQueue<>(50);
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        //BobRossServer bob = new BobRossServer(5000, queue);
        //new Thread(bob).start();
        //BobRossClient view = new BobRossClient("localhost", 5000);
        //new Thread(view).start();
        GUI.launchGUI();

    }

    /**
     * 
     * @return the undo redo functionality
     */
    public static UndoRedo<Image> getUndoRedo() {
        return UNDO_REDO;
    }

    /**
     * 
     * @return 
     */
    public static FileManager getFileManager() {
        return FILE_MANAGER;
    }

    /**
     * 
     * @return 
     */
    public static DrawPreviews getPreviews() {
        return PREVIEWS;
    }

    /**
     * 
     * @return 
     */
    public static GUI getGUI() {
        return GUI;
    }

    /**
     * 
     * @return 
     */
    public static Draw getDraw() {
        return DRAW;
    }

    public static TimeAccumulator<String> getLogger() {
        return logger;
    }

    /**
     * Call before making changes to canvas. Every time canvas is changed!
     */
    public static void onChangeMade() {

        //queue.add(ImageTransformer.canvasToImage(GUI.getCanvas()));
        FILE_MANAGER.setChangesMade(); //record that change was made
        UNDO_REDO.pushUndo(ImageTransformer.canvasToImage(GUI.getCanvas())); //save canvas to undo
        if (UNDO_REDO.undoEmpty()) { //update undo and redo button abilities
            GUI.setViewUndo(false);
        } else {
            GUI.setViewUndo(true);
        }
        if (UNDO_REDO.redoEmpty()) {
            GUI.setViewRedo(false);
        } else {
            GUI.setViewRedo(true);
        }
        
        
    }
    
    

}
