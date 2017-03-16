package GUI.Dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class VariableDialog extends Dialog{

    private ButtonType okButtonType;
    private TextField variableName;
    private TextField variableValue;

    public VariableDialog() {
        this.setTitle("Create Variable");
        this.setHeaderText("Name the variable and its value");

        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        variableName = new TextField();
        variableName.requestFocus();
        variableName.setPromptText("Name");

        variableValue = new TextField();
        variableValue.requestFocus();
        variableValue.setPromptText("Value");

        gridPane.add(new Label("$"), 0, 0);
        gridPane.add(variableName, 1, 0);
        gridPane.add(new Label(" = "), 2, 0);
        gridPane.add(variableValue, 3, 0);

        this.getDialogPane().setContent(gridPane);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType)
                return new Pair<String, String>(variableName.getText(), variableValue.getText());

            return null;
        });
    }
}
