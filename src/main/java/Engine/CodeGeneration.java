package Engine;

import GUI.Console;
import Instruction.Instruction;
import Memory.JavaProgramTemplate;
import Utility.FileUtility;
import Utility.Helper;

import java.util.HashMap;

public class CodeGeneration {
    private Console console;

    public CodeGeneration(Console console) {
        this.console = console;
    }

    /***
     * Generates Java code given an array of source code
     * @param sourceCode
     */
    public void generateCode(String[] sourceCode) {
        LexicalAnalyser lexicalAnalyser = new LexicalAnalyser(Helper.getInstructionSet());
        SynaticAnalyser synaticAnalyser = new SynaticAnalyser(console);
        HashMap<Integer, Instruction> intermediateRepresentation =
                synaticAnalyser.generateIntermediateRepresentation(
                        lexicalAnalyser.generateAnnotatedToken(sourceCode));

        if (intermediateRepresentation == null) {
            this.console.reportError("Terminating code generation");
            return;
        }

        printCode(intermediateRepresentation);

        FileUtility.saveJavaProgram(null, new JavaProgramTemplate(intermediateRepresentation));
    }

    public void printCode(HashMap<Integer, Instruction> tokenisedInstruction) {
        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            Instruction instruction = tokenisedInstruction.get(instructionCounter);
            System.out.println(instruction.generateCode());
        }
    }
}
