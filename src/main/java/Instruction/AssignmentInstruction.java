package Instruction;

public class AssignmentInstruction implements Instruction{
    private String instructionType;
    private String id;
    private String expression;
    private Variable assignedTo;
    private boolean isDeclaration;
    private boolean isFullyDefined = false;

    public AssignmentInstruction(Variable assignedTo, String expression) {
        this.instructionType = "ASSIGNMENT";
        this.assignedTo = assignedTo;
        this.expression = expression;
        this.id = generateId();
        this.isDeclaration = true;
    }

    public Variable getAssignedTo() {
        return this.assignedTo;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setDeclaration(boolean isDeclared) {
        this.isDeclaration = isDeclared;
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
        return (
                ((this.isDeclaration) ? assignedTo.getType()+" " : "")  + assignedTo.getName().replace("$", "") + " = " + formatExpression(expression)+";"
        );
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    private String formatExpression(String expression) {
        System.out.println(expression+" called");
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
