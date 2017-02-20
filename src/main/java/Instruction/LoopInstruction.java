package Instruction;

import Engine.BlockInstruction;
import Memory.Variable;

import java.math.BigInteger;
import java.security.SecureRandom;

public class LoopInstruction implements Instruction {

    private String instructionType;
    private String id;
    public String variableID;
    private boolean isFullyDefined = false;
    private int numOfIteration;
    private Variable iteration;
    private BlockInstruction body;
    private SecureRandom random = new SecureRandom();

    public LoopInstruction() {
        this.instructionType = "LOOP";
        this.variableID = generateVariableId();
        this.iteration = new Variable(variableID, "0", "GLOBAL");
        this.id = generateId();
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
    public String getInstructionID() {
        return this.id;
    }

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

    private String generateId() {
        return (this.instructionType+this.getTotalIteration()+this.getIteration().getName()+this.getIteration().getValue()).hashCode()+"";
    }

    private String generateVariableId() {
        return (this.instructionType+ new BigInteger(32, random).toString(32));
    }
}
