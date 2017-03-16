package Memory;

import Instruction.Instruction;

import java.util.HashMap;

public class InstructionStorage {
    HashMap<Integer, Instruction> instructionMap;

    public InstructionStorage() {
        this.instructionMap = new HashMap<>();
    }

    public void addInstruction(int instructionCounter, Instruction instruction) {
        this.instructionMap.put(instructionCounter, instruction);
    }

    public Instruction getInstruction(int instructionCounter) {
        return this.instructionMap.get(instructionCounter);
    }

    public HashMap<Integer, Instruction> getInstructions(){
        return this.instructionMap;
    }
}
