package Engine;

import Utility.Helper;
import junit.framework.TestCase;

import java.util.ArrayList;

public class TestInstructionDetector extends TestCase{

    LexicalAnalyser lexicalAnalyser;

    public void setUp() {
        lexicalAnalyser = new LexicalAnalyser(new InstructionSet());
    }

    public void testGetVariableName() {
        Helper helper = new Helper();
        String statement = "$x = 3+4";
        String expectedVariableName = "$x";
        String actualVariableName = Helper
                .getVariableName(statement);

        assertEquals(expectedVariableName, actualVariableName);
    }

    public void testIsNumber() {
        String numStr = "123";
        boolean actualResult = Helper
                .isNumber(numStr);

        assertTrue(actualResult);
    }

    public void test_isNumber_with_invalidNumber() {
        String numStr = "d123as";
        boolean actualResult = Helper
                .isNumber(numStr);

        assertFalse(actualResult);
    }

    public void test_identifyToken_with_validNumToken() {
        char token = '1';
        String expectedResult = "1";
        String actualResult = this.lexicalAnalyser
                .identifyToken(token);

        assertEquals(expectedResult, actualResult);
    }

    public void test_identifyToken_with_validToken() {
        char token = '$';
        String expectedResult = "$";
        String actualResult = this.lexicalAnalyser
                .identifyToken(token);

        assertEquals(expectedResult, actualResult);
    }

    public void test_identifyToken_validVariableName() {
        String token = "$x";
        ArrayList<String> expectedResult = new ArrayList<>();
        expectedResult.add("VARIABLE_NAME =>$x");
        expectedResult.add("");
        ArrayList<String> actualResult = this.lexicalAnalyser
                .annotateToken(token);

        assertEquals(expectedResult, actualResult);
    }

    public void test_identifyToken_with_arrayOfToken() {

    }

    public void test_instructionDetection() {

    }
}
