package Instruction;

import Memory.Variable;

public class PrintInstruction implements Instruction {
    private String instructionType;
    private boolean isFullyDefined = false;
    private Variable data;

    public PrintInstruction() {
        this.instructionType = "PRINT";
        this.data = new Variable("UNKNOWN", "", "GLOBAL");
    }

    public PrintInstruction setData(Variable data) {
        this.data = data;

        return this;
    }

    public Variable getData() {
        return this.data;
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
        StringBuilder sb = new StringBuilder();
        System.out.println(this.data.getName() + " " + this.data.getValue());
        sb.append("System.out.println("+this.data.getValue()+");");

        return sb.toString();
    }
}
