package Instruction;

import java.util.ArrayList;

public class FunctionDispatchInstruction implements Instruction{
    private String instructionType;
    private String functionName;
    private boolean isFullyDefined = false;
    private String id;
    private ArrayList<Variable> arguments;

    public FunctionDispatchInstruction(String functionName) {
        this.instructionType = "FUNCTION DISPATCH";
        this.functionName = functionName;
        arguments = new ArrayList<>();
        this.id = functionName+"_dispatch";
    }

    public void addArgument(Variable agrs) {
        this.arguments.add(agrs);
    }

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
        StringBuilder sb = new StringBuilder();
        sb.append(functionName + "();");

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
