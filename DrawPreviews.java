/*
 * Eric Yager
 */
package paint;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class contains all methods related to showing previews of drawings.
 * 
 * @author ericyager
 */
public class DrawPreviews {

    //Group to put previews in
    private final Group root;
    //Style saving fields
    private GraphicsContext gc;
    private int numberOfVertices, arcWidth;
    //Original user click point when drawing
    private double originalX, originalY;
    //Previews
    private final Line DRAG_LINE = new Line();
    private final Rectangle DRAG_RECTANGLE = new Rectangle();
    private final Ellipse DRAG_ELLIPSE = new Ellipse();
    private final Polygon DRAG_POLYGON = new Polygon();
    private final ArrayList<Ellipse> POINTS = new ArrayList<>(); //points for irregular polygon preview
    private final Text TEXT = new Text();
    private ImageView dragImage = null;

    /*----------------CONSTRUCTORS------------------*/
    /**
     * Constructor.
     *
     * @param root Group to put previews in
     * @param gc Graphics context to get style from
     */
    public DrawPreviews(Group root, GraphicsContext gc) {
        this.root = root;
        this.gc = gc;
    }

    /**
     * Constructor.
     *
     * @param root Group to put previews in
     * @param gc Graphics context to get style from
     * @param numberOfVertices number of vertices to draw
     * @param arcWidth width of rectangle rounding
     * @param text size of text
     */
    public DrawPreviews(Group root, GraphicsContext gc, int numberOfVertices, int arcWidth, String text) {
        this.root = root;
        this.gc = gc;
        this.numberOfVertices = numberOfVertices;
        this.arcWidth = arcWidth;
        TEXT.setText(text);
    }

