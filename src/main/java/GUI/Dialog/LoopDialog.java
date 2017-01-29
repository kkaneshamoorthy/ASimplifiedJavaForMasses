package GUI.Dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class LoopDialog extends Dialog {

    private ButtonType okButtonType;
    private TextField numOfIteration;
    private TextField incrementBy;

    public LoopDialog() {
        this.setTitle("Loop Instruction");
        this.setHeaderText("Loop Instruction");

        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        numOfIteration = new TextField();
        numOfIteration.setPromptText("Number of Iterations ");
        numOfIteration.requestFocus();
        incrementBy = new TextField();
        incrementBy.setPromptText("Increment by: ");

        gridPane.add(new Label("Loop "), 0, 0);
        gridPane.add(numOfIteration, 1, 0);
        gridPane.add(new Label("times "), 2, 0);
        gridPane.add(new Label("Increment By:"), 0, 1);
        gridPane.add(incrementBy, 1, 1);

        this.getDialogPane().setContent(gridPane);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType)
                return new Pair<>(numOfIteration.getText(), incrementBy.getText());

            return null;
        });
    }
}
