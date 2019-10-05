/*
 * Eric Yager
 */
package paint;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;

/**
 * This class contains all methods related to drawing on the canvas.
 *
 * @author ericyager
 */
public class Draw {
    
    //store the points for irregular polygons
    private final ArrayList<Double> irregularPolygonPoints = new ArrayList<>();
    //Graphics context for style
    private GraphicsContext gc;
    //other important style data
    private int numberOfVertices, arcWidth;
    private String text;
    //Original user click point when drawing
    private double originalX, originalY;
    //Temporary adjustible point, used for upper left corner of rectangles
    private double tempX, tempY;
    //Image 
    private boolean imageSelected = false;

    /*---------MOUSE EVENT HANDLERS FOR DRAWING FUNCTIONS---------*/
    /**
     * Handles user mouse pressed events.
     *
     * @param canvas canvas for drawing
     * @param x horizontal location of click
     * @param y vertical location of click
     * @param tool tool the user is using
     */
    public void mousePressed(Canvas canvas, double x, double y, String tool) {
        
        gc = canvas.getGraphicsContext2D();
        switch (tool) {
            case "Erase":
                gc.setStroke(Color.WHITE);
            case "Free Draw":
                Paint.onChangeMade();
                //record starting point
                originalX = x;
                originalY = y;
                //fix line cap
                if (gc.getLineCap().equals(StrokeLineCap.BUTT)) {
                    gc.setLineCap(StrokeLineCap.SQUARE);
                }
                break;
            case "Rectangle":
            case "Square":
                //record temporary point
                tempX = x;
                tempY = y;
            case "Line":
            case "Ellipse":
            case "Circle":
            case "Regular Polygon":
            case "Star":
                //record starting point
                originalX = x;
                originalY = y;
                break;
            case "Select & Move":
                if (!imageSelected) {
                    //record starting point
                    originalX = x;
                    originalY = y;
                }
                break;
        }

    }

    /**
     * Handles user mouse dragged events.
     *
     * @param x horizontal location of click
     * @param y vertical location of click
     * @param canvas canvas for drawing
     * @param tool tool the user is using
     */
    public void mouseDragged(Canvas canvas, double x, double y, String tool) {

        switch (tool) {
            case "Erase":
            case "Free Draw":
                gc.strokeLine(originalX, originalY, x, y);
                originalX = x;
                originalY = y;
                break;
        }

    }

