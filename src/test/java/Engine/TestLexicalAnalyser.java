package Engine;

import Instruction.Instruction;
import Instruction.PrintInstruction;
import Instruction.LoopInstruction;
import Memory.Variable;
import junit.framework.TestCase;

import java.util.HashMap;

public class TestLexicalAnalyser extends TestCase {

    LexicalAnalyser lexicalAnalyser;

    public void setUp() {
        lexicalAnalyser = new LexicalAnalyser();
    }

    public void test_validToken() {
        HashMap<Integer, Instruction> expectedToken = new HashMap<Integer, Instruction>();
        expectedToken.put(0, new PrintInstruction().setData(new Variable("var", "\"Hello World!\"", "GLOBAL")));

        LoopInstruction loopInstruction = new LoopInstruction();
        loopInstruction.setNumOfIteration(5)
                .setIteration(new Variable("i", "0", "GLOBAL"))
                .setBody(new BlockInstruction(new PrintInstruction().setData(new Variable("num", "1", "GLOBAL"))));

        expectedToken.put(1, loopInstruction);

        String[] statements = new String[3];
        statements[0] = "print \"Hello World!\" to the console";
        statements[1] = "write a loop to go through 5 times";
        statements[2] = "\t print 1";

        HashMap<Integer, Instruction> token = this.lexicalAnalyser.lexicalAnalyser(statements);

        if (token.get(1) instanceof LoopInstruction) {
            LoopInstruction instruction = (LoopInstruction) token.get(1);
            assertEquals(loopInstruction.getBody().generateCode(), instruction.getBody().generateCode());
        }

        assertEquals(expectedToken.get(0).generateCode(), token.get(0).generateCode());
    }
}