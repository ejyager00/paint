/*
 * Eric Yager
 */
package paint;

import paintcanvas.PaintCanvas;
import savetimer.SaveTimerDisplay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import logger.TimeAccumulator;
import paint.drawtools.*;
import paintcanvas.DrawTool;
import undoredo.UndoRedo;

/**
 * Main class that launches a paint application and maintains the functionality.
 * 
 * @author ericyager
 */
public class Paint extends Application {

    private UndoRedo<Image> undoRedo = new UndoRedo<>();
    private FileManager fileManager = new FileManager("tempfile.png", new String[]{"jpg", "png", "gif"});
    private TimeAccumulator<String> logger = new TimeAccumulator<>("Mouse", MOUSE_TOOLS, "log.txt");
    private int changeTracker = 0; //needed for certain cases of undo and redo

    /*------------------GUI FIELDS---------------------*/
    private final int DEFAULT_WIDTH = 1250; //HARD CODE DEFAULT CANVAS SIZE
    private final int DEFAULT_HEIGHT = 750;
    private final int MIN_STAGE_WIDTH1 = 600; //min height and width for primary stage
    private final int MIN_STAGE_HEIGHT1 = 450;
    private final int MIN_STAGE_WIDTH2 = 200; //min height and width for secondary stages
    private final int MIN_STAGE_HEIGHT2 = 200;
    private final int TEXT_MAX_WIDTH = 200; //size of text field for input
    private final int TEXT_MAX_HEIGHT = 50;
    private final int SAVE_FREQUENCY = 120000;
    private final String MENU_BAR_STYLE = "-fx-padding: 1 5 1 5;";//Default padding for menu stuff
    private static final String[] MOUSE_TOOLS
            = new String[]{"Mouse", "Line", "Free Draw", "Erase", "Square", "Rectangle", "Circle", "Ellipse",
                "Regular Polygon", "Irregular Polygon", "Star", "Text", "Grab Color", "Copy & Paste", "Cut & Paste",
                "Fill", "Image Fill", "Rainbow Fun Fill"};
    /*--------------Main stage nodes--------------*/
    private Scene scene; //main scene
    private PaintCanvas canvas = new PaintCanvas(DEFAULT_WIDTH, DEFAULT_HEIGHT); //Creates the starting canvas
    private ScrollPane canvasPane;
    private Group root = new Group(); //This group contains the canvas, and allows for the line preview
    private Pane staticCanvasPane; //This pane holds the Group and is needed so that the preview nodes scale like the canvas when zoomed
    private MenuBar menuBar; //menubar for the application menu
    private GridPane mainPane; //This pane contains the menubar, the canvas scrollpane, and sometimes the toolbar
    private ImageView transparencyGrid;
    /*--------------secondary stages, tool bar, & timer--------------*/
    private Stage toolsWindow, aboutStage, toolGuide, timerDisplay; //these are the secondary stages for other needed windows
    private GridPane toolsBar; //this holds the draw tools when they are in the primary stage
    /*--------------Current setting for tools location--------------*/
    private boolean toolsSeparate = true; //keeps track of whether or not the tools are in a separate window
    /*--------------Draw tools--------------*/
    private HashMap<String, DrawTool> mouseToolHash = new HashMap<>();
    private ColorPicker linePicker, fillPicker; //colorpickers
    private ComboBox mouseTools, edgeRounding; //combocboxes for tools and input
    private Slider lineWidthSlider, polygonPointsSlider; //sliders for number options
    private Button zoomIn, zoomOut; //zoom in and out buttons
    private TextArea text; //field to allow input for text function
    /*-----------Zoom instance variables------------*/
    private Label zoom; //label to show user current zoom
    private double currentZoom = 1; //current zoom factor
    private Scale zoomScale; //scale for the staticCanvasPane
    /*--------------Store reference to menus and menu items--------------*/
    private HashMap<String, MenuItem> menuItems = new HashMap<>(25); //hashmap to store menu items
    private HashMap<String, Menu> menus = new HashMap<>(12); //hashmap to store menus

