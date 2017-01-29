package Instruction;

import Memory.Variable;

public class AssignmentInstruction implements Instruction{
    private String instructionType;
    private Variable assignedTo;
    private String expression;
    private boolean isFullyDefined = false;

    public AssignmentInstruction(Variable assignedTo, String expression) {
        this.instructionType = "ASSIGNMENT";
        this.assignedTo = assignedTo;
        this.expression = expression;
    }

    public Variable getAssignedTo() {
        return this.assignedTo;
    }

    public String getExpression() {
        return this.expression;
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
        return (assignedTo.getName() + " = " + formatExpression(expression)+";");
    }

    private String formatExpression(String expression) {
        return expression.replace("ASSIGNMENT", "")
                .replace("INT =>", "")
                .replace("STRING =>", "")
                .replace("VARIABLE_NAME => ", "")
                .replace("UNKNOWN", "")
                .trim();
    }
}
