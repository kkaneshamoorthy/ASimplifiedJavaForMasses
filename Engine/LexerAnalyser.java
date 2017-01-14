package Engine;

import Memory.VariableHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerAnalyser {
    private HashMap<Integer, HashMap<String, String>> instructions;
    private int instructionCounter = 0;
    private InstructionDetector instructionDetector;
    private InstructionSet instructionSet;
    private VariableHolder variableHolder;

    public LexerAnalyser() {
        this.instructionSet = new InstructionSet();
        this.instructions = new HashMap<Integer, HashMap<String, String>>();
        this.instructionDetector = new InstructionDetector(this.instructionSet);
        this.variableHolder = new VariableHolder();
    }

    public void analyse(String[] statements) {
        for (int i=0; i<statements.length; i++) {
            String detectedInstruction = this.instructionDetector.detectInstruction(statements[i]);
            String statement = statements[i];
            System.out.println(detectedInstruction);
            if (detectedInstruction.equals(this.instructionSet.PRINT))
                this.printStatement(statement);
            else if (detectedInstruction.equals(this.instructionSet.VARIABLE))
                this.variableStatement(statement);
            else if (detectedInstruction.equals(this.instructionSet.LOOP))
                this.loopStatement(statements, statement);
            else if (detectedInstruction.equals(this.instructionSet.ARITHMETIC))
                this.arithmeticStatement(statement);
            else if (detectedInstruction.equals(this.instructionSet.EXP))
                this.expressionStatement(statement);
            else
                this.unknowStatement(statement, detectedInstruction);
        }

        Backend be = new Backend(this.instructionSet, this.instructions, this.variableHolder);
        be.executeInstruction();
    }

    public HashMap<Integer, HashMap<String, String>> lexicalAnalyser(String[] statements) {
        HashMap<Integer, HashMap<String, String>> result = new HashMap<Integer, HashMap<String, String>>();
        int instructionCounter = 0;

        for (String statement : statements) {
            HashMap<String, String> tokenisedStatement = new HashMap<>();
            String[] tokens = statement.split(" "); //TODO: statement should not be split by space. e.g x=2+3+4
            ArrayList<String> identifiedTokens = this.instructionDetector.identifyToken(tokens);

            for (String token : identifiedTokens) {
                switch (token) {
                    case "INPUT":
                        tokenisedStatement.put(token, "GET_INPUT");
                        if (tokenisedStatement.containsKey("PRINT")) {
                            tokenisedStatement.put("PRINT", "PRINT_VARIABLE => ");
                            tokenisedStatement.put(token, "PRINT_INPUT");
                        }
                        break;
                    case "PRINT":
                        tokenisedStatement.put(token, "");

                        String str = getArrayListElementWithText(identifiedTokens, "STRING");
                        if (str != null)
                            tokenisedStatement.put(token, str);

                        break;
                }
                System.out.print(token + " ");
            }
            System.out.println();
            result.put(instructionCounter, tokenisedStatement);
            instructionCounter++;
        }

        return result;
    }

    private String getArrayListElementWithText(ArrayList<String> ls, String str) {
        String result = null;
        for (String token : ls)
            if (token.contains(str))
                return token;

        return result;
    }

    public void generateCode(String[] statements) {
        HashMap<Integer, HashMap<String, String>> tokenisedInstriction = this.lexicalAnalyser(statements);
        HashMap<Integer, ArrayList<String>> javaCode = this.codeGeneration(tokenisedInstriction);

        System.out.println("--- Java code is being generated ---");

        for (Integer instructionCounter : javaCode.keySet()) {
            ArrayList<String> instructionLs = javaCode.get(instructionCounter);
            for (String i : instructionLs) {
                System.out.println(i);
            }
        }
    }

    private HashMap<Integer, ArrayList<String>> codeGeneration(HashMap<Integer, HashMap<String, String>> tokenisedInstruction) {
        HashMap<Integer, ArrayList<String>> javaCode = new HashMap<>();
        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            HashMap<String, String> tokens = tokenisedInstruction.get(instructionCounter);
            ArrayList<String> javaInstructions = new ArrayList<>();
            for (String instruction : tokens.keySet()) {
                switch (instruction) {
                    case InstructionSet.INPUT:
                        String additionalInfo = tokens.get(instruction);
                        if (additionalInfo.equals("GET_INPUT")) {
                            javaInstructions.add("Scanner s = new Scanner(System.in);");
                            javaInstructions.add("String tempVariableValue = s.nextLine();");
                        }
                        break;
                    case InstructionSet.PRINT:
                        String addInfo = tokens.get("PRINT");
                        if (addInfo.contains("PRINT_VARIABLE")) {
                            javaInstructions.add("System.out.println(tempVariableValue);");
                        } else if (addInfo.contains("STRING =>")) {
                            javaInstructions.add("System.out.println("+addInfo.split("=>")[1].trim()+");");
                        }
                        break;
                }
            }

            javaCode.put(instructionCounter, javaInstructions);
        }

        return javaCode;
    }

    public void codeExecution(HashMap<Integer, HashMap<String, String>> tokenisedInstruction) {
        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            HashMap<String, String> tokens = tokenisedInstruction.get(instructionCounter);
            for (String instruction : tokens.keySet()) {
                switch (instruction) {
                    case InstructionSet.INPUT:
                        String additionalInfo = tokens.get(instruction);
                        if (additionalInfo.equals("GET_INPUT")) {
                            Scanner s = new Scanner(System.in);
                            System.out.println("Enter a str: ");
                            String tempVariableValue = s.nextLine();
                            this.variableHolder.add(VariableHolder.GLOBAL, "varx", tempVariableValue);
                        }
                        break;
                    case InstructionSet.PRINT:
                        String addInfo = tokens.get("PRINT");
                        if (addInfo.contains("PRINT_VARIABLE")) {
                            tokens.put(InstructionSet.PRINT, tokens.get(InstructionSet.PRINT)+"varx");
                            String variableName = tokens.get("PRINT").split("=>")[1].trim();
                            String variableValue = this.variableHolder.getVariableGivenScopeAndName(VariableHolder.GLOBAL, variableName).getValue();
                            System.out.println(variableValue);
                        } else if (addInfo.contains("STRING =>")) {
                            System.out.println(addInfo.split("=>")[1].trim().replace("\"", ""));
                        }
                        break;
                }
            }
        }
    }

    private void arithmeticStatement(String statement) {
        HashMap<String, String> instruction = new HashMap<String, String>();

        //TODO: this should go in PRINT / VARIABLE
    }

    private void expressionStatement(String statement) {
        //TODO: this should go in PRINT / VARIABLE
    }

    private void printStatement(String statement) {
        HashMap<String, String> instruction = new HashMap<String, String> ();

        if (statement.contains("\"")) { //PRINT TEXT
            String value = statement.substring(statement.indexOf("\"")+1, statement.lastIndexOf("\""));
            instruction.put(this.instructionSet.PRINT, value);
        } else { //PRINT VARIABLE
            String variableName = this.getVariableName(statement);
            instruction.put(this.instructionSet.PRINT_VARIABLE, variableName.replace("$", "").trim());
        }
        instructions.put(instructionCounter, instruction);
        instructionCounter++;
    }

    private void variableStatement(String statement) {
        System.out.println("REACHED");
        HashMap<String, String> instruction = new HashMap<String, String> ();

        String variableName = this.getVariableName(statement).replace("$", "");
        String variableValue = statement.substring(statement.lastIndexOf("=")+1);
        instruction.put(this.instructionSet.VARIABLE, variableName);
        this.variableHolder.add("GLOBEL", variableName, variableValue);
        System.out.println(variableName + " == " + variableValue);
        instructions.put(instructionCounter, instruction);
        instructionCounter++;
    }

    private void loopStatement(String[] statements, String statement) {
        HashMap<String, String> instruction = new HashMap<String, String> ();

        String loopStatement = this.getLoopInstruction(statements);
        String[] loopStartStatement = statement.split(":");

        String expr = this.getStringBetweenSpeechMarks(loopStartStatement[0]);
        instruction.put(this.instructionSet.LOOP, expr);
        instruction.put(this.instructionSet.LOOP_CONTENT, this.getLoopBody(loopStatement));
        instructions.put(instructionCounter, instruction);
        instructionCounter++;
    }

    private void unknowStatement(String statement, String detectedInstruction) {
        HashMap<String, String> instruction = new HashMap<String, String> ();

        instruction.put(detectedInstruction, statement);
        instructions.put(instructionCounter, instruction);
        instructionCounter++;
    }

    private String getLoopInstruction(String[] instructions) {
        String statementToBeReturned = "";
        while (this.instructionCounter<instructions.length){
            String statement = instructions[this.instructionCounter];
            statementToBeReturned += statement;
            if (statement.contains(this.instructionSet.LOOP_END))
                return statementToBeReturned;
            this.instructionCounter++;
        }

        return statementToBeReturned;
    }

    private String getLoopBody(String statement) {
        String result = "";
        Pattern p = Pattern.compile(":(.+?)loop-end");
        Matcher m = p.matcher(statement);
        while (m.find()) {
            result = m.group(1);
        }

        return result;
    }

    private String getVariableName(String statement) {
        String variableName = "";
        Pattern p = Pattern.compile("\\$\\s*(\\w+)");
        Matcher m = p.matcher(statement);
        while (m.find()) {
            variableName = m.group(0);
        }

        return variableName;
    }

    private String getStringBetweenSpeechMarks(String statement) {
        String result = "";
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(statement);
        while (m.find()) {
            result = m.group(1);
        }

        return result;
    }
}
