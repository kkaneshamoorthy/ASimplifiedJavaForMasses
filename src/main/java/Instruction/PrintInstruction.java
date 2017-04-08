package Instruction;

public class PrintInstruction implements Instruction {
    private String instructionType;
    private String id;
    private boolean isFullyDefined = false;
    private Variable data;

    public PrintInstruction() {
        this.instructionType = "PRINT";
        this.data = new Variable("UNKNOWN", "", "GLOBAL");
        this.id = generateId();
    }

    public PrintInstruction setData(Variable data) {
        this.data = data;

        return this;
    }

    public Variable getData() {
        return this.data;
    }

    @Override
    public String getInstructionID() {
        return this.id;
    }

    @Override
    public boolean isFullyDefined() {
        return this.isFullyDefined;
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }

    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        System.out.println(this.data.getName() + " " + this.data.getValue());
        sb.append("System.out.println("+this.data.getValue().replace("$", "")+");");

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    private String generateId() {
        return (this.instructionType+data.getName()+data.getValue()).hashCode()+"";
    }
}
