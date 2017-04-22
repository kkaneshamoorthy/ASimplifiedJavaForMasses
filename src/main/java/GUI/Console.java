package GUI;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class Console extends CodeArea {
    public Console() {
        super();
    }

    public void reportError(String errorMessage) {
        this.append(errorMessage);
    }

    public void append(String str) {
        this.insertText(this.getLength(), str);
        this.insertText(this.getLength(), "\n");
    }
}
