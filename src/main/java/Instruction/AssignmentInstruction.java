package Instruction;

import Memory.Variable;

public class AssignmentInstruction implements Instruction{
    private String instructionType;
    private String id;
    private String expression;
    private Variable assignedTo;
    private boolean isFullyDefined = false;


    public AssignmentInstruction(Variable assignedTo, String expression) {
        this.instructionType = "ASSIGNMENT";
        this.assignedTo = assignedTo;
        this.expression = expression;
        this.id = generateId();
    }

    public Variable getAssignedTo() {
        return this.assignedTo;
    }

    public String getExpression() {
        return this.expression;
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
        return (assignedTo.getType() + " " + assignedTo.getName().replace("$", "") + " = " + formatExpression(expression)+";");
    }

    private String formatExpression(String expression) {
        return expression.replace("ASSIGNMENT", "")
                .replace("INT =>", "")
                .replace("STRING =>", "")
                .replace("VARIABLE_NAME => ", "")
                .replace("UNKNOWN", "")
                .trim();
    }

    private String generateId() {
        return (this.getInstructionType()+this.getExpression()+this.getAssignedTo().getName()+this.getAssignedTo().getValue()).hashCode()+"";
    }
}
