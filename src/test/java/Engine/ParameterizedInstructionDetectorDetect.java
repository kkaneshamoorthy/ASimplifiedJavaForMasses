package Engine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
/**
 * Created by kowrishankar on 26/03/17.
 */
@RunWith(Parameterized.class)
public class ParameterizedInstructionDetectorDetect {
    private String input;
    private String expectedResult;
    private static InstructionDetector instructionDetector = new InstructionDetector(new InstructionSet());

    public ParameterizedInstructionDetectorDetect(String input, String expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection inputData() {
        return Arrays.asList(new String[][] {
                {
                        "", "UNKNOWN"
                },
                {
                        "please print \"Hello World!\"", "PRINT"+"\"Hello World!\""
                },
                {
                        "print 5", "PRINT5"
                },
                {
                        "create $x", "VARIABLE$x"
                },
                {
                        "if 2==2", "IF2==2"
                },
                {
                        "write what 2+2 to the console", "PRINT2+2"
                },
                {
                        "print 2+2+3", "PRINT2+2+3"
                },
                {
                        "$x = 5+4", "ASSIGNMENT$x=5+4"
                },
                {
                        "$x = 45", "ASSIGNMENT$x=45"
                },
                {
                        "if 4==4", "IF4==4"
                },
                {
                    "$x = 4/4", "ASSIGNMENT$x=4/4"
                },
                {
                        "$x = 4*4", "ASSIGNMENT$x=4*4"
                },
                {
                        "$x = 4-4", "ASSIGNMENT$x=4-4"
                },
                {
                        "$x = 4%4", "ASSIGNMENT$x=4%4"
                }
        });
    }

    @Test
    public void test_detect_with_parameters() {
        HashMap<Integer, Pair> actualRes = instructionDetector.detect(new String[] {this.input});
        String actualResult = actualRes.get(0).getKey()+actualRes.get(0).getValue();
        assertEquals("", expectedResult, actualResult);

    }
}
