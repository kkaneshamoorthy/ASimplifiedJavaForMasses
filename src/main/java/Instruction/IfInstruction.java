package Instruction;

import java.util.ArrayList;

public class IfInstruction implements Instruction{
    private String instructionType;
    private boolean isFullyDefined = false;
    private BlockInstruction body;
    private String condition;
    private Variable conditionVariable;
    private ArrayList<Variable> conditionVar;
    private String id;

    public IfInstruction() {
        this.instructionType = "IF";
        this.id = generateId();
    }

    public IfInstruction setBody(BlockInstruction body) {
        this.body = body;

        return this;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    public IfInstruction setCondition(String condition) {
        this.condition = condition;
        this.conditionVariable = new Variable(condition, condition, this.generateId());

        return this;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setConditionVar(ArrayList<Variable> conditionVar) {
        this.conditionVar = conditionVar;
    }

    @Override
    public String getInstructionID() {
        return id;
    }

    @Override
    public boolean isFullyDefined() {
        return this.isFullyDefined;
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }

    public String formatExpression() {
        StringBuilder sb = new StringBuilder();
        boolean isString = false;

        for (Variable argument : this.conditionVar) {
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

        this.conditionVariable.setValue(sb.toString().replace("$", ""));

        //todo: string is returned
        if (isString) {
            this.conditionVariable.setType("String");
        } else {
            this.conditionVariable.setType("int");
            return sb.toString().replace("\"", "");
        }

        return sb.toString().replace("$", "");
    }

    @Override
    public String generateCode() {
        formatExpression();

        StringBuilder sb = new StringBuilder();
        sb.append("if ("+this.condition.replace("$", "")+") { \n");
        sb.append(body == null ? "" : this.body.generateCode());
        sb.append("} \n");

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    private String generateId() {
        return (this.getInstructionType()+this.getCondition()).hashCode()+"";
    }
}
