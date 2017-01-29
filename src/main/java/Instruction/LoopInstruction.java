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
        this.iteration = new Variable("i", "0", "GLOBAL");
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

    public Variable getIteration() { return this.iteration; }

    @Override
    public boolean isFullyDefined() {
        return this.isFullyDefined;
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }

    @Override
    public String generateCode() {

        System.out.println(this.body);

        StringBuilder sb = new StringBuilder();
        sb.append("for (int " + this.iteration.getName() + " = 0; " + this.iteration.getName() + "<" + numOfIteration + "; " + this.iteration.getName() + "++) \n");
        sb.append("{ \n");
        sb.append(this.body == null ? "" : this.body.generateCode());
        sb.append("} \n");

        return sb.toString();
    }
}
