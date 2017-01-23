package Instruction;

import Engine.BlockInstruction;

public class IfInstruction implements Instruction{
    private String instructionType;
    private boolean isFullyDefined = false;
    private BlockInstruction body;
    private String condition;

    public IfInstruction() {
        this.instructionType = "IF";
    }

    public IfInstruction setBody(BlockInstruction body) {
        this.body = body;

        return this;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    public IfInstruction setCondition(String condition) {
        this.condition = condition;

        return this;
    }

    public String getCondition() {
        return this.condition;
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

        sb.append("if ("+condition+") { \n");
        sb.append(body == null ? "" : this.body.generateCode());
        sb.append("} \n");

        return sb.toString();
    }
}
