package Engine;

public class Instruction {
    private String instructionType;
    private String additionalInfo;
    private boolean isFullyDefined = false;

    public Instruction(String instructionType) {
        this.instructionType = instructionType;
    }

    public Instruction(String instructionType, String additionalInfo) {
        this.instructionType = instructionType;
        this.additionalInfo = additionalInfo;
        this.isFullyDefined = true;
    }

    public boolean isFullyDefined() { return this.isFullyDefined; }
    public String getInstructionType() { return this.instructionType; }
    public String getAdditionalInfo() { return this.additionalInfo; }
    public void setAdditionalInfo(String newInfo) {
        this.additionalInfo = newInfo;
        this.isFullyDefined = true;
    }
}
