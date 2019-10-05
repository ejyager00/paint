/*
 * Eric Yager
 */
package paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * This class loads text and image resources from the resources folder.
 *
 * @author ericyager
 */
public class ResourceLoader {

    private static final String RELEASE_NOTES = "releasenotes.txt";
    private static final String TOOL_GUIDE = "toolGuide.txt";
    private static final String TRANSPARENCY_GRID = "transparencyGrid.jpg";

    /**
     * Get current version of YagerPaint.
     *
     * @return String containing current version of
     */
    public static String getVersion() {

        try {
            //Open file
            ClassLoader classLoader = new ResourceLoader().getClass().getClassLoader();
            File file = new File(classLoader.getResource(RELEASE_NOTES).getFile());
            //Read File Content
            String content = new String(Files.readAllBytes(file.toPath()));
            content = content.split("VERSION ")[1]; //version number comes after VERSION
            content = content.split("\n")[0]; //and before the next new line
            return content;
        } catch (IOException ex) {
            Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * Get most recent release notes.
     *
     * @return String containing most recent release notes
     */
    public static String getReleaseNotes() {

        try {
            //Open file
            ClassLoader classLoader = new ResourceLoader().getClass().getClassLoader();
            File file = new File(classLoader.getResource(RELEASE_NOTES).getFile());
            //Read File Content
            String content = new String(Files.readAllBytes(file.toPath()));
            return content;
        } catch (IOException ex) {
            Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * Get tool guide.
     *
     * @return String[] containing tool headers and information
     */
    public static String[] getToolGuide() {
        try {
            ClassLoader classLoader = new ResourceLoader().getClass().getClassLoader();
            File file = new File(classLoader.getResource(TOOL_GUIDE).getFile());

            //Read File Content
            String content = new String(Files.readAllBytes(file.toPath()));
            return content.split("\n\n");
        } catch (IOException ex) {
            Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Get a transparency grid to put behind transparent image.
     *
     * @param width desired transparency grid width
     * @param height desired transparency grid height
     * @return the appropriately sized transparency grid
     */
    public static Image getTransparencyGrid(int width, int height) {
        try {
            //Open image file
            Image fullGrid = new Image(new FileInputStream(new File(new ResourceLoader().getClass().getClassLoader().getResource(TRANSPARENCY_GRID).getFile())));
            //Scale image 
            WritableImage partial = new WritableImage(fullGrid.getPixelReader(), width, height);
            return partial;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
