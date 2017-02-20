package Instruction;

import Engine.BlockInstruction;

public class FunctionInstruction implements Instruction{
    private String instructionType;
    private String functionName;
    private boolean isFullyDefined = false;
    private BlockInstruction body;

    public FunctionInstruction(String functionName) {
        this.instructionType = "FUNCTION";
        this.functionName = functionName;
    }

    public FunctionInstruction setBody(BlockInstruction newBody) {
        this.body = newBody;

        return this;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    @Override
    public String getInstructionID() {
        return functionName;
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

        sb.append(this.functionName.equalsIgnoreCase("main") ? "public static void main(String[] args) {  \n" : "public static void " + this.functionName + "() { \n");
        sb.append(this.body == null ? "" : this.body.generateCode());
        sb.append(this.functionName.equalsIgnoreCase("main") ? "}" : "}  \n");

        return sb.toString();
    }
}
