package Memory;

import java.util.HashMap;

/****
 * This class provides a data structure to store variables within a scope
 */
public class VariableHolder {

    //(Scope Name, (Variable Name, Variable))
    private HashMap<String, HashMap<String, Variable>> variableStorage;

    public VariableHolder() {
        this.variableStorage = new HashMap<String, HashMap<String, Variable>>();
    }

    /***
     * Adds Variable to a scope
     * @param variableToAdd
     * @return
     */
    public boolean add(Variable variableToAdd) {
        String scope = variableToAdd.getScope();
        String variableName = variableToAdd.getName();

        if (this.variableStorage.containsKey(scope)) {
            if (this.variableStorage.get(scope).containsKey(variableName)) {
                return false; //If variable already exists
            } else {
                this.variableStorage.get(scope).put(variableName, variableToAdd);
            }
        } else { //if no scope exists then creates a new scope
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

    /***
     * Get Variable given variable name and scope
     * @param variableName
     * @param scope
     * @return
     */
    public Variable getVariableGivenScopeAndName(String variableName, String scope) {
        if (this.variableStorage.get(scope) != null)
            return this.variableStorage.get(scope).get(variableName);

        return null;
    }

    public boolean hasVariable(String variableName, String scope) {
        return (this.getVariableGivenScopeAndName(variableName, scope) != null) ? true : false;
    }
}
