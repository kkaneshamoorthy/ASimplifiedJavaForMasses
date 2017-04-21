package GUI.Dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Created by kowrishankar on 21/04/17.
 */
public class FunctionCallDialog extends Dialog {
    private ButtonType okButtonType;
    private TextField nameOfFunction;

    public FunctionCallDialog() {
        this.setTitle("Function Call");
        this.setHeaderText("Name the function you want to call:");

        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        nameOfFunction = new TextField();
        nameOfFunction.requestFocus();
        nameOfFunction.setPromptText("Name of the function you want to call:");

        gridPane.add(new Label("Function name: "), 0, 0);
        gridPane.add(nameOfFunction, 1, 0);
        gridPane.add(new Label(""), 2, 0);

        this.getDialogPane().setContent(gridPane);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType)
                return nameOfFunction.getText();

            return null;
        });
    }
}
