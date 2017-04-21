package Memory;

import Utility.Helper;

/***
 * This class is used to store data about variable
 */
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

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setValue(String newValue) { this.value = newValue; }
    public String getValue() { return this.value; }
    public void setScope(String scope) { this.scope = scope; }
    public String getScope() { return this.scope; }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }

    public String getExprType() {
        if (Helper.isNumber(this.value)) return "int";
        if (Helper.isString(this.value)) return "String";
        if (Helper.isBoolean(this.value)) return "boolean";

        return "String";
    }
}
