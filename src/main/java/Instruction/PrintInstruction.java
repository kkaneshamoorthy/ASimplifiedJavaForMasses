package Instruction;

import Memory.Variable;

public class PrintInstruction implements Instruction {
    private String instructionType;
    private boolean isFullyDefined = false;
    private Variable data;

    public PrintInstruction() {
        this.instructionType = "PRINT";
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
}
