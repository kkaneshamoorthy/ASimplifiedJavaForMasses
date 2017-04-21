package Utility;

import Engine.CodeExecution;
import GUI.CodeEditor;
import Memory.JavaProgramTemplate;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.tools.ant.taskdefs.Execute;

import java.io.*;

public class FileUtility {

    private static String fileName;

    public static void saveFile(Stage primaryStage, String content) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        File selectedFile = fileChooser.showSaveDialog(primaryStage);

        if (selectedFile != null) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(selectedFile.getPath()));
                bw.write(content);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                //TODO: Print exception to the console
            }
        }
    }

    public static void saveTemporaryFile(Stage primaryStage, String content) {
        try {
            File temp = new File("temp.java");
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: Print exception to the console
        }
    }

    public static void clearTempFile() {
        FileUtility.saveTemporaryFile(null, "");
    }

    public static void openFile(Stage primaryStage, CodeEditor editor) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try {
                System.out.println(selectedFile.getPath());
                BufferedReader br = new BufferedReader(new FileReader(selectedFile.getPath()));
                String sCurrentLine = "";
                editor.clear();
                while ((sCurrentLine = br.readLine()) != null) {
                    editor.appendText(sCurrentLine + "\n");
//                    append(ta, sCurrentLine);
                    System.out.println(sCurrentLine);
                }

            } catch (FileNotFoundException e) {
                //TODO: print exception to the console
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("Error reading file");
            }
        }
    }

    public static void saveJavaProgram(Stage primaryStage, JavaProgramTemplate javaProgramTemplate) {
        fileName = javaProgramTemplate.getClassName();
        saveFile(primaryStage, javaProgramTemplate.toString());
    }

    public static void saveJavaProgramTemporaryForExecution(Stage primaryStage, JavaProgramTemplate javaProgramTemplate) {
        fileName = javaProgramTemplate.getClassName();
        saveTemporaryFile(primaryStage, javaProgramTemplate.toString());
    }
}
