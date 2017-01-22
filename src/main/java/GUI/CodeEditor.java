package GUI;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends CodeArea {

    private static final String[] KEYWORDS = new String[] {
            "print", "input", "loop", "$", "if", "equal"
    };

    private static final String[] ARITHMETIC_OPERATION_PATTERN = new String[] {
            "+", "-", "/", "*", "%"
    };

    private static final String[] RELATIONAL_OPERATION_PATTERN = new String[] {
            "==", "<", ">", "<=", ">=", "!", "!="
    };

    private static final String[] BITWISE_OPERATION_PATTERN = new String[] {
            "&&", "and", "or", "||"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String RELATIONAL_PATTERN = "\\b(" + String.join("|", RELATIONAL_OPERATION_PATTERN) + ")\\b";
    private static final String BITWISE_PATTERN = "\\b(" + String.join("|", BITWISE_OPERATION_PATTERN) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String NUMBER_PATTERN =  "\\d";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<RELATIONAL>" + RELATIONAL_PATTERN + ")"
                    + "|(?<BITWISE>" + BITWISE_PATTERN + ")"
    );

    public CodeEditor() {
        super();
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> {
                    this.setStyleSpans(0, computeHighlighting(this.getText()));
                });
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                        matcher.group("NUMBER") != null ? "number" :
                            matcher.group("PAREN") != null ? "paren" :
                                matcher.group("BRACE") != null ? "brace" :
                                    matcher.group("BRACKET") != null ? "bracket" :
                                        matcher.group("SEMICOLON") != null ? "semicolon" :
                                            matcher.group("STRING") != null ? "string" :
                                                matcher.group("COMMENT") != null ? "comment" :
                                                        matcher.group("RELATIONAL") != null ? "relational" :
                                                            matcher.group("BITWISE") != null ? "bitwise" :
                                                                null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
