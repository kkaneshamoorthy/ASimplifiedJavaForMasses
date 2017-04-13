package Instruction;

import Engine.InstructionDetector;
import Engine.InstructionSet;

import java.util.ArrayList;

public class AssignmentInstruction implements Instruction{
    private String instructionType;
    private String id;
    private String expression;
    private ArrayList<Variable> expressionLs;
    private Variable assignedTo;
    private boolean isDeclaration;
    private boolean isFullyDefined = false;

    public AssignmentInstruction(Variable assignedTo, String expression) {
        this.instructionType = "ASSIGNMENT";
        this.assignedTo = assignedTo;
        this.expression = expression;
        this.id = ""; //generateId();
        this.isDeclaration = true;
        expressionLs = new ArrayList<Variable>();
    }

    public void setExpression(ArrayList<Variable> expressionLs) {
        this.expressionLs = expressionLs;
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
        System.out.println("RDKGHDFRGH"+formatExpression() + " == " + assignedTo.getName() );
        return (
                ((this.isDeclaration) ? assignedTo.getType()+" " : "")  + assignedTo.getName().replace("$", "") + " = " + formatExpression()+";"
        );
    }

    public String formatExpression() {
        StringBuilder sb = new StringBuilder();
        boolean isString = false;

        for (Variable argument : this.expressionLs) {
            if (argument.getScope().equals("OPERATION")) {
                sb.append(argument.getValue());
            } else {
                if (argument.getExprType().equals("String")) {
                    isString = true;
                }

                if (argument.getScope().equals("NONE")) {
                    sb.append(argument.getValue());
                } else {
                    sb.append(argument.getName());
                }
            }
        }

        if (isString) {
            this.assignedTo.setType("String");
        } else {
            this.assignedTo.setType("int");
        }

        System.out.println(sb.toString() + " " + isString);

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public static void main(String[] args) {
        AssignmentInstruction assignmentInstruction = new AssignmentInstruction(new Variable("", "", ""), "");
        System.out.println(assignmentInstruction.generateCode());
    }

    private String generateId() {
        return (this.getInstructionType()+this.getExpression()+this.getAssignedTo().getName()+this.getAssignedTo().getValue()).hashCode()+"";
    }
}
