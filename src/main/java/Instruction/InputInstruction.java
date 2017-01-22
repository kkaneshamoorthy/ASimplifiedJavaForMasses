package Instruction;

import Memory.Variable;

public class InputInstruction implements Instruction{
    private String instructionType;
    private boolean isFullyDefined = false;
    private String inputLocation;
    private Variable data;

    public InputInstruction() {
        this.instructionType = "INPUT";
    }

    public InputInstruction setInputLocation(String inputLocation) {
        this.inputLocation = inputLocation;

        return this;
    }

    public InputInstruction setData(Variable data) {
        this.data = data;

        return this;
    }

    public Variable getData() {
        return this.data;
    }

    public String getInputLocation() {
        return this.inputLocation;
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
