package GUI;

import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class EditorStackPane extends StackPane{
    public EditorStackPane() {
        super(new VirtualizedScrollPane(new CodeEditor()));
    }
}
