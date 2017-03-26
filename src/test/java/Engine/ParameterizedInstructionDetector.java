package Engine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParameterizedInstructionDetector {

    private String input;
    private String expectedOutput;

    public ParameterizedInstructionDetector(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    @Parameterized.Parameters
    public static Collection inputData() {
        return Arrays.asList( new Object[][] {
                {
                    "write a function called main():", "FUNCTION"
                },
                {
                    "please print \"hello world\" to the console", "PRINT"
                },
                {
                    "$s = 45", "ASSIGNMENT"
                },
                {
                    "create $x", "VARIABLE"
                },
                {
                    "write the result of 2+2 to the console", "PRINT"
                },
                {
                    "loop 5 times", "LOOP"
                },
                {
                    "fdkhgfd kdfjhrgkdkjdfg fjdgt", "UNKNOWN"
                },
                {
                    "", "UNKNOWN"
                },
                {
                    "print 45", "PRINT"
                },
                {
                    "write a program to loop 5 times", "LOOP"
                },
                {
                    "add 2 plus  2 && print the result", "PRINT"
                }
        });
    }

    @Test
    public void testParameterizedInputs() {
        InstructionDetector instructionDetector = new InstructionDetector(new InstructionSet());
        String actualOutput = instructionDetector.detectInstruction(input);

        assertEquals("Incorrect instruction detected", actualOutput, expectedOutput);
    }
}
