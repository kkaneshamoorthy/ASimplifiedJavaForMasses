package Instruction;

import Memory.BlockInstruction;
import Memory.Variable;
import Utility.Helper;

import java.math.BigInteger;
import java.security.SecureRandom;

public class LoopInstruction implements Instruction {

    private String instructionType;
    private String id;
    public String variableID;
    private Variable numOfIteration;
    private Variable currentValue;
    private Variable iterateBy;
    private BlockInstruction body;
    private SecureRandom random = new SecureRandom();

    public LoopInstruction() {
        this.instructionType = "LOOP";
        this.variableID = generateVariableId();
        this.currentValue = new Variable(variableID, "0", "GLOBAL");
        this.id = generateId();
        this.iterateBy = new Variable(generateVariableId(), "1", getInstructionID());
    }

    public LoopInstruction setNumOfIteration(Variable numOfIteration) {
        this.numOfIteration = numOfIteration;

        return this;
    }

    public LoopInstruction setBody(BlockInstruction body) {
        this.body = body;

        return this;
    }

    public void setIterateBy(String interateBy) {
        if (Helper.isNumber(interateBy))
            this.iterateBy.setValue(interateBy+"");
    }

    public void setId(String id) {
        this.id = id;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    public Variable getTotalIteration() {
        return this.numOfIteration;
    }

    public String getIterations() {

        String iterations = this.getTotalIteration().getValue();

        if (iterations.contains("$")) {
            return iterations.replace("$", "");
        } else if (isNumber(iterations)) {
            return iterations;
        } else {
            //TODO: error
        }

        return iterations;
    }

    public boolean isNumber(String identifiedToken) {
        if (identifiedToken.startsWith("INT =>")) return true;
        try {
            Integer.parseInt(identifiedToken);
        } catch (NumberFormatException e) { return false; }

        return true;
    }

    public Variable getCurrentValue() { return this.currentValue; }

    @Override
    public String getInstructionID() {
        return this.id;
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }

    @Override
    public String generateCode() {

        System.out.println(this.body);

        StringBuilder sb = new StringBuilder();
        sb.append("autoLoopIterator = Integer.parseInt("+this.getIterations()+"+\"\");");
        sb.append("for (int " + this.currentValue.getName() + " = 0; " + this.currentValue.getName() + "< autoLoopIterator; " + this.currentValue.getName() + "+="+ this.iterateBy.getValue()+") \n");
        sb.append("{ \n");
        sb.append(this.body == null ? "" : this.body.generateCode());
        sb.append("} \n");

        return sb.toString();
    }

    private String generateId() {
        return (this.instructionType+this.getTotalIteration()+this.getCurrentValue().getName()+this.getCurrentValue().getValue()).hashCode()+"";
    }

    private String generateVariableId() {
        return (this.instructionType+ new BigInteger(32, random).toString(32));
    }
}
