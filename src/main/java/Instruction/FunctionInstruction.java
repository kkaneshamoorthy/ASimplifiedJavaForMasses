package Instruction;

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

        String parameter = (this.functionName.equalsIgnoreCase("main") ? "String[] args" : "");

        sb.append("public static void " +  this.functionName + "("+parameter+") {");
        sb.append(this.body == null ? "" : this.body.generateCode());
        sb.append("}  \n");

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.functionName = id;
    }
}
