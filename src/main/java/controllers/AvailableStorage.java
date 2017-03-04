package controllers;

import Engine.InstructionDetector;
import Engine.InstructionSet;
import GUI.CodeEditor;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class AvailableStorage implements Runnable{

    private TreeView storageView;
    private CodeEditor codeEditor;
    private InstructionDetector instructionDetector;

    public AvailableStorage(TreeView storageView, CodeEditor codeEditor) {
        this.storageView = storageView;
        this.codeEditor = codeEditor;
        this.instructionDetector = new InstructionDetector(new InstructionSet());
    }

    @Override
    public void run() {
        addFunctionNamesToView();
    }

    public ArrayList<String> addFunctionNamesToView() {
        ArrayList<String> functionNames = new ArrayList<>();

        String[] lines = codeEditor.getText().split("\n");

        for (String line : lines) {
            ArrayList<String> tokens = this.instructionDetector.identifyTokens(line);
            String functionName = this.getArrLsElement(tokens, "FUNCTION_NAME");
            if (functionName != null) {
                TreeItem treeItem = this.storageView.getRoot();
                TreeItem<String> function = (TreeItem) treeItem.getChildren().get(1);

                ObservableList<TreeItem<String>> functionNamesTree =  function.getChildren();
                for (TreeItem<String> functionItem : functionNamesTree) {
                    if (!functionItem.getValue().equals(functionName.replace("FUNCTION_NAME =>", ""))) {
                        this.storageView.getRoot().getChildren().addAll(new TreeItem<String>(functionName.replace("FUNCTION_NAME =>", "")));
                    }
                }
            }
        }

        return functionNames;
    }

    private String getArrLsElement(ArrayList<String> ls, String str) {
        String result = null;
        for (String token : ls)
            if (token.startsWith(str))
                return token;

        return result;
    }

    public static void main(String[] args) {
        int a = -1,b = 0,c=-1;

        for (; a < 12; b++) {
            a = (a+a) * c;

            if (a == 32) {
                System.out.println(a + " " + b + " " + c);
            }
        }

        permute(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    }


    public static void permute(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        backtrack(list, new ArrayList<>(), nums);

        System.out.println(list);
    }

    public static void backtrack(List<List<Integer>> list, List<Integer> tempList, int[] nums) {
        if (tempList.size() == nums.length) {
            list.add(new ArrayList<>(tempList));
        } else {
            for (int i=0; i<nums.length; i++) {
                if (tempList.contains(nums[i])) continue; // if we already have the value then it continues and gets the next value
                tempList.add(nums[i]);
                backtrack(list, tempList, nums);
                tempList.remove(tempList.size() - 1);
            }
        }
    }
}
