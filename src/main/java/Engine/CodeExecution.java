package Engine;

import Instruction.Instruction;
import Memory.InstructionStorage;
import Memory.JavaProgramTemplate;
import Memory.VariableHolder;
import Utility.FileUtility;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CodeExecution {
    private TextArea console;

    public CodeExecution(TextArea console) {
        this.console = console;
    }

    public void executeCode(String[] sourceCode) throws Exception {
        SynaticAnalyser synaticAnalyser = new SynaticAnalyser();
        InstructionDetector instructionDetector = new InstructionDetector(new InstructionSet());
        HashMap<Integer, Instruction> map = synaticAnalyser.generateInstructions(instructionDetector.detect(sourceCode));
        JavaProgramTemplate javaProgramTemplate = new JavaProgramTemplate(map, synaticAnalyser.getVariableHolder());
        FileUtility.saveJavaProgramTemporaryForExecution(null, javaProgramTemplate);

        System.out.println("---- Output ----");
        runProcess("javac " + javaProgramTemplate.getClassName() +".java");
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

    private void runProcess(String command) throws Exception {
        Runtime.getRuntime().exec(command);
        Process pro = Runtime.getRuntime().exec(command);
        printLines("", pro.getInputStream());
        printError("", pro.getErrorStream());
        pro.waitFor();
//        System.out.println(command + " exitValue() " + pro.exitValue());
    }

    private void append(String str) {
        this.console.insertText(this.console.getLength(), str);
        this.console.insertText(this.console.getLength(), "\n");
    }
}
