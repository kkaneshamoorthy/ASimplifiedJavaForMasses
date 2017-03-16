package Engine;

import Instruction.Instruction;
import Instruction.BlockInstruction;
import Instruction.FunctionInstruction;
import Instruction.PrintInstruction;
import Instruction.Variable;
import junit.framework.TestCase;

import java.util.HashMap;

public class TestLexicalAnalyser extends TestCase {

    LexicalAnalyser lexicalAnalyser;

    public void setUp() {
        lexicalAnalyser = new LexicalAnalyser();
    }

    public void test_printInstruction() {
        String input = "function main(): \n     print 4";
        HashMap<Integer, Instruction> instructionHashMap = lexicalAnalyser.lexicalAnalyser(input.split("\\n"));

        HashMap<Integer, Instruction> expectedInstructionHashMap = new HashMap<Integer, Instruction>();

        FunctionInstruction mainFunc = new FunctionInstruction("main");

        BlockInstruction body = new BlockInstruction();
        PrintInstruction printInstr = new PrintInstruction();
        printInstr.setData(new Variable("", "4", mainFunc.getInstructionID()));
        body.addInstructionToBlock(printInstr);
        mainFunc.setBody(body);
    }
}