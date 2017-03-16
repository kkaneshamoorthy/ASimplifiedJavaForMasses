package Instruction;

public class InputInstruction implements Instruction{
    private String instructionType;
    private String id;
    private boolean isFullyDefined = false;
    private String inputLocation;
    private Variable data;

    public InputInstruction() {
        this.instructionType = "INPUT";
        this.data = new Variable("userInput", "", "GLOBAL");
        this.id = generateId();
    }

    public InputInstruction setInputLocation(String inputLocation) {
        this.inputLocation = inputLocation;

        return this;
    }

    public InputInstruction setData(Variable data) {
        this.data = data;

        return this;
    }

    public Variable getData() {
        return this.data;
    }

    public String getInputLocation() {
        return this.inputLocation;
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

        sb.append("Scanner s = new Scanner(System.in); \n");
        sb.append("String " + this.data.getName() + " = s.nextLine() \n");

        return sb.toString();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    private String generateId() {
        return (this.instructionType+this.getInputLocation()+this.getData().getName()+this.getData().getValue()).hashCode()+"";
    }
}
