package Engine;

import javafx.scene.control.TextArea;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParameterizedProgram {
    private String input;
    private String expectedOutput;
    private CodeExecution codeExecution;

    public ParameterizedProgram(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.codeExecution = new CodeExecution(new TextArea());
    }

    @Parameterized.Parameters
    public static Collection inputData() {
        return Arrays.asList(new String[][] {
                {
                    "function main(): ", "Hello world!"
                }
        });
    }


    @Test
    public void testParameterInputs() {

    }

}
