/*
 * Eric Yager
 */
package savetimer;

import javafx.scene.control.Label;

/**
 * JavaFX scene object for running and displaying an auto-save timer.
 *
 * @author ericyager
 */
public class SaveTimerDisplay extends Label {

    private final SaveTimer timer;

    /**
     * Constructor.
     *
     * @param saveFrequency interval between saves, in seconds
     * @param saveAction action to run when attempting to save
     */
    public SaveTimerDisplay(long saveFrequency, Runnable saveAction) {

        super("Time until auto-save:\nXX:XX"); //Create label
        this.timer = new SaveTimer(saveFrequency, saveAction, new Runnable() {
            //Anonymous inner class required instead of lambda because of instantiation rules
            @Override
            public void run() {
                updateTimerDisplay(timer.getTimeLeft()); //run this action to update display
            }
        });

    }

    /**
     * Start running the timer.
     */
    public void start() {

        Thread thread = new Thread(timer); //create new thread with a timer
        thread.setDaemon(true);
        thread.start();
        //new Thread(timer).start(); 

    }

    /**
     * Update time remaining display.
     *
     * @param seconds time remaining in seconds
     */
    private void updateTimerDisplay(int seconds) {

        String min = Integer.toString(seconds / 60);
        String sec;
        if (seconds % 60 < 10) {
            sec = "0" + seconds % 60; //if single digit seconds, add leading zero
        } else {
            sec = Integer.toString(seconds % 60);
        }
        setText("Time until auto-save:\n" + min + ":" + sec);

    }

    /**
     * Reset the timer to the beginning.
     */
    public void resetTimer() {
        timer.resetTimer();
    }

}
