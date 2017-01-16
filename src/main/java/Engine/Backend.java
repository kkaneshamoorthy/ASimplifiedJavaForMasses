package Engine;

import Memory.Variable;
import Memory.VariableHolder;

import java.util.HashMap;

public class Backend {
    private InstructionSet instructionSet;
    private HashMap<Integer, HashMap<String, String>> instructions;
    private VariableHolder variableHolder;

    public Backend(InstructionSet instructionSet, HashMap<Integer, HashMap<String, String >> instructions, VariableHolder variableHolder) {
        this.instructionSet = instructionSet;
        this.instructions = instructions;
        this.variableHolder = variableHolder;
    }

    public void executeInstruction() {
        for (HashMap<String, String> instruction : instructions.values()) {
            for (String key : instruction.keySet()){
                System.out.println(key);
                switch (key) {
                    case InstructionSet.PRINT:
                        this.executePrintInstruction(instruction);
                        break;
                    case InstructionSet.EXP:
                        this.executeExpressionInstruction(instruction);
                        break;
                    case InstructionSet.VARIABLE:
                        this.executeCreateVariableInstruction(instruction);
                        break;
                    case InstructionSet.PRINT_VARIABLE:
                        this.executePrintVariableInstruction(instruction);
                        break;
                    case InstructionSet.LOOP:
                        this.executeLoopInstruction(instruction);
                        break;
                    default:
                        System.out.println("ELSE: " + key);
                }
            }
        }
    }

    private void executePrintInstruction(HashMap<String, String> instruction) {
        //TODO: check if print ARTHMETIC OR STRING
        String value = instruction.get(this.instructionSet.PRINT);
        if (value.equals(" ")) {
//            FileFunction.append(console, "Exception: Missing quotes");
            System.out.println("Exception: Missing quotes");
        } else {
//            FileFunction.append(console, value);
            System.out.println(value);
        }
    }

    private void executeLoopInstruction(HashMap<String, String> instruction) {
        String expr = instruction.get(this.instructionSet.LOOP);
        String loopContent = instruction.get(this.instructionSet.LOOP_CONTENT);
        String[] loop = new String[1];
        loop[0] = loopContent.trim();
        if (expr.split(" ").length > 1)
            System.out.println("EXP");
        else
        if (this.isNum(expr)) {
//            int numOfLoop = Integer.parseInt(expr);
//            for (int var = 0; var<numOfLoop; var++)
//                new LexerAnalyser().analyse(loop);
        } else {
            String variableName = expr;
        }
    }

    private boolean isNum(String expr) {
        try {
            Integer.parseInt(expr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void executePrintVariableInstruction(HashMap<String, String> instruction) {
        System.out.println("PRINT VARIABLE");
        String variableName = instruction.get(InstructionSet.PRINT_VARIABLE);
        System.out.println("VARIABLE NAME: " + variableName);
        Variable var = this.variableHolder.getVariableGivenScopeAndName("GLOBEL", variableName);
        if (var != null) {
//            FileFunction.append(console, var.getValue());
            System.out.println(var.getValue());
        }
    }

    private void executeCreateVariableInstruction(HashMap<String, String> instruction) {
        System.out.println("CREATE");
        //TODO: check if ARTHMETIC OR STRING
//		FileFunction.append(console, "Variable x");
    }

    private void executeExpressionInstruction(HashMap<String, String> instruction) {
        String expression = instruction.get(InstructionSet.EXP);
        String[] stringNum = instruction.get(InstructionSet.EXP).split("[-+*/]");
        int sum = Integer.parseInt(stringNum[0]);
        int counter = 0;
        for (int x=1; x<stringNum.length; x++) {
            counter+=stringNum[x-1].length();
            int number = Integer.parseInt(stringNum[x]);
            char opera = expression.charAt(counter);
            counter += 1;
            if (opera == '+') sum += number;
            else if (opera == '-') sum -= number;
            else if (opera == '*') sum *= number;
            else if (opera == '/') sum /= number;
        }

//        FileFunction.append(console, sum+"");
    }
}