    /*---------------MOUSE EVENT HANDLERS-----------------*/
    /**
     * Event handler for when the user presses the mouse.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param tool tool the user is using
     */
    public void mousePressed(double x, double y, String tool) {

        switch (tool) {
            case "Line":
                //Match preview with selections
                DRAG_LINE.setStroke(gc.getStroke());
                DRAG_LINE.setStrokeWidth(gc.getLineWidth());
                DRAG_LINE.setStrokeLineCap(gc.getLineCap());
                //Begin drawing preview
                DRAG_LINE.setStartX(x);
                DRAG_LINE.setStartY(y);
                DRAG_LINE.setEndX(x);
                DRAG_LINE.setEndY(y);
                root.getChildren().add(DRAG_LINE);
                break;
            case "Rectangle":
            case "Square":
                //Save original point
                originalX = x;
                originalY = y;
                //Match preview with selections
                DRAG_RECTANGLE.setFill(gc.getFill());
                DRAG_RECTANGLE.setStrokeWidth(gc.getLineWidth());
                DRAG_RECTANGLE.setStroke(gc.getStroke());
                if (gc.getLineCap().equals(StrokeLineCap.ROUND)) {
                    DRAG_RECTANGLE.setArcWidth(arcWidth * 5);
                    DRAG_RECTANGLE.setArcHeight(arcWidth * 5);
                } else {
                    DRAG_RECTANGLE.setArcWidth(0);
                    DRAG_RECTANGLE.setArcHeight(0);
                }
                //Begin drawing preview
                DRAG_RECTANGLE.setX(x);
                DRAG_RECTANGLE.setY(y);
                DRAG_RECTANGLE.setWidth(0);
                DRAG_RECTANGLE.setHeight(0);
                root.getChildren().add(DRAG_RECTANGLE);
                break;
            case "Ellipse":
            case "Circle":
                //Save original point
                originalX = x;
                originalY = y;
                //Match preview with selections
                DRAG_ELLIPSE.setFill(gc.getFill());
                DRAG_ELLIPSE.setStrokeWidth(gc.getLineWidth());
                DRAG_ELLIPSE.setStroke(gc.getStroke());
                //Begin drawing preview
                DRAG_ELLIPSE.setCenterX(x);
                DRAG_ELLIPSE.setCenterY(y);
                DRAG_ELLIPSE.setRadiusX(0);
                DRAG_ELLIPSE.setRadiusY(0);
                root.getChildren().add(DRAG_ELLIPSE);
                break;
            case "Regular Polygon":
                //Save original point
                originalX = x;
                originalY = y;
                //Match preview with selections
                DRAG_POLYGON.setFill(gc.getFill());
                DRAG_POLYGON.setStrokeWidth(gc.getLineWidth());
                DRAG_POLYGON.setStroke(gc.getStroke());
                //Begin drawing preview
                root.getChildren().add(DRAG_POLYGON);
                break;
            case "Star":
                //Save original point
                originalX = x;
                originalY = y;
                //Match preview with selections
                DRAG_POLYGON.setFill(gc.getFill());
                DRAG_POLYGON.setStrokeWidth(gc.getLineWidth());
                DRAG_POLYGON.setStroke(gc.getStroke());
                //Begin drawing preview
                root.getChildren().add(DRAG_POLYGON);
                break;
            case "Text":
                //Match preview with selections
                TEXT.setStroke(gc.getStroke());
                TEXT.setFill(gc.getFill());
                TEXT.setX(x);
                TEXT.setY(y);
                TEXT.fontProperty().setValue(new Font(gc.getLineWidth()));
                //Begin drawing preview
                root.getChildren().add(TEXT);
                break;
            case "Select & Move":
                if (dragImage == null) {
                    //Save original point
                    originalX = x;
                    originalY = y;
                    //Set outline rectangle
                    DRAG_RECTANGLE.setStrokeWidth(1);
                    DRAG_RECTANGLE.setStroke(new Color(.2, .2, .2, 1.0));
                    DRAG_RECTANGLE.getStrokeDashArray().addAll(3.0, 7.0, 3.0, 7.0);
                    DRAG_RECTANGLE.setFill(new Color(0, 0, 0, 0));
                    DRAG_RECTANGLE.setX(x);
                    DRAG_RECTANGLE.setY(y);
                    DRAG_RECTANGLE.setWidth(0);
                    DRAG_RECTANGLE.setHeight(0);
                    root.getChildren().add(DRAG_RECTANGLE);
                }
                break;
        }

    }

