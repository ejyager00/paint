/*
 * Eric Yager
 */
package paint;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Manages dialogue windows for paint.
 *
 * @author ericyager
 */
public class DialogueManager {

    /**
     * Show save file dialogue.
     * @param stage Stage on which to show dialogue
     * @param fileFormats legal file formats
     * @return user-selected file location
     */
    public static File selectSaveFile(Stage stage, String[] fileFormats) {

        FileChooser fileChooser = new FileChooser();

        //set extension filters based on supported image formats
        for (String ext : fileFormats) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(ext.toUpperCase() + " (*." + ext + ")", "*." + ext)
            );
        }

        File file = fileChooser.showSaveDialog(stage);
        return file;

    }

    /**
     * Show load file dialogue.
     * @param stage Stage on which to show dialogue
     * @param fileFormats legal file formats
     * @return user-selected file
     */
    public static File selectLoadFile(Stage stage, String[] fileFormats) {

        //Set up window to choose file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");

        //Generates the appropriate extension filter based on the current supported
        //image types
        String filterName = "Image (";
        for (String ext : fileFormats) {
            filterName += "*." + ext + ", ";
        }
        filterName = filterName.substring(0, filterName.length() - 2);
        filterName += ")";
        ArrayList<String> extensions = new ArrayList();
        for (String ext : fileFormats) {
            extensions.add("*." + ext);
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterName, extensions));

        //Show file chooser dialog
        File file = fileChooser.showOpenDialog(stage);
        return file;

    }

    /**
     * Show that an error has occurred while saving.
     */
    public static void showSaveError() {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("An error has occured.");
        alert.setContentText("Your file was unable to be saved.");
        alert.showAndWait();

    }

    /**
     * Show that an error has occurred while loading.
     */
    public static void showLoadError() {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("An error has occured.");
        alert.setContentText("Your file was unable to be loaded.");
        alert.showAndWait();

    }

    /**
     * Warns the user that a save may cause data loss. If the user wants to save
     * anyway, return true. Otherwise, return false.
     * @return user wants to proceed
     */
    public static boolean dataLossWarning() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //checks that the user wants to close
        alert.setTitle("DATA LOSS WARNING!!");
        alert.setHeaderText("This save could cause data to be lost.");
        alert.setContentText("You are attempting to save a transparent image to a file type that "
                + "does not support transparency. Hit okay to proceed, or hit cancel to cancel.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result == null) {return false;}
        return result.get().equals(ButtonType.OK);

    }

    /**
     * Show that the user incorrectly formatted image dimensions.
     */
    public static void dimensionFormatError() {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("That was not correct input.");
        alert.setContentText("Please input dimensions like: 400x400");
        alert.showAndWait();

    }

    /**
     * Show that the user entered negative dimensions.
     */
    public static void negativeDimensionError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("That was not correct input.");
        alert.setContentText("Please use positive values for the new image dimensions.");
        alert.showAndWait();
    }

    /**
     * Remind the user to save their work. Returns "OK" if the user wants to proceed
     * anyway, returns "Save" if the user wants to save, and returns "Cancel" if the
     * user wants to cancel the action.
     * @return user choice
     */
    public static String saveReminder() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //checks that the user wants to close
        alert.setTitle("DON'T FORGET TO SAVE");
        alert.setHeaderText("You are about to lose changes.");
        alert.setContentText("You have not saved your project since making edits! Press Save to save,"
                + " press Cancel to continue editing, or press OK to continue.");
        ButtonType saveBtn = new ButtonType("Save");
        alert.getButtonTypes().add(saveBtn);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return "OK";
        } else if (result.get() == saveBtn) {
            return "Save";
        } else {
            return "Cancel";
        }

    }
    
    /**
     * Opens a dialogue window to ask the user to input dimensions for a new
     * canvas. Returns {0, 0} if the dimensions are invalid.
     *
     * @return int[] with the dimensions for a new canvas
     */
    public static int[] getNewCanvasDimensions() {

        //Create new dialog asking about dimensions
        TextInputDialog dialog = new TextInputDialog("1250x750");
        dialog.setTitle("Set new file dimensions");
        dialog.setHeaderText("Please enter new dimensions for your canvas.\nExample: 400x400");
        dialog.setContentText("Dimensions:");

        //displays the dialogue
        Optional<String> result = dialog.showAndWait();
        int[] dimN = new int[]{0, 0};
        if (result.toString().equals("Optional.empty")) {
            return dimN;
        }
        result.ifPresent(name -> {
            try {
                String[] dimS = result.toString().substring(9).split("x"); //dimensions in string form
                dimN[0] = Integer.parseInt(dimS[0]);
                dimN[1] = Integer.parseInt(dimS[1].substring(0, dimS[1].length() - 1)); //dimensions in int form
            } catch (NumberFormatException e) { // if there is illegal input, this will catch it and display an error
                dimensionFormatError();
            }
        });
        if (dimN[0] < 1 || dimN[1] < 1) { //if the input is negative
            negativeDimensionError();
            return new int[]{0, 0};
        }
        return dimN;
    }

}
