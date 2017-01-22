package Memory;

import Engine.BlockInstruction;

import java.util.HashMap;

public class InstructionStorage {
    HashMap<Integer, BlockInstruction> instructionBlockMap;

    public InstructionStorage() {
        this.instructionBlockMap = new HashMap<>();
    }

    public void addInstructionBlock(int instructionCounter, BlockInstruction blockInstruction) {
        this.instructionBlockMap.put(instructionCounter, blockInstruction);
    }

    public BlockInstruction getBlockInstruction(int instructionCounter) {
        return this.instructionBlockMap.get(instructionCounter);
    }

    public HashMap<Integer, BlockInstruction> getInstructions(){
        return this.instructionBlockMap;
    }
}
