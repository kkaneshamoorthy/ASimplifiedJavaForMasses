package Memory;

import Engine.InstructionDetector;
import Engine.InstructionSet;

public class Variable {
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
    public String getType() {
        InstructionDetector instructionDetector = new InstructionDetector(new InstructionSet());

        if (instructionDetector.isNumber(this.value)) return "int";
        if (instructionDetector.isString(this.value)) return "String";
        if (instructionDetector.isBoolean(this.value)) return "boolean";

        return "int";
    }
}
