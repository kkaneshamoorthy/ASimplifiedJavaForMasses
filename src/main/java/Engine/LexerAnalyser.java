package Engine;

import Memory.VariableHolder;

import java.util.ArrayList;
import java.util.HashMap;
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

    public void lexicalAnalyser(String[] statements) {
        HashMap<Integer, ArrayList<String>> result = new HashMap<Integer, ArrayList<String>>();
        int instructionCounter = 0;

        for (String statement : statements) {
            String detectedInstruction = this.instructionDetector.detectInstruction(statement);
            System.out.print("INSTRUCTION: " + detectedInstruction + " -> ");
//            if (detectedInstruction.equals(this.instructionSet.PRINT))

            ArrayList<String> tokenisedStatement = new ArrayList<>();
            String[] tokens = statement.split(" "); //TODO: statement should not be split by space. e.g x=2+3+4
            for (String token : tokens) {
                String identifiedToken = this.instructionDetector.identifyToken(token.toUpperCase());
                tokenisedStatement.add(identifiedToken);

                System.out.print(identifiedToken + " ");
            }
            System.out.println();
            result.put(instructionCounter, tokenisedStatement);
            instructionCounter++;
        }
    }

    public static void main(String[] args) {
        String[] statements = {"can you print hello world;", "x = 2 + 3 + 1;", "loop 3: print $x; loop-end;"};
        LexerAnalyser la = new LexerAnalyser();
        la.lexicalAnalyser(statements);
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