    /**
     * Event handler for when the user drags the mouse.
     *
     * @param x horizontal location of click
     * @param y vertical location of click
     * @param tool tool the user is using
     */
    public void mouseDragged(double x, double y, String tool) {

        switch (tool) {
            case "Line":
//                if (x > gc.getCanvas().getWidth() + gc.getLineWidth()
//                        && x - (gc.getCanvas().getWidth() + gc.getLineWidth()) 
//                        > y - (gc.getCanvas().getHeight() + gc.getLineWidth())) {
//                    y = ((y-originalY)/(x-originalX))*((gc.getCanvas().getWidth() + gc.getLineWidth()) - x) + y;
//                    x = gc.getCanvas().getWidth() + gc.getLineWidth();
//                } else if (y > gc.getCanvas().getHeight()+gc.getLineWidth()) {
//                    
//                }
//                
//                //Update end of preview line when dragged
//                if (x > gc.getCanvas().getWidth()+gc.getLineWidth()) {
//                    DRAG_LINE.setEndX(gc.getCanvas().getWidth()+gc.getLineWidth());
//                } else if(x < 0-gc.getLineWidth()) {
//                    DRAG_LINE.setEndX(0-gc.getLineWidth());
//                } else {
//                    DRAG_LINE.setEndX(x);
//                }
//                if (y > gc.getCanvas().getHeight()+gc.getLineWidth()) {
//                    DRAG_LINE.setEndY(gc.getCanvas().getHeight());
//                } else if (y < 0-gc.getLineWidth()) {
//                    DRAG_LINE.setEndY(0-gc.getLineWidth());
//                } else {
//                    DRAG_LINE.setEndY(y);
//                }
                DRAG_LINE.setEndX(x);
                DRAG_LINE.setEndY(y);
                break;
            case "Select & Move":
            case "Rectangle":
                //Update upper left corner
                if (x < originalX) { //if the user dragged left
                    DRAG_RECTANGLE.setX(x);
                } else {
                    DRAG_RECTANGLE.setX(originalX);
                }
                if (y < originalY) { //if the user dragged right
                    DRAG_RECTANGLE.setY(y);
                } else {
                    DRAG_RECTANGLE.setY(originalY);
                }
                //Update rectangle width and height
                DRAG_RECTANGLE.setWidth(Math.abs(x - originalX));
                DRAG_RECTANGLE.setHeight(Math.abs(y - originalY));
                break;
            case "Square":
                //update upper left corner
                if (x < originalX) { //If the user dragged left
                    if (Math.abs(originalX - x) > Math.abs(originalY - y)) { //If the user dragged more horizontally than vertically
                        DRAG_RECTANGLE.setX(x);
                    } else { //if the user dragged more vertically than horizontally
                        DRAG_RECTANGLE.setX(originalX - Math.abs(originalY - y));
                    }
                } else {
                    DRAG_RECTANGLE.setX(originalX);
                }
                if (y < originalY) { //if the user dragged up
                    if (Math.abs(originalY - y) > Math.abs(originalX - x)) { //if the user dragged more vertically than horizontally
                        DRAG_RECTANGLE.setY(y);
                    } else { //If the user dragged more horizontally than vertically
                        DRAG_RECTANGLE.setY(originalY - Math.abs(originalX - x));
                    }
                } else {
                    DRAG_RECTANGLE.setY(originalY);
                }
                //update square dimensions
                if (Math.abs(originalX - x) > Math.abs(originalY - y)) { //If the user dragged more horizontally than vertically
                    DRAG_RECTANGLE.setWidth(Math.abs(x - originalX));
                    DRAG_RECTANGLE.setHeight(Math.abs(x - originalX));
                } else { //if the user dragged more vertically than horizontally
                    DRAG_RECTANGLE.setWidth(Math.abs(y - originalY));
                    DRAG_RECTANGLE.setHeight(Math.abs(y - originalY));
                }
                break;
            case "Ellipse":
                //update ellipse radii
                DRAG_ELLIPSE.setRadiusX(Math.abs(DRAG_ELLIPSE.getCenterX() - x));
                DRAG_ELLIPSE.setRadiusY(Math.abs(DRAG_ELLIPSE.getCenterY() - y));
                break;
            case "Circle":
                //update circle radius
                if (Math.abs(originalX - x) > Math.abs(originalY - y)) { //If the user dragged more horizontally than vertically
                    DRAG_ELLIPSE.setRadiusX(Math.abs(DRAG_ELLIPSE.getCenterX() - x));
                    DRAG_ELLIPSE.setRadiusY(Math.abs(DRAG_ELLIPSE.getCenterX() - x));
                } else { //if the user dragged more vertically than horizontally
                    DRAG_ELLIPSE.setRadiusX(Math.abs(DRAG_ELLIPSE.getCenterY() - y));
                    DRAG_ELLIPSE.setRadiusY(Math.abs(DRAG_ELLIPSE.getCenterY() - y));
                }
                break;
            case "Regular Polygon":
                //reset points
                DRAG_POLYGON.getPoints().clear();
                DRAG_POLYGON.getPoints().addAll(ShapeMath.getPolygonPoints(x, y, originalX, originalY, numberOfVertices));
                break;
            case "Star":
                //reset points
                DRAG_POLYGON.getPoints().clear();
                DRAG_POLYGON.getPoints().addAll(ShapeMath.getStarPoints(x, y, originalX, originalY, numberOfVertices));
                break;
            case "Text":
                //move text
                TEXT.setX(x);
                TEXT.setY(y);
                break;
        }

    }

