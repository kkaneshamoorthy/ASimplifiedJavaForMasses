package Engine;

import java.util.HashMap;

public class BlockInstruction {
    private int instructionCounter;
    private HashMap<Integer, Instruction> instructionMap;

    public BlockInstruction() {
        this.instructionCounter = 0;
        this.instructionMap = new HashMap<>();
    }

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

    public Instruction getInstructionGivenType(String instructionType, boolean isFullyDefined) {
        for (Integer counter : this.instructionMap.keySet()) {
            Instruction instruction = this.instructionMap.get(counter);
            if (instruction.getInstructionType().equals(instructionType) && instruction.isFullyDefined() == isFullyDefined)
                return instruction;
        }

        return null;
    }
}