    /**
     * Handles user mouse release events.
     *
     * @param canvas canvas for drawing
     * @param x horizontal location of click
     * @param y vertical location of click
     * @param tool tool the user is using
     */
    public void mouseReleased(Canvas canvas, double x, double y, String tool) {

        switch (tool) {
            case "Line":
                Paint.onChangeMade();
                //Draw line
                gc.strokeLine(originalX, originalY, x, y);
                break;
            case "Rectangle":
                Paint.onChangeMade();
                //find upper left corner
                if (x < tempX) { //If the user dragged left after clicking
                    double temp = tempX; //Swap x and tempX
                    tempX = x;
                    x = temp;
                }
                if (y < tempY) { //If the user dragged up after clicking
                    double temp = tempY; //Swap y and tempY
                    tempY = y;
                    y = temp;
                }
                //Draw rectangle
                if (gc.getLineCap().equals(StrokeLineCap.ROUND)) { //if it's rounded
                    gc.fillRoundRect(tempX, tempY, Math.abs(x - tempX), Math.abs(y - tempY), arcWidth * 5, arcWidth * 5);
                    gc.strokeRoundRect(tempX, tempY, Math.abs(x - tempX), Math.abs(y - tempY), arcWidth * 5, arcWidth * 5);
                } else { //if it's not rounded
                    gc.fillRect(tempX, tempY, Math.abs(x - tempX), Math.abs(y - tempY)); //abs(n - tempN)  gives side length
                    gc.strokeRect(tempX, tempY, Math.abs(x - tempX), Math.abs(y - tempY));
                }
                break;
            case "Square":
                Paint.onChangeMade();
                //Locate upper left corner
                if (x < tempX) { //if the user dragged left after clicking
                    if (tempX - x > Math.abs(y - tempY)) { //if the user dragged more horizontally than vertically
                        double temp = tempX; //swap x and tempX
                        tempX = x;
                        x = temp;
                    } else { //if the user dragged more vertically than horizontally
                        tempX = tempX - Math.abs(y - tempY); //force tempX to be as equal distance from start as tempY
                    }
                }
                if (y < tempY) { //if the user dragged up after clicking
                    if (tempY - y > Math.abs(x - tempX)) { //if the user dragged more vertically than horizontally
                        double temp = tempY; //swap y and tempY
                        tempY = y;
                        y = temp;
                    } else { //if the user dragged more horizontally than vertically
                        tempY = tempY - Math.abs(x - tempX); //force tempY to be as equal distance from start as tempX
                    }
                }
                //Draw square
                if (gc.getLineCap().equals(StrokeLineCap.ROUND)) { //if it's rounded
                    if (Math.abs(tempX - x) > Math.abs(tempY - y)) { //if the user dragged more horizontally than vertically
                        gc.fillRoundRect(tempX, tempY, Math.abs(x - tempX), Math.abs(x - tempX), arcWidth * 5, arcWidth * 5); //Use width as side length
                        gc.strokeRoundRect(tempX, tempY, Math.abs(x - tempX), Math.abs(x - tempX), arcWidth * 5, arcWidth * 5);
                    } else { //if the user dragged more vertically than horizontally
                        gc.fillRoundRect(tempX, tempY, Math.abs(y - tempY), Math.abs(y - tempY), arcWidth * 5, arcWidth * 5); //Use height as side length
                        gc.strokeRoundRect(tempX, tempY, Math.abs(y - tempY), Math.abs(y - tempY), arcWidth * 5, arcWidth * 5);
                    }
                } else { //if it's not rounded
                    if (Math.abs(tempX - x) > Math.abs(tempY - y)) { //if the user dragged more horizontally than vertically
                        gc.fillRect(tempX, tempY, Math.abs(x - tempX), Math.abs(x - tempX)); //Use width as side length
                        gc.strokeRect(tempX, tempY, Math.abs(x - tempX), Math.abs(x - tempX));
                    } else { //if the user dragged more vertically than horizontally
                        gc.fillRect(tempX, tempY, Math.abs(y - tempY), Math.abs(y - tempY)); //Use height as side length
                        gc.strokeRect(tempX, tempY, Math.abs(y - tempY), Math.abs(y - tempY));
                    }
                }
                break;
            case "Ellipse":
                Paint.onChangeMade();
                //Draw ellipse
                gc.fillOval(originalX - Math.abs(originalX - x), originalY - Math.abs(originalY - y),
                        2 * Math.abs(originalX - x), 2 * Math.abs(originalY - y));
                gc.strokeOval(originalX - Math.abs(originalX - x), originalY - Math.abs(originalY - y),
                        2 * Math.abs(originalX - x), 2 * Math.abs(originalY - y));
                break;
            case "Circle":
                Paint.onChangeMade();
                //Draw circle
                if (Math.abs(originalX - x) > Math.abs(originalY - y)) { //the user dragged more horizontally than vertically
                    gc.fillOval(originalX - Math.abs(originalX - x), originalY - Math.abs(originalX - x),
                            2 * Math.abs(originalX - x), 2 * Math.abs(originalX - x));
                    gc.strokeOval(originalX - Math.abs(originalX - x), originalY - Math.abs(originalX - x),
                            2 * Math.abs(originalX - x), 2 * Math.abs(originalX - x));
                } else { //the user dragged more vertically than horizontally
                    gc.fillOval(originalX - Math.abs(originalY - y), originalY - Math.abs(originalY - y),
                            2 * Math.abs(originalY - y), 2 * Math.abs(originalY - y));
                    gc.strokeOval(originalX - Math.abs(originalY - y), originalY - Math.abs(originalY - y),
                            2 * Math.abs(originalY - y), 2 * Math.abs(originalY - y));
                }
                break;
            case "Regular Polygon":
                Paint.onChangeMade();
                //Get points
                ArrayList<Double> polygonPoints = ShapeMath.getPolygonPoints(x, y, originalX, originalY, numberOfVertices);
                //get x and y valuse of coordinates for polygon
                double[] xValues = new double[numberOfVertices];
                for (int i = 0; i < numberOfVertices; i++) {
                    xValues[i] = polygonPoints.get(i * 2);
                }
                double[] yValues = new double[numberOfVertices];
                for (int i = 0; i < numberOfVertices; i++) {
                    yValues[i] = polygonPoints.get(i * 2 + 1);
                }
                //draw polygon
                gc.fillPolygon(xValues, yValues, numberOfVertices);
                gc.strokePolygon(xValues, yValues, numberOfVertices);
                break;
            case "Star":
                Paint.onChangeMade();
                //Get points
                ArrayList<Double> starPoints = ShapeMath.getStarPoints(x, y, originalX, originalY, numberOfVertices);
                //get x and y valuse of coordinates for polygon
                double[] xValuesStar = new double[numberOfVertices * 2];
                for (int i = 0; i < numberOfVertices * 2; i++) {
                    xValuesStar[i] = starPoints.get(i * 2);
                }
                double[] yValuesStar = new double[numberOfVertices * 2];
                for (int i = 0; i < numberOfVertices * 2; i++) {
                    yValuesStar[i] = starPoints.get(i * 2 + 1);
                }
                //draw polygon
                gc.fillPolygon(xValuesStar, yValuesStar, numberOfVertices * 2);
                gc.strokePolygon(xValuesStar, yValuesStar, numberOfVertices * 2);
                break;
            case "Text":
                Paint.onChangeMade();
                //Draw text
                gc.setFont(new Font(gc.getLineWidth()));
                gc.setLineWidth(1);
                gc.fillText(text, x, y);
                gc.strokeText(text, x, y);
                break;
            case "Select & Move":
                if (!imageSelected) {
                    //ensure correct orientation of rectangle
                    if (originalX > x) {
                        double temp = x;
                        x = originalX;
                        originalX = temp;
                    }
                    if (originalY > y) {
                        double temp = y;
                        y = originalY;
                        originalY = temp;
                    }
                    //White out area where the snapshot was take from
                    gc.setFill(Color.WHITE);
                    gc.fillRect(originalX, originalY, Math.abs(x - originalX), Math.abs(y - originalY));
                    imageSelected = true;
                }
        }

    }

