package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;

public class HelpDocumentation extends TableView<InstructionHelpMessage> {
    private final ObservableList<InstructionHelpMessage> data =
            FXCollections.observableArrayList(
                    new InstructionHelpMessage(
                            "Print",
                            "Prints provided data to the console",
                            "print \"Hello World!\" \nshow \"Hello World!\""
                    ),
                    new InstructionHelpMessage(
                            "Function",
                            "Groups instructions together, that can be called by calling the function name",
                            "define a function called myFunction() \nwrite a method called myFunction()"
                    ),
                    new InstructionHelpMessage(
                            "Variable",
                            "A way of storing data that can be edited and retrieved",
                            "$variableName = \"data\""
                    ),
                    new InstructionHelpMessage(
                            "Loop",
                            "Allows a group of instructions to be repeated",
                            "define a loop to go through 10 times \nrepeat the code 10 times"
                    ),
                    new InstructionHelpMessage(
                            "Function call",
                            "Calls a function, a block code to be executed",
                            "call functionName()"
                    ),
                    new InstructionHelpMessage(
                            "If",
                            "Executes a block of code if the condition is met",
                            "execute the code if 2 == 2 \nwrite an if statement to be executed if 2 == 2"
                    ),
                    new InstructionHelpMessage(
                            "End",
                            "Ends a block of instruction",
                            "end for loop"
                    ),
                    new InstructionHelpMessage(
                            "Input",
                            "Gets input for the user that gets assigned to a variable",
                            "$x = get user input"
                    )


            );

    public HelpDocumentation() {
        this.setId("docHelp");
        final Label label = new Label("Language Documentation");
        label.setFont(new Font("Arial", 20));
        this.setEditable(false);

        TableColumn instruction = new TableColumn("Instruction");
        instruction.setMinWidth(341);
        instruction.setCellValueFactory(
                new PropertyValueFactory<InstructionHelpMessage, String>("instruction"));


        TableColumn description = new TableColumn("Description");
        description.setMinWidth(341);
        description.setCellValueFactory(
                new PropertyValueFactory<InstructionHelpMessage, String>("description"));


        TableColumn example = new TableColumn("Code examples");
        example.setMinWidth(341);
        example.setCellValueFactory(
                new PropertyValueFactory<InstructionHelpMessage, String>("example"));


        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setItems(data);
        this.getColumns().addAll(instruction, description, example);
    }
}