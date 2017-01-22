package Engine;

import junit.framework.TestCase;

public class TestInstructionSet extends TestCase{

    private InstructionSet instructionSet;

    public void setUp() {
        this.instructionSet = new InstructionSet();
    }

    public void testComputeRegex_if() {
        String statement = "IF";
        String expectedResult = "IF";
        String actualResult = this.instructionSet.computeRegex(statement);

        assertEquals(expectedResult, actualResult);
    }

    public void testComputeRegex_num() {
        String statement = "34";
        String expectedResult = "34";
        String actualResult = this.instructionSet.computeRegex(statement);

        assertEquals(expectedResult, actualResult);
    }
}
