package GUI;

import Engine.LexerAnalyser;
import Utility.FileUtility;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.stage.Stage;

public class SimplifiedJava extends Application {
    TextArea textArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("A simplified Java for the masses");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1500, 800, Color.WHITE);

        textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMinWidth(650);
        textArea.setMinHeight(500);

//        Thread keyTermHighligher = new Thread(new HighlightKeyWord(textArea));
//        keyTermHighligher.setDaemon(true);
//        keyTermHighligher.start();

        TextArea console = new TextArea();
        console.setEditable(false);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        // File menu - new, save, exit
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem openMenuItem = new MenuItem("Open");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(ActionEvent -> Platform.exit());

        openMenuItem.setOnAction(ActionEvent -> FileUtility.openFile(primaryStage, textArea));
        saveMenuItem.setOnAction(ActionEvent -> FileUtility.saveFile(primaryStage, textArea.getText()));

        fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem,
                new SeparatorMenuItem(), exitMenuItem);

        // Run
        Menu runMenu = new Menu("Run");
        MenuItem runMenuItem = new MenuItem("Run program");
        runMenuItem.setOnAction(ActionEvent -> new LexerAnalyser().analyse(textArea.getText().replaceAll("(\\r|\\n)", "").split(";")));

        runMenu.getItems().add(runMenuItem);

        // Tutorial
        Menu tutorialMenu = new Menu("Tutorial");

        // Window
        Menu windowMenu = new Menu("Window");

        // Help
        Menu helpMenu = new Menu("Help");


        menuBar.getMenus().addAll(fileMenu, runMenu, tutorialMenu, windowMenu, helpMenu);

        VBox vbox = new VBox(textArea);

        VBox consoleVBox = new VBox(console);
        consoleVBox.setMaxWidth(Double.MAX_VALUE);
        consoleVBox.setMinWidth(650);
        consoleVBox.setMinHeight(500);

        root.setCenter(vbox);
        root.setBottom(consoleVBox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
