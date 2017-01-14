package Memory;

import java.util.HashMap;

public class VariableHolder {
    public static final String GLOBAL = "GLOBAL";
    private HashMap<String, HashMap<String, Variable>> variableStorage;

    public VariableHolder() {
        this.variableStorage = new HashMap<String, HashMap<String, Variable>>();
    }

    public void add(String scope, String variableName, String variableValue) {
        Variable newVariable = new Variable(variableName, variableValue, scope);

        if (this.variableStorage.containsKey(scope)) {
            this.variableStorage.get(scope).put(variableName, newVariable);
        } else {
            HashMap<String, Variable> nameVariableMap = new HashMap<String, Variable>();
            nameVariableMap.put(variableName, newVariable);
            this.variableStorage.put(scope, nameVariableMap);
        }
    }

    public HashMap<String, Variable> getVariableGivenScope(String scope) {
        return this.variableStorage.get(scope);
    }

    public Variable getVariableGivenScopeAndName(String scope, String variableName) {
        return this.variableStorage.get(scope).get(variableName);
    }

    public void removeVariablesFromScope(String scope) {
        this.variableStorage.remove(scope);
    }
}
