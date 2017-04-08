package Memory;

import Instruction.Instruction;

import java.util.HashMap;

public class JavaProgramTemplate {
    private String className;
    private VariableHolder variableHolder;
    private HashMap<Integer, Instruction> instructionHashMap;

    public JavaProgramTemplate(HashMap<Integer, Instruction> instructionHashMap, VariableHolder variableHolder) {
        this.variableHolder = variableHolder;
        this.instructionHashMap = instructionHashMap;
        this.className = "temp";
    }

    public String getClassName() { return this.className; }
    public VariableHolder getVariableHolder() { return this.variableHolder; }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        HashMap<Integer, String> javaCode = this.codeGeneration(this.instructionHashMap);

        sb.append("public class " + className + " { ");
        for (Integer instructionCounter : javaCode.keySet())
            sb.append(javaCode.get(instructionCounter));
        sb.append("}");

        return sb.toString();
    }

    private HashMap<Integer, String> codeGeneration(HashMap<Integer, Instruction> tokenisedInstruction) {
        HashMap<Integer, String> javaCode = new HashMap<>();

        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            Instruction instruction = tokenisedInstruction.get(instructionCounter);
            javaCode.put(instructionCounter, instruction.generateCode());
        }

        return javaCode;
    }
}
