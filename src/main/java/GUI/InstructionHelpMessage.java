package GUI;

public class InstructionHelpMessage {
    private String instruction;
    private String description;
    private String example;

    public InstructionHelpMessage(String instruction, String description, String example) {
        this.instruction = instruction;
        this.description = description;
        this.example = example;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