    /**
     * Creates the GUI and loads the window upon launch command.
     *
     * @param primaryStage the main stage of the application
     */
    @Override
    public void start(Stage primaryStage) {

        /*--------------- Initialize fields -------------*/
        //make blank white canvas
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        addToolsToDict(primaryStage); //add the tools to the canvas
        canvas.setCurrentTool("Mouse");
        canvas.setChangeAction(() -> {
            onChangeMade();
        });

        //Tool selector comboBox
        mouseTools = new ComboBox(FXCollections.observableArrayList(MOUSE_TOOLS));
        mouseTools.getSelectionModel().select(0);
        mouseTools.setTooltip(new Tooltip("Shape to draw"));
        mouseTools.valueProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setCurrentTool(newValue.toString()); //update canvas tool
            logger.switchTools(newValue.toString()); //log change
            changeTracker = 0;
        });

        //Color picker for line color
        linePicker = new ColorPicker();
        linePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            canvas.getGraphicsContext2D().setStroke(newValue); //update line color
        });
        linePicker.valueProperty().setValue(Color.BLACK); //default black
        linePicker.getCustomColors().add(Color.TRANSPARENT);
        linePicker.setTooltip(new Tooltip("Color for the line/outline"));
        linePicker.setOnAction(e -> { //update custom colors when new color added
            fillPicker.getCustomColors().clear();
            fillPicker.getCustomColors().addAll(linePicker.getCustomColors());
        });

        //Color picker for fill color
        fillPicker = new ColorPicker();
        fillPicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            canvas.getGraphicsContext2D().setFill(newValue); //update fill
        });
        fillPicker.valueProperty().setValue(Color.TRANSPARENT); //default transparent
        fillPicker.getCustomColors().add(Color.TRANSPARENT);
        fillPicker.setTooltip(new Tooltip("Color to fill the shape"));
        fillPicker.setOnAction(e -> { //update custom colors when new color added
            linePicker.getCustomColors().clear();
            linePicker.getCustomColors().addAll(fillPicker.getCustomColors());
        });

        //Width selector slider for line width and text size, etc
        lineWidthSlider = new Slider(1, 50, 5); //range 1 to 50 with default 5
        lineWidthSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            canvas.getGraphicsContext2D().setLineWidth((double) newValue); //update line width
        });
        canvas.getGraphicsContext2D().setLineWidth(5);
        //show ticks
        lineWidthSlider.setShowTickMarks(true);
        lineWidthSlider.setShowTickLabels(true);
        lineWidthSlider.setMajorTickUnit(49);
        lineWidthSlider.setMinorTickCount(48);
        lineWidthSlider.setTooltip(new Tooltip("Width of the line or outline, or degree of rectangle rounding"));

        //Edge rounding combobox
        edgeRounding = new ComboBox(FXCollections.observableArrayList(new String[]{"Round", "Square"}));
        edgeRounding.valueProperty().addListener((ov, oldValue, newValue) -> {
            //update edge rounding
            if (newValue.toString().equals("Round")) {
                canvas.getGraphicsContext2D().setLineCap(StrokeLineCap.ROUND);
            } else {
                canvas.getGraphicsContext2D().setLineCap(StrokeLineCap.BUTT);
            }
        });
        edgeRounding.getSelectionModel().select(1);
        edgeRounding.setTooltip(new Tooltip("Rounds corners of rectangles and ends of lines"));

        //Zoom in and out
        zoomIn = new Button("Zoom In");
        zoomIn.setTooltip(new Tooltip("Zoom in on the canvas 5%"));
        zoomIn.setOnAction(e -> {
            //remove old scale
            staticCanvasPane.getTransforms().remove(zoomScale);
            //update zoom
            currentZoom += .05;
            zoomScale = new Scale(currentZoom, currentZoom, 0, 0);
            staticCanvasPane.getTransforms().add(zoomScale);
            //update label
            zoom.setText(Math.round(currentZoom * 100) + "%");
            //if we've zoomd in, that means we're not all the way zoomed out
            //so enable zoom out
            zoomOut.setDisable(false);
        });
        zoomOut = new Button("Zoom Out");
        zoomOut.setTooltip(new Tooltip("Zoom out from the canvas 5%"));
        zoomOut.setOnAction(e -> {
            //remove old scale
            staticCanvasPane.getTransforms().remove(zoomScale);
            //update zoom
            currentZoom -= .05;
            zoomScale = new Scale(currentZoom, currentZoom, 0, 0);
            staticCanvasPane.getTransforms().add(zoomScale);
            //update label
            zoom.setText(Math.round(currentZoom * 100) + "%");
            if (currentZoom < .06) { //if we're all the way zoomed out, disable zoom out
                zoomOut.setDisable(true);
            }
        });
        zoom = new Label("100%");
        zoom.setTooltip(new Tooltip("Current Zoom"));

        //Slider for number of vertices in a polygon
        polygonPointsSlider = new Slider(3, 18, 5); //range 3 to 18 with default 5
        polygonPointsSlider.setTooltip(new Tooltip("Number of points to have on a polygon or star"));
        //show ticks
        polygonPointsSlider.setShowTickMarks(true);
        polygonPointsSlider.setShowTickLabels(true);
        polygonPointsSlider.setMajorTickUnit(3);
        polygonPointsSlider.setMinorTickCount(2);
        polygonPointsSlider.setSnapToTicks(true);

        //text field for text writer
        text = new TextArea("Add text here");
        text.setTooltip(new Tooltip("Text to add to the canvas"));
        text.setMaxHeight(TEXT_MAX_HEIGHT);
        text.setMaxWidth(TEXT_MAX_WIDTH);

        //Create MenuBar
        menuBar = new MenuBar();
        menuBar.getMenus().addAll(Arrays.asList(constructMenus(primaryStage)));
        menuBar.setMinWidth(DEFAULT_WIDTH);
        menuBar.setStyle(MENU_BAR_STYLE);
        //Adds event listener for stage resize, menubar resizes too
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            menuBar.setMinWidth(newVal.doubleValue());
        });

        //Root needed for displaying drag previews
        root.getChildren().add(canvas);
        //Pane for zoom scaling
        staticCanvasPane = new Pane();
        staticCanvasPane.getChildren().add(root);
        staticCanvasPane.setMaxSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        staticCanvasPane.setMinSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        //Create scroll bars 
        canvasPane = new ScrollPane();
        canvasPane.setContent(new Group(staticCanvasPane));
        canvasPane.setStyle("-fx-focus-color: transparent;-fx-background: #DCEEFF;"
                + "-fx-background-color: #DCEEFF;");

        mainPane = new GridPane(); //This is the mane grid that includes the menu and canvas
        mainPane.add(menuBar, 0, 0); //add menu to grid
        mainPane.add(canvasPane, 0, 1); //add canvas to grid
        mainPane.setBackground(new Background(new BackgroundFill(new Color(.8588, .9294, 1.0, 1.0),
                CornerRadii.EMPTY, Insets.EMPTY))); //this gives the grid a sexy background color

        scene = new Scene(mainPane); //Creates the scene and passes my pane to it

        /*-------------SET SCENE EVENT LISTERNERS (Keyboard shortcuts)--------------*/
        scene.setOnKeyPressed((e) -> {
            if (new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN).match(e)) {
                //Undo
                menuItems.get("Undo").fire();
            } else if (new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN,
                    KeyCodeCombination.SHIFT_DOWN).match(e)) {
                //Redo
                menuItems.get("Redo").fire();
            } else if (new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN).match(e)) {
                //Save
                menuItems.get("Save").fire();
            } else if (new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN,
                    KeyCodeCombination.SHIFT_DOWN).match(e)) {
                //Save as
                menuItems.get("Save As").fire();
            } else if (new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN).match(e)) {
                //New file
                menuItems.get("New File").fire();
            } else if (e.getCode().equals(KeyCode.F1)) {
                //Show help about
                menuItems.get("About").fire();
            } else if (new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN).match(e)) {
                //load file
                menuItems.get("Load Image").fire();
            } else if (e.getCode().equals(KeyCode.ESCAPE)) {
                //switch to select tool
                mouseTools.setValue("Mouse");
            } else if (new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN).match(e)) {
                //exit
                menuItems.get("Exit").fire();
            } else if (new KeyCodeCombination(KeyCode.F, KeyCodeCombination.ALT_DOWN).match(e)) {
                //show file menu
                if (menus.get("File").isShowing()) {
                    menus.get("File").hide();
                } else {
                    menus.get("File").show();
                }
            } else if (new KeyCodeCombination(KeyCode.H, KeyCodeCombination.ALT_DOWN).match(e)) {
                //show help menu
                if (menus.get("Help").isShowing()) {
                    menus.get("Help").hide();
                } else {
                    menus.get("Help").show();
                }
            } else if (new KeyCodeCombination(KeyCode.V, KeyCodeCombination.ALT_DOWN).match(e)) {
                //show edit menu
                if (menus.get("View").isShowing()) {
                    menus.get("View").hide();
                } else {
                    menus.get("View").show();
                }
            } else if (new KeyCodeCombination(KeyCode.E, KeyCodeCombination.ALT_DOWN).match(e)) {
                //show view menu
                if (menus.get("Edit").isShowing()) {
                    menus.get("Edit").hide();
                } else {
                    menus.get("Edit").show();
                }
            }
        });

        /*---------BUILDING THE STAGE---------*/
        primaryStage.setTitle("Yagerpaint"); //Titles the stage
        primaryStage.minWidthProperty().setValue(MIN_STAGE_WIDTH1); //prevents user from significantly shrinking window
        primaryStage.minHeightProperty().setValue(MIN_STAGE_HEIGHT1);
        primaryStage.setScene(scene); //Attaches the scene to the stage 
        primaryStage.setOnCloseRequest(e -> {//Smart save no matter how the stage is closed
            logger.switchTools(mouseTools.getValue().toString());
            exitBehavior(primaryStage);
            e.consume();
        });
        primaryStage.show();

        //Instantiate secondary stages
        createTimerWindow();
        createToolsWindow();
        createAboutWindow();
        createToolGuideWindow();

        //Shows the tools Window & timer
        toolsWindow.show();
        toolsWindow.setX(10);
        toolsWindow.setY(10);
        timerDisplay.show();
        timerDisplay.setX(10);
        timerDisplay.setY(500);

    }

    /**
     * Builds the file, view, and help menus and their menu items.
     *
     * @param primaryStage primary stage of the application
     * @return Menu[] of all the menus for the menu bar
     */
    private Menu[] constructMenus(Stage primaryStage) {

        /*------------ Create File Menu-------------------*/
        //New Canvas option
        MenuItem newFile = new MenuItem("New Canvas");
        newFile.setOnAction((ActionEvent e) -> {
            createNewCanvasDriver(primaryStage);
        });
        menuItems.put("New File", newFile);

        //Load image option
        MenuItem loadFile = new MenuItem("Load Image");
        loadFile.setOnAction((ActionEvent e) -> {
            loadImageDriver(primaryStage);
        });
        menuItems.put("Load Image", loadFile);

        //Save option
        MenuItem save = new MenuItem("Save");
        save.setOnAction((ActionEvent e) -> {
            if (fileManager.getLoadedFile() == null) {
                fileManager.saveAsDriver(primaryStage, ImageTransformer.canvasToImage(canvas)); //if the user didn't load a file, reverts to saveas
            } else {
                fileManager.saveDriver(ImageTransformer.canvasToImage(canvas)); //save
            }
        });
        menuItems.put("Save", save);

        //Save as option
        MenuItem saveAs = new MenuItem("Save As");
        saveAs.setOnAction((ActionEvent e) -> {
            //save as
            fileManager.saveAsDriver(primaryStage, ImageTransformer.canvasToImage(canvas));
        });
        menuItems.put("Save As", saveAs);

        //Load last autosave button
        MenuItem autosave = new MenuItem("Load Last Auto-Save");
        autosave.setOnAction((ActionEvent e) -> {
            Image picture = fileManager.loadLastAutoSaveDriver();
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.widthProperty().setValue(picture.getWidth()); //sets the canvas dimensions to fit the picture
            canvas.heightProperty().setValue(picture.getHeight());
            canvas.getGraphicsContext2D().drawImage(picture, 0, 0);
            staticCanvasPane.setMaxSize(picture.getWidth(), picture.getHeight());
            staticCanvasPane.setMinSize(picture.getWidth(), picture.getHeight());
        });
        menuItems.put("Load Autosave", autosave);
        MenuItem exitButton = new MenuItem("Exit");
        exitButton.setOnAction((ActionEvent e) -> {
            exitBehavior(primaryStage);
        });
        menuItems.put("Exit", exitButton);

        //Menu button
        Menu file = new Menu("File");
        file.getItems().addAll(newFile, loadFile, save, saveAs, autosave, exitButton);
        file.setStyle(MENU_BAR_STYLE);
        menus.put("File", file);

        /*---------------- Create help menu ---------------*/
        //about option
        MenuItem helpAbout = new MenuItem("About");
        helpAbout.setOnAction((ActionEvent e) -> {
            if (!aboutStage.isShowing()) {
                aboutStage.show();
            } else {
                aboutStage.toFront();
            }

        });
        menuItems.put("About", helpAbout);

        //tools info option
        MenuItem toolsInfo = new MenuItem("Tool Guides");
        toolsInfo.setOnAction((ActionEvent e) -> {
            if (!toolGuide.isShowing()) {
                toolGuide.show();
            } else {
                toolGuide.toFront();
            }
        });
        menuItems.put("Tool Guides", toolsInfo);

        //create help button
        Menu help = new Menu("Help");
        help.getItems().addAll(helpAbout, toolsInfo);
        help.setStyle(MENU_BAR_STYLE);
        menus.put("Help", help);

        /*------------------- Create View Menu ----------------------*/
        //show tools option
        MenuItem showTools = new MenuItem("Show Draw Tools");
        showTools.setOnAction((ActionEvent e) -> {

            if (!toolsWindow.isShowing()) {
                toolsWindow.show();
            } else {
                toolsWindow.toFront();
            }
            toolsWindow.setX(10);
            toolsWindow.setY(10);

        });
        menuItems.put("Show Tools", showTools);

        //toggle tools location option
        MenuItem toggleTools = new MenuItem("Toggle Tools Location");
        toggleTools.setOnAction(e -> {
            toggleTools();
        });
        menuItems.put("Toggle Tools", toggleTools);

        //show tools option
        MenuItem showTimer = new MenuItem("Show Auto-save Timer");
        showTimer.setOnAction((ActionEvent e) -> {

            if (!timerDisplay.isShowing()) {
                timerDisplay.show();
            } else {
                timerDisplay.toFront();
            }
            timerDisplay.setX(10);
            timerDisplay.setY(500);

        });
        menuItems.put("Show Timer", showTimer);

        //create view button
        Menu view = new Menu("View");
        view.getItems().addAll(showTools, toggleTools, showTimer);
        view.setStyle(MENU_BAR_STYLE);
        menus.put("View", view);

        /*----------------CREATE EDIT MENU---------------*/
        //undo
        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e -> {
            Image picture = undoRedo.popUndo(ImageTransformer.canvasToImage(canvas)); //loads the picture
            canvas.widthProperty().setValue(picture.getWidth()); //sets the canvas dimensions to fit the picture
            canvas.heightProperty().setValue(picture.getHeight());
            canvas.getGraphicsContext2D().drawImage(picture, 0, 0);

            if (undoRedo.undoEmpty()) { //update undo and redo button abilities
                menuItems.get("Undo").setDisable(true);
            } else {
                menuItems.get("Undo").setDisable(false);
            }
            if (undoRedo.redoEmpty()) {
                menuItems.get("Redo").setDisable(true);
            } else {
                menuItems.get("Redo").setDisable(false);
            }
        });
        menuItems.put("Undo", undo);
        undo.setDisable(true);

        //redo
        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> {
            Image picture = undoRedo.popRedo(ImageTransformer.canvasToImage(canvas)); //loads the picture
            canvas.widthProperty().setValue(picture.getWidth()); //sets the canvas dimensions to fit the picture
            canvas.heightProperty().setValue(picture.getHeight());
            canvas.getGraphicsContext2D().drawImage(picture, 0, 0);

            if (undoRedo.undoEmpty()) { //update undo and redo button abilities
                menuItems.get("Undo").setDisable(true);
            } else {
                menuItems.get("Undo").setDisable(false);
            }
            if (undoRedo.redoEmpty()) {
                menuItems.get("Redo").setDisable(true);
            } else {
                menuItems.get("Redo").setDisable(false);
            }
        });
        menuItems.put("Redo", redo);
        redo.setDisable(true);

        //scale image
        MenuItem scale = new MenuItem("Scale");
        scale.setOnAction(e -> {
            int[] dimensions = DialogueManager.getNewCanvasDimensions();
            onChangeMade();
            WritableImage newImage = ImageTransformer.bilinearScale(ImageTransformer.canvasToImage(canvas), dimensions[0], dimensions[1]);
            canvas.widthProperty().setValue(newImage.getWidth()); //sets the canvas dimensions to fit the picture
            canvas.heightProperty().setValue(newImage.getHeight());
            canvas.getGraphicsContext2D().setFill(Color.WHITE); //ensures that the canvas behind the picture is white
            canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.getGraphicsContext2D().drawImage(newImage, 0, 0);
            staticCanvasPane.setMinSize(newImage.getWidth(), newImage.getHeight());
        });
        menuItems.put("Scale", scale);

        //Create edit button
        Menu edit = new Menu("Edit");
        edit.getItems().addAll(undo, redo, scale);
        edit.setStyle(MENU_BAR_STYLE);
        menus.put("Edit", edit);

        //return all the menu buttons
        return new Menu[]{file, edit, view, help};

    }

    /*------------Behaviors for buttons and keyboard shortcuts-------------*/
    /**
     * Deletes whatever image is currently in the canvas and creates a new blank
     * canvas with user given dimensions.
     *
     * @param primaryStage primary stage of the application
     */
    private void createNewCanvasDriver(Stage primaryStage) {
        if (fileManager.changesWereMade()) {

            switch (DialogueManager.saveReminder()) {
                case "Save":
                    if (fileManager.getLoadedFile() == null) {
                        fileManager.saveAsDriver(primaryStage, ImageTransformer.canvasToImage(canvas)); //if the user didn't load a file, reverts to saveas
                    } else {
                        fileManager.saveDriver(ImageTransformer.canvasToImage(canvas)); //save
                    }
                case "OK":
                    createNewCanvas();
                    break;
                default:
                    break;
            }

        } else {
            createNewCanvas();
        }
    }

    /**
     * Does the actual canvas creation.
     */
    private void createNewCanvas() {
        int[] dimensions = DialogueManager.getNewCanvasDimensions();
        if (dimensions[0] != 0 && dimensions[1] != 0) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.widthProperty().setValue(dimensions[0]);
            canvas.heightProperty().setValue(dimensions[1]);
            canvas.getGraphicsContext2D().setFill(Color.WHITE); //makes sure whole canvas is white
            canvas.getGraphicsContext2D().fillRect(0, 0, dimensions[0], dimensions[1]);
            fileManager.nullifyLoadedFile();
            staticCanvasPane.setMaxSize(dimensions[0], dimensions[1]);
            staticCanvasPane.setMinSize(dimensions[0], dimensions[1]);
        }
    }

    /**
     * Loads a user-chosen image file as a new canvas.
     *
     * @param primaryStage primary stage of the application
     */
    private void loadImageDriver(Stage primaryStage) {
        if (fileManager.changesWereMade()) {

            switch (DialogueManager.saveReminder()) {
                case "Save":
                    if (fileManager.getLoadedFile() == null) {
                        fileManager.saveAsDriver(primaryStage, ImageTransformer.canvasToImage(canvas)); //if the user didn't load a file, reverts to saveas
                    } else {
                        fileManager.saveDriver(ImageTransformer.canvasToImage(canvas)); //save
                    }
                case "OK":
                    loadImage(primaryStage);
                    break;
                default:
                    break;
            }

        } else {
            loadImage(primaryStage);
        }
        if (fileManager.hasTransparency()) { //If the image is transparent
            transparencyGrid = new ImageView(ResourceLoader.getTransparencyGrid((int) canvas.getWidth(), (int) canvas.getHeight()));
            root.getChildren().remove(canvas);
            root.getChildren().add(transparencyGrid); //add transparency grid behind the image
            root.getChildren().add(canvas);
        } else {
            root.getChildren().remove(transparencyGrid);
        }

    }

    /**
     * Does the actual loading of the image file.
     *
     * @param primaryStage primary stage of application
     */
    private void loadImage(Stage primaryStage) {
        Image picture = fileManager.loadFileDriver(primaryStage); //loads the picture
        if (picture != null) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.widthProperty().setValue(picture.getWidth()); //sets the canvas dimensions to fit the picture
            canvas.heightProperty().setValue(picture.getHeight());
            canvas.getGraphicsContext2D().drawImage(picture, 0, 0);
            staticCanvasPane.setMaxSize(picture.getWidth(), picture.getHeight());
            staticCanvasPane.setMinSize(picture.getWidth(), picture.getHeight());
        }
    }

    /**
     * Move tools from external stage into bar on main stage, and vice versa.
     */
    private void toggleTools() {
        if (toolsSeparate) {
            createToolsBar();
            toolsWindow.close(); //close the tools window if it's open
            menuItems.get("Show Tools").setDisable(true); //disable show tools, it's not needed
            //remove canvas and replace it lower, and add the toolbar in above it
            mainPane.getChildren().remove(canvasPane);
            mainPane.add(toolsBar, 0, 1);
            mainPane.add(canvasPane, 0, 2);
            //set toolsSeparate to false
            toolsSeparate = !toolsSeparate;
        } else {
            createToolsWindow();
            //remove toolbar and replace canvas
            mainPane.getChildren().remove(toolsBar);
            mainPane.getChildren().remove(canvasPane);
            mainPane.add(canvasPane, 0, 1);
            //enable show tools, so the user can get the tools window back
            menuItems.get("Show Tools").setDisable(false);
            //show the external tools stage
            toolsWindow.show();
            toolsWindow.toFront();
            toolsWindow.setX(10);
            toolsWindow.setY(10);
            //set toolsSeparate to true
            toolsSeparate = !toolsSeparate;
        }
    }

    /**
     * Close application with smart save. If the user has not saved the file
     * they are working on, prompt them to do so.
     *
     * @param primaryStage primary stage of the application
     */
    private void exitBehavior(Stage primaryStage) {
        if (fileManager.changesWereMade()) {
            switch (DialogueManager.saveReminder()) {
                case "Save":
                    if (fileManager.getLoadedFile() == null) {
                        fileManager.saveAsDriver(primaryStage, ImageTransformer.canvasToImage(canvas)); //if the user didn't load a file, reverts to saveas
                    } else {
                        fileManager.saveDriver(ImageTransformer.canvasToImage(canvas)); //save
                    }
                case "OK":
                    logger.updateCurrentTime();
                    logger.saveToFile();
                    primaryStage.close();
                    System.exit(0);
                    break;
                default:
                    break;
            }
        } else {
            primaryStage.close(); //closes the application window
            System.exit(0); //closes java
        }
    }

    /*----------CREATE SECONDARY STAGES AND IN-STAGE TOOL BAR-----------*/
    /**
     * Add all the 'about' information to the about window.
     */
    private void createAboutWindow() {

        aboutStage = new Stage();
        aboutStage.setTitle("About YagerPaint");

        //Create Title for stage
        Label title = new Label("About YagerPaint");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        title.setStyle("-fx-padding: 8 8 8 8");

        //Make info scrollable
        ScrollPane aboutScroll = new ScrollPane();
        aboutScroll.setStyle("-fx-focus-color: transparent;");

        //version number
        Label version = new Label("YagerPaint version " + ResourceLoader.getVersion());
        version.setFont(Font.font("Times New Roman", FontWeight.MEDIUM, 16));
        version.setStyle("-fx-padding: 8 8 32 8");
        version.wrapTextProperty().setValue(true);

        //loads all the release notes
        Label releaseNotes = new Label(ResourceLoader.getReleaseNotes());
        releaseNotes.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 14));
        releaseNotes.setStyle("-fx-padding: 8 8 8 8");
        releaseNotes.wrapTextProperty().setValue(true);

        //Organize info into grid
        GridPane organizer = new GridPane();
        organizer.add(title, 0, 0);
        organizer.add(version, 0, 1);
        organizer.add(releaseNotes, 0, 2);

        //Set scene for stage
        aboutScroll.setContent(organizer);
        aboutStage.setScene(new Scene(aboutScroll, 600, 500));
    }

    /**
     * Add all the tool information to the tool guide window.
     */
    private void createToolGuideWindow() {

        toolGuide = new Stage();
        toolGuide.setTitle("Tool Guide");

        //Create Title for stage
        Label title = new Label("Guide to YagerPaint Tools");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        title.setStyle("-fx-padding: 8 8 8 8");

        //Make info scrollable
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-focus-color: transparent;");
        scroll.setMaxWidth(600);

        boolean isParagraph = true;
        ArrayList<Label> toolGuideLabels = new ArrayList<>();

        /*The get tool guide method from file manager splits the tool guide document
        at the double new lines and returns. Since it is formatted so that every other
        String will be a heading, I use is paragraph to swith between styles, starting 
        with the welcome paragraph*/
        for (String x : ResourceLoader.getToolGuide()) {

            Label temp = new Label(x);
            temp.wrapTextProperty().setValue(true);
            if (isParagraph) {
                temp.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 14));
                temp.setStyle("-fx-padding: 8 8 8 8");
            } else { //is heading
                temp.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
                temp.setStyle("-fx-padding: 8 8 0 8");
            }
            toolGuideLabels.add(temp);
            isParagraph = !isParagraph;

        }

        //Organize info into grid
        GridPane organizer = new GridPane();
        organizer.add(title, 0, 0);

        for (int i = 0; i < toolGuideLabels.size(); i++) {
            organizer.add(toolGuideLabels.get(i), 0, i + 1);
        }

        //Set scene for stage
        scroll.setContent(organizer);
        toolGuide.setScene(new Scene(scroll, 600, 500));

    }

    /**
     * Create the auto-save timer display window.
     */
    private void createTimerWindow() {

        timerDisplay = new Stage();

        SaveTimerDisplay saveTimerDisplay = new SaveTimerDisplay(SAVE_FREQUENCY, () -> {
            fileManager.autoSaveDriver(ImageTransformer.canvasToImage(canvas));
        });
        saveTimerDisplay.setTooltip(new Tooltip("Time until next auto-save"));

        timerDisplay.setScene(new Scene(saveTimerDisplay));
        saveTimerDisplay.start();

    }

    /**
     * Create the tools stage and add all the tools to it.
     */
    private void createToolsWindow() {

        //Initialize new stage and pane to contain buttons
        toolsWindow = new Stage();
        GridPane pane = new GridPane();
        pane.setStyle("-fx-focus-color: transparent;-fx-background: #DCEEFF;-fx-background-color: #DCEEFF;");

        //Add all the tools
        pane.add(new Label("Line Color"), 0, 0);
        pane.add(linePicker, 1, 0);
        pane.add(new Label("Fill Color"), 0, 1);
        pane.add(fillPicker, 1, 1);
        pane.add(new Label("Line Width"), 0, 2);
        pane.add(lineWidthSlider, 1, 2);
        pane.add(new Label("Mouse Tool"), 0, 3);
        pane.add(mouseTools, 1, 3);
        pane.add(new Label("Edge Rounding"), 0, 4);
        pane.add(edgeRounding, 1, 4);
        pane.add(zoomIn, 0, 5);
        pane.add(zoomOut, 1, 5);
        pane.add(new Label("Current Zoom:"), 0, 6);
        pane.add(zoom, 1, 6);
        pane.add(new Label("Sides:"), 0, 7);
        pane.add(polygonPointsSlider, 1, 7);
        pane.add(new Label("Text:"), 0, 8);
        pane.add(text, 1, 8);

        toolsWindow.setScene(new Scene(pane));
        toolsWindow.setTitle("Drawing Tools");
        toolsWindow.minWidthProperty().setValue(MIN_STAGE_WIDTH2);
        toolsWindow.minHeightProperty().setValue(MIN_STAGE_HEIGHT2);
    }

    /**
     * Create the tool bar gridpane and add all the tools to it.
     */
    private void createToolsBar() {

        //Create new grid to put the tools in the primary window
        toolsBar = new GridPane();

        //Add all the tools
        toolsBar.add(new Label("Line Color"), 0, 0);
        toolsBar.add(linePicker, 0, 1);
        toolsBar.add(new Label("Fill Color"), 1, 0);
        toolsBar.add(fillPicker, 1, 1);
        toolsBar.add(new Label("Line Width"), 2, 0);
        toolsBar.add(lineWidthSlider, 2, 1);
        toolsBar.add(new Label("Mouse Tool"), 3, 0);
        toolsBar.add(mouseTools, 3, 1);
        toolsBar.add(new Label("Edge Rounding"), 4, 0);
        toolsBar.add(edgeRounding, 4, 1);
        toolsBar.add(zoomIn, 5, 0);
        toolsBar.add(zoomOut, 5, 1);
        toolsBar.add(new Label("Current Zoom:"), 6, 0);
        toolsBar.add(zoom, 6, 1);
        toolsBar.add(new Label("Sides:"), 7, 0);
        toolsBar.add(polygonPointsSlider, 7, 1);
        toolsBar.add(new Label("Text:"), 8, 0);
        toolsBar.add(text, 8, 1);

    }

    /**
     * Add all the tools to the PaintCanvas.
     * 
     * @param primaryStage main stage of the application
     */
    private void addToolsToDict(Stage primaryStage) {
        //canvas.addTool("Mouse", null);
        canvas.addTool("Line", new LineTool(canvas));
        canvas.addTool("Free Draw", new FreeDrawTool(canvas));
        canvas.addTool("Erase", new EraseTool(canvas));
        canvas.addTool("Square", new SquareTool(canvas, () -> {
            return 5 * polygonPointsSlider.getValue();
        }));
        canvas.addTool("Rectangle", new RectangleTool(canvas, () -> {
            return 5 * polygonPointsSlider.getValue();
        }));
        canvas.addTool("Circle", new CircleTool(canvas));
        canvas.addTool("Ellipse", new EllipseTool(canvas));
        canvas.addTool("Regular Polygon", new RegularPolygonTool(canvas, () -> {
            return (int) polygonPointsSlider.getValue();
        }));
        canvas.addTool("Irregular Polygon", new IrregularPolygonTool(canvas, () -> {
            return (int) polygonPointsSlider.getValue();
        }));
        canvas.addTool("Star", new StarTool(canvas, () -> {
            return (int) polygonPointsSlider.getValue();
        }));
        canvas.addTool("Text", new TextTool(canvas, () -> {
            return text.getText();
        }));
        canvas.addTool("Grab Color", new GrabColorTool(canvas, (color) -> {
            this.newColorGrabbed(color);
        }));
        canvas.addTool("Copy & Paste", new SelectAndMoveTool(canvas));
        canvas.addTool("Cut & Paste", new CutAndMoveTool(canvas));
        canvas.addTool("Fill", new FillTool(canvas));
        canvas.addTool("Image Fill", new ImageFillTool(canvas, () -> {
            return fileManager.loadFileDriver(primaryStage);
        }));
        canvas.addTool("Rainbow Fun Fill", new RainbowFunFillTool(canvas));
    }

    /**
     * Call before making changes to canvas. Every time canvas is changed!
     */
    private void onChangeMade() {

        switch (canvas.getCurrentTool()) {
            case "Mouse":
            case "Grab Color":
                break;
            case "Copy & Paste":
            case "Cut & Paste":
                if (changeTracker == 0) {
                    fileManager.setChangesMade(); //record that change was made
                    undoRedo.pushUndo(ImageTransformer.canvasToImage(canvas)); //save canvas to undo
                    changeTracker++;
                } else {
                    changeTracker = 0;
                }
                break;
            case "Irregular Polygon":
                if (changeTracker == 0) {
                    fileManager.setChangesMade(); //record that change was made
                    undoRedo.pushUndo(ImageTransformer.canvasToImage(canvas)); //save canvas to undo
                }
                changeTracker++;
                if (changeTracker == (int) polygonPointsSlider.getValue()) {
                    changeTracker = 0;
                }
                break;
            default:
                fileManager.setChangesMade(); //record that change was made
                undoRedo.pushUndo(ImageTransformer.canvasToImage(canvas)); //save canvas to undo
                break;
        }
        if (undoRedo.undoEmpty()) { //update undo and redo button abilities
            menuItems.get("Undo").setDisable(true);
        } else {
            menuItems.get("Undo").setDisable(false);
        }
        if (undoRedo.redoEmpty()) {
            menuItems.get("Redo").setDisable(true);
        } else {
            menuItems.get("Redo").setDisable(false);
        }

    }

    /**
     * Update the color lists with the color that has been grabbed.
     *
     * @param color color that has been grabbed
     */
    public void newColorGrabbed(Color color) {

        linePicker.getCustomColors().add(color); //add color to colorpickers' lists
        fillPicker.getCustomColors().add(color);
        linePicker.setValue(color); //set line color to picked color

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

}
