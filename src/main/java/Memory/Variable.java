package Memory;

public class Variable {
    private String name;
    private String value;
    private String scope;

    public Variable(String name, String value, String scope) {
        this.name = name;
        this.value = value;
        this.scope = scope;
    }

    public String getName() { return this.name; }
    public String getValue() { return this.value; }
    public String getScope() { return this.scope; }
    public void setValue(String newValue) { this.value = newValue; }
}
