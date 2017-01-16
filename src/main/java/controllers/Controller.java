package controllers;

import Engine.LexerAnalyser;
import GUI.CodeEditor;
import GUI.EditorStackPane;
import Utility.FileUtility;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.fxmisc.flowless.VirtualizedScrollPane;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private EditorStackPane editorStackPane;
    private CodeEditor editor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("View is now loaded!");
        VirtualizedScrollPane v = (VirtualizedScrollPane) editorStackPane.getChildren().get(0);
        editor = (CodeEditor) v.getContent();
    }

    @FXML
    private void exit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void saveFile(ActionEvent event) {
        FileUtility.saveFile(null, editor.getText());
    }

    @FXML
    private void openFile(ActionEvent event) {
        FileUtility.openFile(null, editor);
    }

    @FXML
    private void run(ActionEvent event) {
        LexerAnalyser la = new LexerAnalyser();
        //HashMap<Integer, HashMap<String, String>> instructions = la.lexicalAnalyser(this.editor.getText().split("\\n"));
        //la.codeExecution(instructions);
    }

    @FXML
    private void generateJavaCode(ActionEvent event) {
        LexerAnalyser la = new LexerAnalyser();
        la.generateCode(this.editor.getText().split("\\n"));
    }
}
