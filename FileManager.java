/*
 * Eric Yager
 */
package paint;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * This class contains all methods related to file and undo/redo functionality.
 *
 * @author ericyager
 */
public class FileManager {

    private final String[] SUPPORTED_IMG_FORMATS = {"jpg", "png", "gif"};
    private final String TEMP_FILE = "tempfile.png";
    private String loadedFile = null; //file path of file if one has been loaded
    private boolean changesMade = false;
    //Transparency
    private static boolean[][] transparentPixels = null; //width in first [], height in second
    private static boolean hasTransparency = false;

    /**
     * Gets the most recently saved or loaded file.
     *
     * @return String containing file path
     */
    public String getLoadedFile() {
        return loadedFile;
    }

    /**
     * Set saved file path to null. Use when creating a new canvas.
     */
    public void nullifyLoadedFile() {
        loadedFile = null;
    }

    /**
     * Test whether changes have been made to the canvas.
     *
     * @return boolean true if the canvas has been changed
     */
    public boolean changesWereMade() {
        return changesMade;
    }

    /**
     * Update when changes have been made to the file.
     */
    public void setChangesMade() {
        changesMade = true;
    }

    public boolean hasTransparency() {
        return hasTransparency;
    }

    /**
     * Opens file selection dialogue and returns an Image object created from
     * file the user selects.
     *
     * @param primaryStage main stage of application
     * @return Image object made from the selected file
     */
    public Image loadFileDriver(Stage primaryStage) {

        //Show file chooser dialog
        File file = DialogueManager.selectLoadFile(primaryStage, SUPPORTED_IMG_FORMATS);

        if (file != null) {
            try {
                return loadFile(file);
            } catch (FileNotFoundException ex) {
                DialogueManager.showLoadError();
            }
        }
        return null;
    }

    /**
     * Load the most recently auto-saved image onto the canvas.
     *
     * @return autosave image
     */
    public Image loadLastAutoSaveDriver() {

        File file = new File(new FileManager().getClass().getClassLoader().getResource(TEMP_FILE).getFile());

        try {
            return loadFile(file);
        } catch (FileNotFoundException ex) {
            DialogueManager.showLoadError();
        }
        return null;

    }

    /**
     * Allow user to select a new file location, and save an image there.
     *
     * @param primaryStage stage to show dialogue
     * @param image image to save
     */
    public void saveAsDriver(Stage primaryStage, WritableImage image) {

        File file = DialogueManager.selectSaveFile(primaryStage, SUPPORTED_IMG_FORMATS);
        if (file == null) {
            DialogueManager.showSaveError();
            return;
        }
        if (hasTransparency) {
            image = ImageTransformer.convertToTransparent(image, transparentPixels);
            if (!file.getName().substring(file.getName().lastIndexOf('.') + 1).equals("png")) {
                if (!DialogueManager.dataLossWarning()) {
                    return;
                }
            }
        }
        saveImage(image, file);
        loadedFile = file.getPath();
        changesMade = false;

    }

    /**
     * Save to the most recently saved or loaded file.
     *
     * @param image image to save
     */
    public void saveDriver(WritableImage image) {

        File file = new File(loadedFile);
        if (hasTransparency) {
            image = ImageTransformer.convertToTransparent(image, transparentPixels);
            if (!file.getName().substring(file.getName().lastIndexOf('.') + 1).equals("png")) {
                if (!DialogueManager.dataLossWarning()) {
                    return;
                }
            }
        }
        saveImage(image, file);
        changesMade = false;

    }

    /**
     * Save to the temporary auto-save file.
     *
     * @param image image to save
     */
    public void autoSaveDriver(WritableImage image) {

        File file = new File(new FileManager().getClass().getClassLoader().getResource(TEMP_FILE).getFile());
        if (hasTransparency) {
            image = ImageTransformer.convertToTransparent(image, transparentPixels);
        }
        saveImage(image, file);

    }

    /**
     * Save an image to the given file.
     *
     * @param image image to save
     * @param file file location to save to
     */
    private void saveImage(Image image, File file) {

        try {
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException ex) {
            DialogueManager.showSaveError();
        }

    }

    /**
     * Load an image from the given file.
     *
     * @param file file to load
     * @return image from file
     * @throws FileNotFoundException
     */
    private Image loadFile(File file) throws FileNotFoundException {
        Image picture = new Image(new FileInputStream(file));
        loadedFile = file.getPath();
        transparentPixels = ImageTransformer.getTransparentPixels(picture);
        hasTransparency = !(transparentPixels == null);
        changesMade = false;
        return picture;
    }

}
