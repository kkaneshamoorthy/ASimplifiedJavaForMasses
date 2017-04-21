package Instruction;

import Memory.Variable;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by kowrishankar on 26/03/17.
 */
public class ErrorMessage implements Instruction {
    private String instructionType;
    private String functionName;
    private boolean isFullyDefined = false;
    private Variable errorMessage;
    private SecureRandom random = new SecureRandom();
    private String id;

    public ErrorMessage(Variable errorMessage) {
        this.errorMessage = errorMessage;
        this.instructionType = "Error Message";
    }

    @Override
    public String getInstructionID() {
        return (this.instructionType+ new BigInteger(32, random).toString(32));
    }

    @Override
    public String getInstructionType() {
        return this.instructionType;
    }

    @Override
    public String generateCode() {
        return "System.out.println("+this.errorMessage.getValue()+");";
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
