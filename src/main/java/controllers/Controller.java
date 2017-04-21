package controllers;

import Engine.CodeExecution;
import Engine.CodeGeneration;
import Engine.InstructionSet;
import GUI.CodeEditor;
import GUI.Console;
import GUI.Dialog.*;
import GUI.EditorStackPane;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private EditorStackPane editorStackPane;
    @FXML
    private CodeEditor editor;
    @FXML
    private TreeView instructionView;
    @FXML
    private TreeView storageView;
    @FXML
    private Console console;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("View is loaded!");
        VirtualizedScrollPane v = (VirtualizedScrollPane) editorStackPane.getChildren().get(0);
        editor = (CodeEditor) v.getContent();
        loadInstructionItems();
//        loadStorageItems();

        this.console.setEditable(false);
        setConsoleMessage();
        setEditorDefaultText();
    }

    @FXML
    public void refreshRecord() {
//        AvailableStorage availableStorage = new AvailableStorage(this.storageView, this.editor);
//        availableStorage.run();
    }


    public void loadStorageItems() {
        this.storageView.setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public TreeCell call(TreeView param) {
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

        TreeItem<String> root = new TreeItem<String>("Storage");
        TreeItem<String> functionItem = new TreeItem<>("Functions");
        functionItem.setExpanded(true);
        root.getChildren().add(functionItem);

        storageView.setRoot(root);
        storageView.setShowRoot(false);
    }

    public void loadInstructionItems() {

        instructionView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
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
                            switch (selectedItem) {
                                case "Loop":
                                    LoopDialog loopDialog= new LoopDialog();
                                    Optional<Pair<String, String>>  result = loopDialog.showAndWait();
                                    if (result.isPresent())
                                        editor.appendText("Loop " + result.get().getKey() + " times and increment by " + result.get().getValue() + "\n\t");
                                    break;
                                case "If":
                                    IfDialog ifDialog = new IfDialog();
                                    Optional<String> condition = ifDialog.showAndWait();
                                    if (condition.isPresent())
                                        editor.appendText("define a condition where if " + condition.get() + " \n\t");
                                    break;
                                case "Print":
                                    PrintDialog printDialog = new PrintDialog();
                                    Optional<String> dataToPrint = printDialog.showAndWait();
                                    if (dataToPrint.isPresent())
                                        editor.appendText("show " + dataToPrint.get() + " \n");
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
                                        editor.appendText("define a variable $" + variableData.get().getKey() + " = " + variableData.get().getValue() + " \n");
                                    break;
                                case "Function":
                                    FunctionDialog functionDialog = new FunctionDialog();
                                    Optional<String> functionName = functionDialog.showAndWait();
                                    if (functionName.isPresent())
                                        editor.appendText("define a function called " + functionName.get() + "(): \n\t");
                                    break;
                                case "Else":
                                    editor.appendText("otherwise \n\t");
                                    break;
                                case "Function Call":
                                    FunctionCallDialog functionCallDialog = new FunctionCallDialog();
                                    Optional<String>  functionCall = functionCallDialog.showAndWait();
                                    if (functionCall.isPresent())
                                        editor.appendText("call " + functionCall.get() + "() \n\t");
                                    break;
                                case "End":
                                    editor.appendText("end \n\t");
                                    break;
                                default:
                                        System.out.println(selectedItem);
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
        TreeItem<String> instructionItem = new TreeItem<>("Instructions");
        instructionItem.setExpanded(true);

        instructionItem.getChildren().add(new TreeItem<>("Loop"));
        instructionItem.getChildren().add(new TreeItem<>("If"));
        instructionItem.getChildren().add(new TreeItem<>("Function"));
        instructionItem.getChildren().add(new TreeItem<>("Function Call"));
        instructionItem.getChildren().add(new TreeItem<>("Print"));
        instructionItem.getChildren().add(new TreeItem<>("Input"));
        instructionItem.getChildren().add(new TreeItem<>("Create Variable"));
        instructionItem.getChildren().add(new TreeItem<>("Else"));
        instructionItem.getChildren().add(new TreeItem<>("End"));

        root.getChildren().add(instructionItem);

        instructionView.setRoot(root);
        instructionView.setShowRoot(false);
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
        console.clear();
        console.setStyle("-fx-text-fill: black;");
        setConsoleMessage();
        CodeExecution codeExecution = new CodeExecution(console);
        codeExecution.executeCode(this.editor.getText().split("\\n"));
    }

    @FXML
    private void generateJavaCode(ActionEvent event) {
        console.clear();
        console.setStyle("-fx-text-fill: black;");
        setConsoleMessage();
        CodeGeneration codeGeneration = new CodeGeneration(console);
        codeGeneration.generateCode(this.editor.getText().split("\\n"));
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
        this.editor.insertText(this.console.getLength(), "function main():");
        this.editor.insertText(this.console.getLength(), "\n\t");
    }

}
