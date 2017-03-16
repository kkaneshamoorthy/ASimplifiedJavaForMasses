package Memory;

import Instruction.Variable;

import java.util.HashMap;

public class VariableHolder {
    public static final String GLOBAL = "GLOBAL";
    //Scope -> Variable Name, Variable
    private HashMap<String, HashMap<String, Variable>> variableStorage;

    public VariableHolder() {
        this.variableStorage = new HashMap<String, HashMap<String, Variable>>();
    }

    public boolean add(Variable variableToAdd) {
        String scope = variableToAdd.getScope();
        String variableName = variableToAdd.getName();

        if (this.variableStorage.containsKey(scope)) {
            if (this.variableStorage.get(scope).containsKey(variableName)) {
                return false;
            } else {
                this.variableStorage.get(scope).put(variableName, variableToAdd);
            }
        } else {
            HashMap<String, Variable> nameVariableMap = new HashMap<String, Variable>();
            nameVariableMap.put(variableName, variableToAdd);
            this.variableStorage.put(scope, nameVariableMap);
        }

        return true;
    }

    public void set(Variable variableToSet) {
        String scope = variableToSet.getScope();
        String variableName = variableToSet.getName();

        if (this.variableStorage.containsKey(scope)) {
            this.variableStorage.get(scope).put(variableName, variableToSet);
        } else {
            HashMap<String, Variable> nameVariableMap = new HashMap<String, Variable>();
            nameVariableMap.put(variableName, variableToSet);
            this.variableStorage.put(scope, nameVariableMap);
        }
    }

    public Variable getVariableGivenScopeAndName(String variableName, String scope) {
        if (this.variableStorage.get(scope) != null)
            return this.variableStorage.get(scope).get(variableName);

        return null;
    }

    public static void main(String[] args) {
        VariableHolder variableHolder = new VariableHolder();
        variableHolder.add(new Variable("a", "a", "a"));
        System.out.println(variableHolder.getVariableGivenScopeAndName("a", "a"));
    }

    public boolean hasVariable(String variableName, String scope) {
        return (this.getVariableGivenScopeAndName(variableName, scope) != null) ? true : false;
    }
}
