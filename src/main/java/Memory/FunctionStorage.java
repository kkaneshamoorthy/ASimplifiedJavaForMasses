package Memory;

import Instruction.FunctionInstruction;

import java.util.HashMap;

public class FunctionStorage {
    private HashMap<String, FunctionInstruction> functionMap;

    public FunctionStorage() {
        this.functionMap = new HashMap<>();
    }

    //TODO: throw exception if functionName already exists
    public void add(FunctionInstruction functionInstruction) {
        this.functionMap.put(functionInstruction.getFunctionName(), functionInstruction);
    }

    public FunctionInstruction get(String functionName) {
        return this.functionMap.get(functionName);
    }

    public HashMap<String, FunctionInstruction> getFunction() { return this.functionMap; }
}
