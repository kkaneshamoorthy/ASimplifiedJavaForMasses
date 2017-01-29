package Memory;

import java.util.ArrayList;
import java.util.HashMap;

public class VariableHolder {
    public static final String GLOBAL = "GLOBAL";
    private HashMap<String, HashMap<String, Variable>> variableStorage;
    private HashMap<String, Variable> scopeVariableMap;

    public VariableHolder() {
        this.variableStorage = new HashMap<String, HashMap<String, Variable>>();
        this.scopeVariableMap = new HashMap<>();
    }

    public boolean add(Variable variableToAdd) {
        String scope = variableToAdd.getScope();
        String variableName = variableToAdd.getName();

        if (this.variableStorage.containsKey(scope)) {
            if (this.variableStorage.get(scope).containsKey(variableName)) {
                return false;
            } else {
                this.variableStorage.get(scope).put(variableName, variableToAdd);
                this.scopeVariableMap.put(variableName, variableToAdd);
            }
        } else {
            HashMap<String, Variable> nameVariableMap = new HashMap<String, Variable>();
            nameVariableMap.put(variableName, variableToAdd);
            this.variableStorage.put(scope, nameVariableMap);
            this.scopeVariableMap.put(variableName, variableToAdd);
        }

        return true;
    }

    public Variable getVariableGivenScopeAndName(String scope, String variableName) {
        HashMap<String, Variable> variableNameVariableMap = this.variableStorage.get(scope);

        return variableNameVariableMap.get(variableName);
    }

    public HashMap<String, Variable> getVariableGivenScope(String scope) {
        return this.variableStorage.get(scope);
    }

    public Variable getVarGivenScopeAndName(String scope, String variableName) {
        return this.variableStorage.get(scope).get(variableName);
    }

    public void removeVariablesFromScope(String scope) {
        this.variableStorage.remove(scope);
    }
}
