/*
 * Eric Yager
 */
package undoredo;

import java.util.Stack;

/**
 * Undo/Redo functionality object.
 *
 * @author ericyager
 * @param <T> Type to save the undo redo
 */
public class UndoRedo<T> {

    private final Stack<T> undoStack;
    private final Stack<T> redoStack;

    /**
     * Create a new undo redo.
     */
    public UndoRedo() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    /**
     * Pops the prior state off the undo stack.
     *
     * @param t Current state
     * @return Previous state
     */
    public T popUndo(T t) {

        redoStack.push(t); //push current state to redo stack
        return undoStack.pop(); //pop previous state from undo stack

    }

    /**
     * Pops the last undone state off the redo stack.
     *
     * @param t Current state
     * @return Last future state
     */
    public T popRedo(T t) {

        undoStack.push(t); //push current state to undo stack
        return redoStack.pop(); //pop current state from undo stack

    }

    /**
     * Pushes the current state onto the undo stack.
     *
     * @param t Current state
     */
    public void pushUndo(T t) {

        redoStack.clear(); //clear redo stack
        undoStack.push(t); //pusg current state to undo stack

    }

    /**
     * Clears the undo and redo stacks. Should be used when creating a new file
     * or loading a different file.
     */
    public void clearUndoRedo() {
        undoStack.clear(); //clear undo stack
        redoStack.clear(); //clear redo stack
    }

    /**
     * Checks if undo stack is empty.
     *
     * @return Boolean true if no actions to undo.
     */
    public boolean undoEmpty() {
        return undoStack.empty();
    }

    /**
     * Checks if redo stack is empty.
     *
     * @return Boolean true if no actions to redo.
     */
    public boolean redoEmpty() {
        return redoStack.empty();
    }

}
