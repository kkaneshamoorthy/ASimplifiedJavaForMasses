package Engine;

import GUI.Console;
import Instruction.Instruction;
import Memory.JavaProgramTemplate;
import Utility.FileUtility;
import Utility.Helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CodeExecution {
    private Console console;

    public CodeExecution(Console console) {
        this.console = console;
    }

    /****
     * Generates Java code and executes it in a process
     * @param sourceCode
     * @throws Exception
     */
    public void executeCode(String[] sourceCode) throws Exception {
        LexicalAnalyser lexicalAnalyser = new LexicalAnalyser(Helper.getInstructionSet());
        SynaticAnalyser synaticAnalyser = new SynaticAnalyser(console);
        HashMap<Integer, Instruction> map =
                synaticAnalyser.generateIntermediateRepresentation(
                        lexicalAnalyser.generateAnnotatedToken(sourceCode));

        if (map == null) { //there is an error in syntactic analyser
            this.console.reportError("Terminating code execution");
            return;
        }

        this.printCode(map);

        JavaProgramTemplate javaProgramTemplate = new JavaProgramTemplate(map);
        FileUtility.saveJavaProgramTemporaryForExecution(null, javaProgramTemplate);

        int status = runProcess("javac " + javaProgramTemplate.getClassName() +".java");
        if (status == 0)
            runProcess("java " + javaProgramTemplate.getClassName());

        FileUtility.clearTempFile();
    }

    private void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + "" + line);
            this.append(name+""+line);
        }
    }

    private void printError(String name, InputStream error) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(error));

        if (in.readLine() != null)  console.setStyle("-fx-text-fill: red;");

        while ((line = in.readLine()) != null) {
            System.out.println(name+""+line);
            this.append(name+""+line);
        }
    }

    /****
     * Starts a process given a command
     * @param command
     * @throws Exception
     */
    private int runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines("", pro.getInputStream());
        printError("", pro.getErrorStream());
        pro.waitFor();

        return pro.exitValue();
    }

    private void printCode(HashMap<Integer, Instruction> tokenisedInstruction) {
        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            Instruction instruction = tokenisedInstruction.get(instructionCounter);
            System.out.println(instruction.generateCode());
        }
    }

    private void append(String str) {
        this.console.append(str);
    }
}
