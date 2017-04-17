package Instruction;

import Engine.InstructionDetector;
import Engine.InstructionSet;

import java.util.ArrayList;

public class FunctionDispatchInstruction implements Instruction{
    private String instructionType;
    private String functionName;
    private boolean isFullyDefined = false;
    private String id;
    private ArrayList<Variable> arguments;

    public FunctionDispatchInstruction(String functionName) {
        this.instructionType = "FUNCTION DISPATCH";
        this.functionName = functionName;
        arguments = new ArrayList<>();
        this.id = functionName+"_dispatch";
    }

    public void addArgument(Variable args) {
        this.arguments.add(args);
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
        StringBuilder sb = new StringBuilder();

        StringBuilder argument = new StringBuilder();
        for (int i=0; i<this.arguments.size(); i++) {
            Variable arg = this.arguments.get(i);
            String value = arg.getValue();
            argument.append(getType(value).equals("String") ? value : "\""+value+"\"" );//type conversion -> changes any data to string - which is default
            if (i != this.arguments.size()-1) argument.append(", ");
        }

        sb.append(functionName + "("+argument.toString()+");");

        return sb.toString();
    }

    public String getType(String value) {
        InstructionDetector instructionDetector = new InstructionDetector(new InstructionSet());

        return instructionDetector.getType(value);
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
