package GUI.Dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class FunctionDialog extends Dialog{
    private ButtonType okButtonType;
    private TextField nameOfFunction;

    public FunctionDialog() {
        this.setTitle("Create Function");
        this.setHeaderText("What do you want to call the function?");

        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        nameOfFunction = new TextField();
        nameOfFunction.requestFocus();
        nameOfFunction.setPromptText("Name of Function");

        gridPane.add(new Label("function "), 0, 0);
        gridPane.add(nameOfFunction, 1, 0);
        gridPane.add(new Label(": "), 2, 0);

        this.getDialogPane().setContent(gridPane);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType)
                return nameOfFunction.getText();

            return null;
        });
    }
}
