package controllers;

import Engine.CodeExecution;
import Engine.LexicalAnalyser;
import GUI.CodeEditor;
import GUI.Dialog.*;
import GUI.EditorStackPane;
import Instruction.Instruction;
import Utility.FileUtility;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.Callback;
import javafx.util.Pair;
import org.fxmisc.flowless.VirtualizedScrollPane;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private EditorStackPane editorStackPane;
    @FXML
    private CodeEditor editor;
    @FXML
    private TreeView treeView;
    @FXML
    private TextArea console;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("View is loaded!");
        VirtualizedScrollPane v = (VirtualizedScrollPane) editorStackPane.getChildren().get(0);
        editor = (CodeEditor) v.getContent();
        loadTreeItems();

        this.console.setEditable(false);
        setConsoleMessage();
        setEditorDefaultText();
    }

    public void loadTreeItems() {

        treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> stringTreeView) {
                TreeCell<String> treeCell = new TreeCell<String>() {
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null)
                            setText(item);
                    }
                };

                treeCell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2 && treeCell.getTreeItem() != null) {
                            String selectedItem = treeCell.getTreeItem().getValue();
                            System.out.println(selectedItem);
                            switch (selectedItem) {
                                case "Loop":
                                    LoopDialog loopDialog= new LoopDialog();
                                    Optional<Pair<String, String>>  result = loopDialog.showAndWait();
                                    if (result.isPresent())
                                        editor.appendText("Loop " + result.get().getKey() + " times \n\t");
                                    break;
                                case "If":
                                    IfDialog ifDialog = new IfDialog();
                                    Optional<String> condition = ifDialog.showAndWait();
                                    if (condition.isPresent())
                                        editor.appendText("if " + condition.get() + " \n\t");
                                    break;
                                case "Print":
                                    PrintDialog printDialog = new PrintDialog();
                                    Optional<String> dataToPrint = printDialog.showAndWait();
                                    if (dataToPrint.isPresent())
                                        editor.appendText("print " + dataToPrint.get() + " \n");
                                    break;
                                case "Input":
                                    InputDialog inputDialog = new InputDialog();
                                    Optional<String> locationOfInput = inputDialog.showAndWait();
                                    if (locationOfInput.isPresent())
                                        editor.appendText("take user input from " + locationOfInput.get() + " \n");
                                    break;
                                case "Create Variable":
                                    VariableDialog variableDialog = new VariableDialog();
                                    Optional<Pair<String, String>> variableData = variableDialog.showAndWait();
                                    if (variableData.isPresent())
                                        editor.appendText("$" + variableData.get().getKey() + " = " + variableData.get().getValue() + " \n");
                                    break;
                                case "Function":
                                    FunctionDialog functionDialog = new FunctionDialog();
                                    Optional<String> functionName = functionDialog.showAndWait();
                                    if (functionName.isPresent())
                                        editor.appendText("function " + functionName.get() + ": \n\t");
                                    break;
                            }
                        }
                    }
                });

                treeCell.setOnMouseDragged(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                      Bounds s = mouseEvent.getPickResult().getIntersectedNode().getBoundsInLocal();
                      Bounds t = editorStackPane.getBoundsInLocal();
                      mouseEvent.setDragDetect(true);
                    }
                });

                return treeCell;
            }
        });

        TreeItem<String> root = new TreeItem<String>("Instructions");
        TreeItem<String> variableItem = new TreeItem<>("Variable");
        variableItem.setExpanded(true);
        TreeItem<String> instructionItem = new TreeItem<>("Instructions");
        instructionItem.setExpanded(true);

        instructionItem.getChildren().add(new TreeItem<>("Loop"));
        instructionItem.getChildren().add(new TreeItem<>("If"));
        instructionItem.getChildren().add(new TreeItem<>("Print"));
        instructionItem.getChildren().add(new TreeItem<>("Input"));
        instructionItem.getChildren().add(new TreeItem<>("Function"));

        variableItem.getChildren().add(new TreeItem<>("Create Variable"));
        variableItem.getChildren().add(new TreeItem<>("Edit Variable"));

        root.getChildren().add(instructionItem);
        root.getChildren().add(variableItem);

        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.setContextMenu(new ContextMenu(new MenuItem("Create Variable")));
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
    private void run(ActionEvent event) throws Exception {
        LexicalAnalyser la = new LexicalAnalyser();
        HashMap<Integer, Instruction> instructions = la.lexicalAnalyser(this.editor.getText().split("\\n"));
        HashMap<Integer, String> javaCode = la.codeGeneration(instructions);
        console.clear();
        console.setStyle("-fx-text-fill: black;");
        setConsoleMessage();
        CodeExecution codeExecution = new CodeExecution(console);
        codeExecution.executeCode(la.getInstructionStorage(), la.getVariableHolder());
    }

    @FXML
    private void generateJavaCode(ActionEvent event) {
        LexicalAnalyser la = new LexicalAnalyser();
        HashMap<Integer, Instruction> hashMap = la.lexicalAnalyser(this.editor.getText().split("\\n"));
        la.generateCode(hashMap);
    }

    @FXML
    private void dragOver(MouseEvent event) {
        System.out.println("ACCEPTED");
    }

    @FXML
    private void dragDropped(MouseEvent event) {
        System.out.println("onDragDropped");
        event.consume();
    }

    private void setConsoleMessage() {
        this.console.insertText(this.console.getLength(), "------------Output of code execution------------");
        this.console.insertText(this.console.getLength(), "\n");
    }

    private void setEditorDefaultText() {
        this.editor.insertText(this.console.getLength(), "function main:");
        this.editor.insertText(this.console.getLength(), "\n\t");
    }

}
