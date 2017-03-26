package Engine;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestInstructionDetector extends TestCase{

    InstructionDetector instructionDetector;

    public void setUp() {
        instructionDetector = new InstructionDetector(new InstructionSet());
    }

    public void testGetVariableName() {
        String statement = "$x = 3+4";
        String expectedVariableName = "$x";
        String actualVariableName = this.instructionDetector
                .getVariableName(statement);

        assertEquals(expectedVariableName, actualVariableName);
    }

    public void testIsNumber() {
        String numStr = "123";
        boolean actualResult = this.instructionDetector
                .isNumber(numStr);

        assertTrue(actualResult);
    }

    public void test_isNumber_with_invalidNumber() {
        String numStr = "d123as";
        boolean actualResult = this.instructionDetector
                .isNumber(numStr);

        assertFalse(actualResult);
    }

    public void test_identifyToken_with_validNumToken() {
        char token = '1';
        String expectedResult = "1";
        String actualResult = this.instructionDetector
                .identifyToken(token);

        assertEquals(expectedResult, actualResult);
    }

    public void test_identifyToken_with_validToken() {
        char token = '$';
        String expectedResult = "$";
        String actualResult = this.instructionDetector
                .identifyToken(token);

        assertEquals(expectedResult, actualResult);
    }

    public void test_identifyToken_validVariableName() {
        String token = "$x";
        ArrayList<String> expectedResult = new ArrayList<>();
        expectedResult.add("VARIABLE_NAME =>$x");
        expectedResult.add("");
        ArrayList<String> actualResult = this.instructionDetector
                .identifyTokens(token);

        assertEquals(expectedResult, actualResult);
    }

    public void test_identifyToken_with_arrayOfToken() {

    }

    public void test_instructionDetection() {

    }
}
