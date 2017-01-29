package Engine;

import Memory.InstructionStorage;
import Memory.Variable;
import Memory.VariableHolder;

import Instruction.Instruction;
import Instruction.InputInstruction;
import Instruction.PrintInstruction;
import Instruction.LoopInstruction;
import Instruction.IfInstruction;
import Instruction.AssignmentInstruction;

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
    private InstructionStorage instructionStorage;

    public LexerAnalyser() {
        this.instructionSet = new InstructionSet();
        this.instructions = new HashMap<Integer, HashMap<String, String>>();
        this.instructionDetector = new InstructionDetector(this.instructionSet);
        this.variableHolder = new VariableHolder();
        this.instructionStorage = new InstructionStorage();
    }

    public HashMap<Integer, Instruction> lexicalAnalyser(String[] statements) {
        int instructionCounter = -1;
        Instruction generalInstruction = null;
        for (String statement : statements) {
            String[] tokens = statement.replace("\\t", "").trim().split(" ");
            ArrayList<String> identifiedTokens = this.instructionDetector.identifyToken(tokens);

            for (String t : identifiedTokens)
                System.out.print(t + " ");
            System.out.println();

            for (String token : identifiedTokens) {
                switch (token) {
                    case "INPUT":
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            InputInstruction inputInstruction = new InputInstruction();
                            Variable var = new Variable("userData", "", "GLOBAL");
                            boolean added = this.variableHolder.add(var);
                            inputInstruction.setData(var);
                            this.instructionStorage.addInstruction(instructionCounter, inputInstruction);
                        }
                        break;
                    case "PRINT":
                        PrintInstruction printInstruction = null;
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            printInstruction = new PrintInstruction();
                            this.instructionStorage.addInstruction(instructionCounter, printInstruction);
                        } else {
                            printInstruction = new PrintInstruction();
                            BlockInstruction bi = new BlockInstruction();
                            bi.addInstructionToBlock(printInstruction);
                            this.setBodyOfInstruction(generalInstruction, bi);
                        }
                        String str = getArrLsElement(identifiedTokens, "STRING");
                        String intStr = getArrLsElement(identifiedTokens, "INT");
                        if (str != null && str.startsWith("STRING")) {
                            Variable var = new Variable("str", str.replace("STRING =>", "").trim(), "GLOBAL");
                            boolean added = this.variableHolder.add(var);
                            printInstruction.setData(var);
                        } else if (intStr != null && intStr.startsWith("INT")) {
                            Variable var = new Variable("intVar", intStr.replace("INT =>", "").trim(), "GLOBAL");
                            boolean added = this.variableHolder.add(var);
                            printInstruction.setData(var);
                        }
                        break;
                    case "LOOP":
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            generalInstruction = new LoopInstruction();
                            this.instructionStorage.addInstruction(instructionCounter, generalInstruction);
                        } else {
                            LoopInstruction loopInstruction = new LoopInstruction();
                            BlockInstruction bi = new BlockInstruction();
                            bi.addInstructionToBlock(loopInstruction);
                            this.setBodyOfInstruction(generalInstruction, bi);
                        }
                        break;
                    case "ASSIGNMENT":
                        instructionCounter++;
                        AssignmentInstruction assignmentInstruction = createAssignment(identifiedTokens);
                        this.instructionStorage.addInstruction(instructionCounter, assignmentInstruction);
                        break;
                    case "IF":
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            generalInstruction = new IfInstruction();
                            this.instructionStorage.addInstruction(instructionCounter, generalInstruction);
                        } else {
                            IfInstruction ifInstruction = new IfInstruction();
                            generalInstruction = ifInstruction;
                            BlockInstruction bi = new BlockInstruction();
                            bi.addInstructionToBlock(ifInstruction);
                            this.setBodyOfInstruction(generalInstruction, bi);
                        }
                        break;
                    default:
                        unknownToken(token, identifiedTokens, generalInstruction);
                }
            }
        }

        return this.instructionStorage.getInstructions();
    }

    private AssignmentInstruction createAssignment(ArrayList<String> identifiedTokens) {
        StringBuilder expression = new StringBuilder();
        for (int i=2; i<identifiedTokens.size(); i++)
            expression.append(identifiedTokens.get(i));
        Variable assignedTo = new Variable(identifiedTokens.get(0).replace("VARIABLE_NAME =>", "").trim(), "", "GLOBAL");

        return new AssignmentInstruction(assignedTo, expression.toString());
    }

    private void unknownToken(String identifiedToken, ArrayList<String> identifiedTokens, Instruction generalInstruction) {
        if (this.instructionDetector.isNumber(identifiedToken) || this.instructionDetector.isArithmeticOperation(identifiedToken)) {
            if (identifiedTokens.contains("LOOP")) {
                LoopInstruction instruction = (LoopInstruction) generalInstruction;
                instruction.setNumOfIteration(Integer.parseInt(identifiedToken));
            } else if (identifiedTokens.contains("PRINT")) {
                if (whichInstruction(generalInstruction).equals("LOOP")) {
                    LoopInstruction instruction = (LoopInstruction) generalInstruction;
                    Variable var = instruction.getIteration();
                    var.setValue(var.getValue() + identifiedToken);
                    instruction.setIteration(var);
                }
            }
        } else if (identifiedToken.startsWith("VARIABLE_NAME")) {
            String variableName = identifiedToken.split("=>")[1].trim();
            Variable var = new Variable(variableName, "", "GLOBAL");
            this.variableHolder.add(var);
        }
    }

    //Sets @bi as body of @generalInstruction - bi is the body of @generalInstruction
    private void setBodyOfInstruction(Instruction generalInstruction, BlockInstruction bi) {
        if (generalInstruction == null) return;

        if (generalInstruction instanceof LoopInstruction) {
            LoopInstruction loopInstruction = (LoopInstruction) generalInstruction;
            loopInstruction.setBody(bi);
        } else if (generalInstruction instanceof IfInstruction) {
            IfInstruction ifInstruction = (IfInstruction) generalInstruction;
            ifInstruction.setBody(bi);
        }
    }

    private String whichInstruction(Instruction generalInstruction) {
        if (generalInstruction instanceof LoopInstruction)
            return InstructionSet.LOOP;
        else if (generalInstruction instanceof PrintInstruction)
            return InstructionSet.PRINT;
        else if (generalInstruction instanceof IfInstruction)
            return InstructionSet.IF;
        else if (generalInstruction instanceof InputInstruction)
            return InstructionSet.INPUT;
        else
            return "UNKOWN";
    }

    private String getArrLsElement(ArrayList<String> ls, String str) {
        String result = null;
        for (String token : ls)
            if (token.startsWith(str))
                return token;

        return result;
    }

    public void generateCode(String[] statements) {
        HashMap<Integer, Instruction> tokenisedInstriction = this.lexicalAnalyser(statements);
        HashMap<Integer, String> javaCode = this.codeGeneration(tokenisedInstriction);

        System.out.println("--- Java code is being generated ---");

        for (Integer instructionCounter : javaCode.keySet()) {
            String instruction = javaCode.get(instructionCounter);
            System.out.println(instruction);
        }
    }

    private HashMap<Integer, String> codeGeneration(HashMap<Integer, Instruction> tokenisedInstruction) {
        HashMap<Integer, String> javaCode = new HashMap<>();

        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            Instruction instruction = tokenisedInstruction.get(instructionCounter);
            javaCode.put(instructionCounter, instruction.generateCode());
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
                            Variable var = new Variable(VariableHolder.GLOBAL, "varx", tempVariableValue);
                            this.variableHolder.add(var);
                            this.variableHolder.add(var);
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

    public String getStringBetweenSpeechMarks(String statement) {
        String result = "";
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(statement);
        while (m.find()) {
            result = m.group(1);
        }

        return result;
    }
}
