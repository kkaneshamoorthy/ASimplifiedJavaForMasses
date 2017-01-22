package Engine;

import junit.framework.TestCase;

public class TestEngine extends TestCase {

    LexerAnalyser lexerAnalyser;

    protected void setUp() {
        lexerAnalyser = new LexerAnalyser();
    }

    public void testRetrivalOfStringBetweenSpeechMarks() {
        String str = "\"hello world!\"";
        String expectedResult = "hello world!";
        String actualResult = lexerAnalyser.getStringBetweenSpeechMarks(str);

        assertEquals(expectedResult, actualResult);
    }

}
