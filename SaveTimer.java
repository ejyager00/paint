/*
 * Eric Yager
 */
package paint;

import javafx.application.Platform;

/**
 * Timer to automatically save.
 *
 * @author ericyager
 */
public class SaveTimer implements Runnable {

    private long startTime; //time the last save occured
    private long elapsedTime; //time elapsed since last save
    private final long SAVE_FREQUENCY; //how often to save, in milliseconds
    private boolean resetTimer; //whether or not the timer should reset
    private final TimerAction saveAction; //action to run when ready to save
    private final TimerAction updateAction; //action to run when updating display

    /**
     * Constructor.
     *
     * @param saveFrequency how often to save, in seconds
     * @param saveAction save method
     * @param updateAction update display method
     */
    public SaveTimer(long saveFrequency, TimerAction saveAction, TimerAction updateAction) {

        this.saveAction = saveAction;
        this.SAVE_FREQUENCY = saveFrequency;
        this.updateAction = updateAction;

    }

    /**
     * Infinitely run countdowns. This code runs when the SaveTimer is started
     * in a new thread.
     */
    @Override
    public void run() {

        resetTimer = false;
        while (true) {
            try {
                countdown(); //start a new countdown
            } catch (InterruptedException ex) {}
        }

    }

    /**
     * Count down timer from the save frequency.
     *
     * @throws InterruptedException
     */
    private void countdown() throws InterruptedException {

        elapsedTime = 0;
        startTime = System.currentTimeMillis(); //initialize start time
        while (elapsedTime < SAVE_FREQUENCY) {
            
            Platform.runLater(() -> {
                updateAction.action(); //update display
            });
            Thread.sleep(250); //wait a quarter second
            elapsedTime = System.currentTimeMillis() - startTime; //calculate new elapsed time
            if (resetTimer) {
                resetTimer = false;
                return;
            }

        }
        Platform.runLater(() -> {
            saveAction.action();
        });

    }

    /**
     * Reset the countdown.
     */
    public void resetTimer() {
        resetTimer = true;
    }
    
    public int getTimeLeft() {
        return ((int) (SAVE_FREQUENCY - elapsedTime) / 1000);
    }

}
