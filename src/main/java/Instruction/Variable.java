package Instruction;

import Engine.InstructionDetector;
import Engine.InstructionSet;
import Utility.Helper;

public class    Variable {
    private String name;
    private String value;
    private String scope;
    private String type;

    public Variable(String name, String value, String scope) {
        this.name = name;
        this.value = value;
        this.scope = scope;
    }

    public String getName() { return this.name; }
    public String getValue() { return this.value; }
    public String getScope() { return this.scope; }
    public void setValue(String newValue) { this.value = newValue; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
    public String getExprType() {
        InstructionDetector instructionDetector = new InstructionDetector(new InstructionSet());
        //TODO: if there is variable check its type
        if (Helper.isNumber(this.value)) return "int";
        if (Helper.isString(this.value)) return "String";
        if (Helper.isBoolean(this.value)) return "boolean";

        return "String";
    }
}
