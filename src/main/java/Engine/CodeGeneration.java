package Engine;

import Instruction.Instruction;
import Memory.InstructionStorage;
import Memory.JavaProgramTemplate;
import Memory.VariableHolder;
import Utility.FileUtility;

import java.util.HashMap;

public class CodeGeneration {

    public void generateCode(String[] sourceCode) {
//        LexicalAnalyser la = new LexicalAnalyser();
        SynaticAnalyser synaticAnalyser = new SynaticAnalyser();
        InstructionDetector instructionDetector = new InstructionDetector(new InstructionSet());
        HashMap<Integer, Instruction> tokenisedInstriction = synaticAnalyser.generateInstructions(instructionDetector.detect(sourceCode));
        HashMap<Integer, String> javaCode = this.generateJavaCode(tokenisedInstriction);

        System.out.println("--- Java code is being generated ---");

        for (Integer instructionCounter : javaCode.keySet()) {
            String instruction = javaCode.get(instructionCounter);
            System.out.println(instruction);
        }

        FileUtility.saveJavaProgram(null, new JavaProgramTemplate(tokenisedInstriction, synaticAnalyser.getVariableHolder()));

        System.out.println("--- Finished generating code ---");
    }

    public HashMap<Integer, String> generateJavaCode(HashMap<Integer, Instruction> tokenisedInstruction) {
        HashMap<Integer, String> javaCode = new HashMap<>();

        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            Instruction instruction = tokenisedInstruction.get(instructionCounter);
            javaCode.put(instructionCounter, instruction.generateCode());
        }

        return javaCode;
    }
}
