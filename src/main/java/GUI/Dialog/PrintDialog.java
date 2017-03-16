package GUI.Dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

public class PrintDialog extends Dialog{

    private ButtonType okButtonType;
    private TextField dataToPrint;

    public PrintDialog() {
        this.setTitle("Print Instruction");
        this.setHeaderText("What would you like to print?");

        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        dataToPrint = new TextField();
        dataToPrint.requestFocus();
        dataToPrint.setPromptText("");

        gridPane.add(new Label("Print"), 0, 0);
        gridPane.add(dataToPrint, 1, 0);

        this.getDialogPane().setContent(gridPane);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType)
                return dataToPrint.getText();

            return null;
        });
    }
}
