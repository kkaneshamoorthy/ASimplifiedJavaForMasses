package Instruction;

import Memory.Variable;

public class InputInstruction implements Instruction{
    private String instructionType;
    private String id;
    private boolean isFullyDefined = false;
    private String inputLocation;
    private Variable assignedTo;
    private boolean isDeclaration = false;

    public InputInstruction() {
        this.instructionType = "INPUT";
        this.assignedTo = new Variable("userInput", "", "GLOBAL");
        this.id = generateId();
    }

    public InputInstruction setInputLocation(String inputLocation) {
        this.inputLocation = inputLocation;

        return this;
    }

    public InputInstruction setAssignedTo(Variable assignedTo) {
        this.assignedTo = assignedTo;

        return this;
    }

    public Variable getAssignedTo() {
        return this.assignedTo;
    }

    public String getInputLocation() {
        return this.inputLocation;
    }

    public void setVariableDeclaration(boolean isDeclaration) {
        this.isDeclaration = isDeclaration;
    }

    @Override
    public String getInstructionID() {
        return this.id;
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }

    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        String type = isDeclaration ? "String " : "";
        System.out.println(this.assignedTo.getValue());
        sb.append(type + this.assignedTo.getName().replace("$", "") + " = JOptionPane.showInputDialog(null, \"Enter assignedTo:\");");

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    private String generateId() {
        return (this.instructionType+this.getInputLocation()+this.getAssignedTo().getName()+this.getAssignedTo().getValue()).hashCode()+"";
    }
}
