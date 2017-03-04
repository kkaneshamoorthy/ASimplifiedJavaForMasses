package Instruction;

import Memory.Variable;

import java.util.ArrayList;

public class FunctionDispatchInstruction implements Instruction{
    private String instructionType;
    private String functionName;
    private boolean isFullyDefined = false;
    private ArrayList<Variable> arguments;

    public FunctionDispatchInstruction(String functionName) {
        this.instructionType = "FUNCTION DISPATCH";
        this.functionName = functionName;
        arguments = new ArrayList<>();
    }

    public void addArgument(Variable agrs) {
        this.arguments.add(agrs);
    }

    @Override
    public String getInstructionID() {
        return functionName+"_dispatch";
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
        sb.append(functionName + "();");

        return sb.toString();
    }
}
