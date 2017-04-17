package Instruction;

import Engine.InstructionDetector;
import Engine.InstructionSet;
import Utility.Helper;

import java.util.ArrayList;

public class FunctionInstruction implements Instruction{
    private String instructionType;
    private String functionName;
    private boolean isFullyDefined = false;
    private ArrayList<Variable> parameter;
    private BlockInstruction body;

    public FunctionInstruction(String functionName) {
        this.instructionType = "FUNCTION";
        this.functionName = functionName;
    }

    public FunctionInstruction setBody(BlockInstruction newBody) {
        this.body = newBody;

        return this;
    }

    public void setParameter(ArrayList<Variable> parameter) {
        this.parameter = parameter;
    }

    public ArrayList<Variable> getParameter() {
        return this.parameter;
    }

    public BlockInstruction getBody() {
        return this.body;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String getInstructionID() {
        return functionName;
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
        StringBuilder sb = new StringBuilder();
        StringBuilder parameterList = new StringBuilder();

        for (int i=0; i<this.parameter.size(); i++) {
            Variable parameter = this.parameter.get(i);
            parameterList.append(getType(parameter.getValue()) + " " + parameter.getName().replace("$", ""));
            if (i != this.parameter.size()-1) parameterList.append(", ");
        }

        String parameter = (this.functionName.equalsIgnoreCase("main") ? "String[] args" : parameterList.toString());
        sb.append("public static void " +  this.functionName + "("+parameter+") {");
        sb.append(this.body == null ? "" : this.body.generateCode());
        sb.append("}  \n");

        return sb.toString();
    }

    public String getType(String value) {
        if (Helper.isNumber(value)) return "int";
        if (Helper.isString(value)) return "String";
        if (Helper.isBoolean(value)) return "boolean";

        return "String";
    }

    public static void main(String[] args) {
        FunctionInstruction functionInstruction = new FunctionInstruction("Test");
        System.out.println(functionInstruction.generateCode());
    }

    @Override
    public void setId(String id) {
        this.functionName = id;
    }
}
