package Utility;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;

public class FileUtility {

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

    public static void openFile(Stage primaryStage, TextArea ta) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try {
                System.out.println(selectedFile.getPath());
                BufferedReader br = new BufferedReader(new FileReader(selectedFile.getPath()));
                String sCurrentLine = "";
                while ((sCurrentLine = br.readLine()) != null) {
                    append(ta, sCurrentLine);
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

    public static void append(TextArea ta, String s) {
        ta.insertText(ta.getLength(), s);
        ta.insertText(ta.getLength(), "\n");
    }
}
