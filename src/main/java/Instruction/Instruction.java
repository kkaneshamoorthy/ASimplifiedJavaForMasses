package Instruction;

public interface Instruction {
    public String getInstructionID();
    public boolean isFullyDefined();
    public String getInstructionType();
    public String generateCode();
    public void setId(String id);
}
