package Instruction;

import java.util.HashMap;
import Instruction.Instruction;

public class BlockInstruction {
    private int instructionCounter;
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

    public String generateCode() {
        StringBuilder sb = new StringBuilder();

        for (Integer counter : this.instructionMap.keySet()) {
            Instruction instruction = this.instructionMap.get(counter);
            sb.append(instruction.generateCode());
        }

        return sb.toString();
    }
}
