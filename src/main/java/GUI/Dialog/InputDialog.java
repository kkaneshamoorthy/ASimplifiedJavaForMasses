package GUI.Dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class InputDialog extends Dialog {

    private ButtonType okButtonType;
    private TextField location;

    public InputDialog() {
        this.setTitle("Input Instruction");
        this.setHeaderText("Where do you want to take the user input from?");

        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        location = new TextField();
        location.setPromptText("From ");

        gridPane.add(new Label("Take user input from "), 0, 0);
        gridPane.add(location, 1, 0);

        this.getDialogPane().setContent(gridPane);
        location.requestFocus();

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType)
                return location.getText();

            return null;
        });
    }
}
