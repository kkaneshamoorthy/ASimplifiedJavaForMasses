package Instruction;

import java.math.BigInteger;
import java.security.SecureRandom;

public class LoopInstruction implements Instruction {

    private String instructionType;
    private String id;
    public String variableID;
    private boolean isFullyDefined = false;
    private Variable numOfIteration;
    private Variable currentIterationValue;
    private BlockInstruction body;
    private SecureRandom random = new SecureRandom();

    public LoopInstruction() {
        this.instructionType = "LOOP";
        this.variableID = generateVariableId();
        this.currentIterationValue = new Variable(variableID, "0", "GLOBAL");
        this.id = generateId();
    }

    public LoopInstruction setCurrentIterationValue(Variable currentIterationValue) {
        this.currentIterationValue = currentIterationValue;

        return this;
    }

    public LoopInstruction setNumOfIteration(Variable numOfIteration) {
        this.numOfIteration = numOfIteration;

        return this;
    }

    public LoopInstruction setBody(BlockInstruction body) {
        this.body = body;

        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    public int getCurrentIterationNum() {
        return Integer.parseInt(this.currentIterationValue.getValue());
    }

    public Variable getTotalIteration() {
        return this.numOfIteration;
    }

    public Variable getCurrentIterationValue() { return this.currentIterationValue; }

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
        sb.append("for (int " + this.currentIterationValue.getName() + " = 0; " + this.currentIterationValue.getName() + "<" + numOfIteration.getValue() + "; " + this.currentIterationValue.getName() + "++) \n");
        sb.append("{ \n");
        sb.append(this.body == null ? "" : this.body.generateCode());
        sb.append("} \n");

        return sb.toString();
    }

    private String generateId() {
        return (this.instructionType+this.getTotalIteration()+this.getCurrentIterationValue().getName()+this.getCurrentIterationValue().getValue()).hashCode()+"";
    }

    private String generateVariableId() {
        return (this.instructionType+ new BigInteger(32, random).toString(32));
    }
}