    /**
     * Handles user mouse click events.
     *
     * @param x horizontal location of click
     * @param y vertical location of click
     * @param canvas canvas for drawing
     * @param tool tool the user is using
     */
    public void mouseClicked(Canvas canvas, double x, double y, String tool) {

        switch (tool) {
            case "Color Grabber":
                Color newColor = ImageTransformer.canvasToImage(canvas).getPixelReader().getColor((int) x, (int) y);
                Paint.getGUI().newColorGrabbed(newColor);
                break;
            case "Irregular Polygon":
                irregularPolygonPoints.add(x);
                irregularPolygonPoints.add(y);
                if (irregularPolygonPoints.size() == numberOfVertices * 2) {
                    
                    //Get points
                    double[] xValues = new double[numberOfVertices];
                    for (int i = 0; i < numberOfVertices; i++) {
                        xValues[i] = irregularPolygonPoints.get(i * 2);
                    }
                    double[] yValues = new double[numberOfVertices];
                    for (int i = 0; i < numberOfVertices; i++) {
                        yValues[i] = irregularPolygonPoints.get(i * 2 + 1);
                    }
                    //draw polygon
                    gc.fillPolygon(xValues, yValues, numberOfVertices);
                    gc.strokePolygon(xValues, yValues, numberOfVertices);
                    
                    irregularPolygonPoints.clear();
                    
                }   
                break;
            case "Free Draw":
            case "Erase":
                //draw point
                originalX = x;
                originalY = y;
                gc.strokeLine(originalX, originalY, x, y);
                break;
            case "Fill":
                Paint.onChangeMade();
                int oldArgb = ImageTransformer.canvasToImage(canvas).getPixelReader().getArgb((int) x, (int) y);
                fill(ImageTransformer.canvasToImage(canvas), (int) x, (int) y, oldArgb, canvas);
                break;
        }

    }

