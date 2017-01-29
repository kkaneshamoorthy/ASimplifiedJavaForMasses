package Instruction;

import Engine.*;
import Memory.Variable;
import junit.framework.TestCase;

public class TestLoopInstruction extends TestCase {

    LoopInstruction loopInstruction;

    public void setUp() {
        this.loopInstruction = new LoopInstruction();
    }

    public void test_ifInstruction_with_valid() {
        BlockInstruction blockInstruction = new BlockInstruction();

        BlockInstruction blockInstructionForPrint = new BlockInstruction();
        PrintInstruction printInstruction = new PrintInstruction();
        printInstruction.setData(new Variable("i", "Hello world!", "GLOBAL"));
        blockInstructionForPrint.addInstructionToBlock(printInstruction);


        IfInstruction ifInstruction = new IfInstruction();
        ifInstruction.setCondition("i==1");
        ifInstruction.setBody(blockInstructionForPrint);

        blockInstruction.addInstructionToBlock(ifInstruction);

        this.loopInstruction.setNumOfIteration(3);
        this.loopInstruction.setIteration(new Variable("i", "3", "GLOBAL"));
        this.loopInstruction.setBody(blockInstruction);

        System.out.println(this.loopInstruction.generateCode());
    }

    public void test_loopInstruction_with_valid() {
        BlockInstruction blockInstructionForPrint = new BlockInstruction();
        PrintInstruction printInstruction = new PrintInstruction();
        printInstruction.setData(new Variable("i", "Hello world!", "GLOBAL"));
        blockInstructionForPrint.addInstructionToBlock(printInstruction);

        this.loopInstruction.setNumOfIteration(3);
        this.loopInstruction.setIteration(new Variable("i", "3", "GLOBAL"));
        this.loopInstruction.setBody(blockInstructionForPrint);

        System.out.println(this.loopInstruction.generateCode());
    }

}
