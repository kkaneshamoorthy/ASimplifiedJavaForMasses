import controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SimplifiedJava extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/ux.fxml"));
        loader.setController(new Controller());
        Pane mainPane = loader.load();
        primaryStage.setTitle("A simplified Programming Language for the Casuals");
        Scene scene = new Scene(mainPane, 1024, 768);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/java_keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
