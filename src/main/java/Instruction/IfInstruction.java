package Instruction;

import Engine.BlockInstruction;

public class IfInstruction implements Instruction{
    private String instructionType;
    private boolean isFullyDefined = false;
    private BlockInstruction body;
    private String condition;
    private String id;

    public IfInstruction() {
        this.instructionType = "IF";
        this.id = generateId();
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
    public String getInstructionID() {
        return id;
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

    private String generateId() {
        return (this.getInstructionType()+this.getCondition()).hashCode()+"";
    }
}
