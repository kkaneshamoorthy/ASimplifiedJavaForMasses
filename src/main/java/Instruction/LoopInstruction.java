package Instruction;

import Engine.BlockInstruction;
import Memory.Variable;

public class LoopInstruction implements Instruction {

    private String instructionType;
    private boolean isFullyDefined = false;
    private int numOfIteration;
    private Variable iteration;
    private BlockInstruction body;

    public LoopInstruction() {
        this.instructionType = "LOOP";
    }

    public LoopInstruction setIteration(Variable iteration) {
        this.iteration = iteration;

        return this;
    }

    public LoopInstruction setNumOfIteration(int numOfIteration) {
        this.numOfIteration = numOfIteration;

        return this;
    }

    public LoopInstruction setBody(BlockInstruction body) {
        this.body = body;

        return this;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    public int getCurrentIterationNum() {
        return Integer.parseInt(this.iteration.getValue());
    }

    public int getTotalIteration() {
        return this.numOfIteration;
    }

    @Override
    public boolean isFullyDefined() {
        return this.isFullyDefined;
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }
}
