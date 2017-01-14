package GUI;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class Console extends CodeArea {
    public Console() {
        super();
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
    }
}
