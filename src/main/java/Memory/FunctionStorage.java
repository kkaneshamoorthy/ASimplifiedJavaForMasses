package Memory;

import Instruction.FunctionInstruction;

import java.util.HashMap;

/***
 * Has an implementation to store functions
 */
public class FunctionStorage {

    /***
     * (Function Name, FunctionInstruction Object)
     */
    private HashMap<String, FunctionInstruction> functionMap;

    public FunctionStorage() {
        this.functionMap = new HashMap<String, FunctionInstruction>();
    }

    /***
     * Adds FunctionInstruction to the function storage
     * @param functionInstruction
     * @return
     */
    public boolean add(FunctionInstruction functionInstruction) {
        String functionName = functionInstruction.getFunctionName();

        if (!this.functionMap.containsKey(functionName)) {
            this.functionMap.put(functionName, functionInstruction);

            return true; //successfully stored
        }

        return false; //function already defined
    }

    public FunctionInstruction get(String functionName) {
        return this.functionMap.get(functionName);
    }

    public HashMap<String, FunctionInstruction> getFunction() { return this.functionMap; }
}
