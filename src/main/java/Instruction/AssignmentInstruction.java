package Instruction;

import Memory.Variable;

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
    public String getInstructionType() {
        return this.instructionType;
    }

    @Override
    public String generateCode() {
        String value = this.formatExpression();
        return (
                ((this.isDeclaration) ? assignedTo.getType()+" " : "")  + assignedTo.getName().replace("$", "") + " = " + value+";"
        );
    }

    public String formatExpression() {
        StringBuilder sb = new StringBuilder();
        boolean isString = false;

        for (Variable argument : this.expressionLs) {
            String variableName = argument.getName().replace("$", "");
            String variableValue = argument.getValue().replace("$", "");

            if (argument.getScope().equals("OPERATION")) {
                sb.append(variableValue);
            } else {
                if (argument.getExprType().equals("String")) {
                    isString = true;
                }

                if (argument.getScope().equals("NONE")) {
                    sb.append(variableValue);
                } else {
                    sb.append(variableName);
                }
            }

        }

        this.assignedTo.setValue(sb.toString().replace("$", ""));

        //todo: string is returned
        if (isString) {
            this.assignedTo.setType("String");
        } else {
            this.assignedTo.setType("int");
            return sb.toString().replace("\"", "");
        }

        return sb.toString().replace("$", "");
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
