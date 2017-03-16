package GUI.Dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class IfDialog extends Dialog{

    private ButtonType okButtonType;
    private TextField condition;

    public IfDialog() {
        this.setTitle("If Instruction");
        this.setHeaderText("When should this condition be valid?");

        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        condition = new TextField();
        condition.requestFocus();
        condition.setPromptText("Condition");

        gridPane.add(new Label("If "), 0, 0);
        gridPane.add(condition, 1, 0);

        this.getDialogPane().setContent(gridPane);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType)
                return condition.getText();

            return null;
        });
    }
}
