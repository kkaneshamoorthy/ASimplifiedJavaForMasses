package Engine;

import Memory.InstructionStorage;
import Memory.JavaProgramTemplate;
import Memory.VariableHolder;
import Utility.FileUtility;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CodeExecution {
    private TextArea console;

    public CodeExecution(TextArea console) {
        this.console = console;
        this.append("----Output of execution----");
    }

    public void executeCode(InstructionStorage instructionStorage, VariableHolder variableHolder) throws Exception {
        JavaProgramTemplate javaProgramTemplate = new JavaProgramTemplate(instructionStorage, variableHolder);
        FileUtility.saveJavaProgramTemporaryForExecution(null, javaProgramTemplate);

        System.out.println("---- Output ----");
        runProcess("javac " + javaProgramTemplate.getClassName() +".java");
        runProcess("java " + javaProgramTemplate.getClassName());
    }

    private void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + "" + line);
            this.append(name+""+line);
        }
    }

    private void runProcess(String command) throws Exception {
        Runtime.getRuntime().exec(command);
        Process pro = Runtime.getRuntime().exec(command);
        printLines("", pro.getInputStream());
        printLines("", pro.getErrorStream());
        pro.waitFor();
//        System.out.println(command + " exitValue() " + pro.exitValue());
    }

    private void append(String str) {
        this.console.insertText(this.console.getLength(), str);
        this.console.insertText(this.console.getLength(), "\n");
    }
}