    /**
     * Event handler for when the user releases the mouse.
     *
     * @param x x coordinate
     * @param y x coordinate
     * @param tool tool the user is using
     */
    public void mouseReleased(double x, double y, String tool) {

        switch (tool) {
            case "Line":
            case "Rectangle":
            case "Square":
            case "Ellipse":
            case "Circle":
            case "Regular Polygon":
            case "Star":
            case "Text":
                //remove previews
                this.removePreviews();
                break;
            case "Select & Move":
                DRAG_RECTANGLE.getStrokeDashArray().clear();
                if (dragImage == null) {
                    //remove outline
                    root.getChildren().remove(DRAG_RECTANGLE);
                    //take snapshot
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
                    WritableImage newImage = new WritableImage(ImageTransformer.canvasToImage(gc.getCanvas()).getPixelReader(),
                            (int) originalX, (int) originalY, (int) Math.abs(originalX - x), (int) Math.abs(originalY - y));
                    dragImage = new ImageView(newImage);
                    //show preview
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(5.0);
                    dropShadow.setOffsetX(3.0);
                    dropShadow.setOffsetY(3.0);
                    dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
                    dragImage.setEffect(dropShadow);
                    dragImage.setX(originalX - 5);
                    dragImage.setY(originalY - 5);
                    dragImage.setOnMousePressed(e -> {
                        originalX = e.getX() - dragImage.getX();
                        originalY = e.getY() - dragImage.getY();
                    });
                    dragImage.setOnMouseDragged(e -> {
                        dragImage.setX(e.getX() - originalX);
                        dragImage.setY(e.getY() - originalY);
                        e.consume();
                    });
                    root.getChildren().add(dragImage);
                }
        }

    }

    /**
     * Handles all onMouseClicked events from canvas.
     *
     * @param x horizontal location of click
     * @param y vertical location of click
     * @param tool tool the user is using
     */
    public void mouseClicked(double x, double y, String tool) {

        if (tool.equals("Irregular Polygon")) {
            Ellipse point = new Ellipse(x, y, gc.getLineWidth(), gc.getLineWidth());
            POINTS.add(point);
            root.getChildren().add(point);
            DRAG_POLYGON.getPoints().add(x);
            DRAG_POLYGON.getPoints().add(y);
            if (DRAG_POLYGON.getPoints().size() == numberOfVertices * 2) {
                //remove preview
                for (int i = 0; i < numberOfVertices; i++) {
                    root.getChildren().remove(POINTS.get(i));
                }
                POINTS.clear();
                DRAG_POLYGON.getPoints().clear();
            }
        }

    }
    
    /*---------------UPDATE GROUP----------------*/
    /**
     * Remove all shape previews from the Group.
     */
    public void removePreviews() {
        root.getChildren().removeAll(DRAG_LINE, DRAG_RECTANGLE, DRAG_ELLIPSE,
                dragImage, TEXT, DRAG_POLYGON);
        try {
            for (int i = 0; i < numberOfVertices; i++) {
                root.getChildren().remove(POINTS.get(i));
            }
        } catch (IndexOutOfBoundsException e) {}
        POINTS.clear();
        DRAG_POLYGON.getPoints().clear();
        dragImage = null;
    }

    /*----------------SETTERS-----------------*/
    /**
     * Change the text to write.
     *
     * @param text String to display
     */
    public void setText(String text) {
        TEXT.setText(text);
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
        TEXT.setText(text);
        numberOfVertices = vertices;
        this.arcWidth = arcWidth;

    }

    /*--------------GETTERS---------------*/
    /**
     * Get the ImageView that contains the part of the canvas that the user
     * selected.
     *
     * @return ImageView with the image
     */
    public ImageView getDragImage() {
        return dragImage;
    }

}
