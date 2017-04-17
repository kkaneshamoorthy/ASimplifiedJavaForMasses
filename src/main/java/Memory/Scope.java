package Memory;

import Instruction.Instruction;

public class Scope {
    private String scopeID;
    private Instruction scope;

    public Scope(String scopeID, Instruction scope ) {
        this.scopeID = scopeID;
        this.scope = scope;
    }

    public String getScopeID() {
        return this.scopeID;
    }

    public Instruction getScopeInstruction() {
        return this.scope;
    }
}
