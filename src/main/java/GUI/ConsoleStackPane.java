package GUI;

import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class ConsoleStackPane extends StackPane {
    public ConsoleStackPane() {
        super(new VirtualizedScrollPane(new Console()));
    }
}
