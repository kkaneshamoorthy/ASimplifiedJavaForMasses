package Memory;

import java.util.HashMap;
import Instruction.Instruction;

/***
 * This class is used to store the body of an instruction.
 * Body is simply a collection of instructions
 */
public class BlockInstruction {

    /***
     * Stores the current instruction number
     */
    private int instructionCounter;

    /***
     * (Instruction Counter, Instruction)
     */
    private HashMap<Integer, Instruction> instructionMap;

    public BlockInstruction() {
        this.instructionCounter = 0;
        this.instructionMap = new HashMap<>();
    }

    public BlockInstruction(Instruction instruction) {
        this.instructionCounter = 0;
        this.instructionMap = new HashMap<>();
        this.addInstructionToBlock(instruction);
    }

    /***
     * Adds instruction to hashmap
     * @param instruction
     */
    public void addInstructionToBlock(Instruction instruction) {
        this.instructionMap.put(this.instructionCounter, instruction);
        this.instructionCounter++;
    }

    public HashMap<Integer, Instruction> getInstructionBlock() {
        return this.instructionMap;
    }

    public Instruction getInstructionGivenType(String instructionType) {
        for (Integer counter : this.instructionMap.keySet()) {
            Instruction instruction = this.instructionMap.get(counter);
            if (instruction.getInstructionType().equals(instructionType))
                return instruction;
        }

        return null;
    }

    /***
     * Generates Java code for instructions in the body
     * @return
     */
    public String generateCode() {
        StringBuilder javaCode = new StringBuilder();

        for (Integer counter : this.instructionMap.keySet()) {
            Instruction instruction = this.instructionMap.get(counter);
            javaCode.append(instruction.generateCode()); //gets the Java code of each of the instruction
        }

        return javaCode.toString();
    }
}