    /*-------------ADDITIONAL METHODS CALLED FROM GUI-----------*/
    /**
     * Remove imageView upon changing the tool.
     *
     * @param root Group with preview
     * @param canvas Canvas for undo
     */
    public void undoSelect(Group root, Canvas canvas) {

        if (imageSelected) {
            Image picture = Paint.getUndoRedo().popUndo(ImageTransformer.canvasToImage(canvas)); //loads the picture
            canvas.widthProperty().setValue(picture.getWidth()); //sets the canvas dimensions to fit the picture
            canvas.heightProperty().setValue(picture.getHeight());
            canvas.getGraphicsContext2D().drawImage(picture, 0, 0);
        }

    }

    /**
     * Draw the current image from the imageview to the canvas.
     *
     * @param dragImage imageview that has been moved to a particular location
     */
    public void writeSelectedImage(ImageView dragImage) {
        gc.drawImage(dragImage.getImage(), dragImage.getX(), dragImage.getY());
        imageSelected = false;
    }
    
    /*----------------SETTERS-----------------*/
    /**
     * Change the text to write.
     *
     * @param text String to display
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Change the number of vertices to draw.
     *
     * @param vertices int number of vertices
     */
    public void setNumberOfVertices(int vertices) {
        numberOfVertices = vertices;
    }

    /**
     * Change the arc width of a round rectangle.
     *
     * @param arcWidth int arc width of rounding
     */
    public void setArcWidth(int arcWidth) {
        this.arcWidth = arcWidth;
    }
    
    /**
     * Update all style fields not contained in the graphics context.
     *
     * @param text String to display
     * @param vertices int number of vertices
     * @param arcWidth int arc width of rounding
     */
    public void updateAll(String text, int vertices, int arcWidth) {
        this.text = text;
        numberOfVertices = vertices;
        this.arcWidth = arcWidth;
    }

    /*------------PRIVATE METHODS FOR DRAWING FUNCTIONS----------*/
    /**
     * Fills all of an area that shares a color with a new color.
     *
     * @param image WritableImage containing snapshot of canvas
     * @param x int x coordinate of click point
     * @param y int y coordinate of click point
     * @param originalColor int argb color of original click point
     * @param canvas Canvas on which to operate fill.
     */
    private void fill(WritableImage image, int x, int y, int originalColor, Canvas canvas) {

        ArrayList<int[]> pixels = new ArrayList<>(); //keeps track of which pixels' neighbors must be checked
        pixels.add(new int[]{x, y}); //add initial point
        for (int i = 0; i < pixels.size(); i++) { //until we've exausted potential pixels
            x = pixels.get(i)[0]; //get current pixel
            y = pixels.get(i)[1];
            for (int j = 0; j < 9; j++) { //for all neighbors
                if (x - 1 + j % 3 >= 0 && x - 1 + j % 3 < canvas.getWidth() // within x bounds of canvas
                        && y - 1 + j / 3 >= 0 && y - 1 + j / 3 < canvas.getHeight() //with y bounds of canvas
                        && image.getPixelReader().getArgb(x - 1 + j % 3, y - 1 + j / 3) == originalColor) { //matches old color
                    canvas.getGraphicsContext2D().fillRect(x - 1 + j % 3, y - 1 + j / 3, 1, 1); //update pixel on canvas
                    image.getPixelWriter().setArgb(x - 1 + j % 3, y - 1 + j / 3, originalColor + 2); //update pixel on image
                    pixels.add(new int[]{x - 1 + j % 3, y - 1 + j / 3}); //add to pixels to check neighbors
                }
            }
        }
    }
    
}
