package Instruction;

public class ElseInstruction implements Instruction {
    private String instructionType;
    private BlockInstruction body;
    private String id;

    public ElseInstruction() {
        this.instructionType = "ELSE";
        this.body = new BlockInstruction();
        this.id = generateId();
    }

    public void setBody(BlockInstruction body) {
        this.body = body;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    @Override
    public String getInstructionID() {
        return this.id;
    }

    @Override
    public boolean isFullyDefined() {
        return false;
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }

    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();

        sb.append("else { \n");
        sb.append(body == null ? "" : this.body.generateCode());
        sb.append("} \n");

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    private String generateId() {
        return (this.getInstructionType()+""+this.body.hashCode());
    }

}
