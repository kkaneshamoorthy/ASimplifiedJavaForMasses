package Engine;

import Memory.InstructionStorage;
import Memory.Variable;
import Memory.VariableHolder;

import Instruction.Instruction;
import Instruction.InputInstruction;
import Instruction.PrintInstruction;
import Instruction.LoopInstruction;

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

    public HashMap<Integer, BlockInstruction> lexicalAnalyser(String[] statements) {
        int instructionCounter = -1;
        for (String statement : statements) {
            String[] tokens = statement.replace("\\t", "").trim().split(" "); //TODO: statement should not be split by space. e.g x=2+3+4
            ArrayList<String> identifiedTokens = this.instructionDetector.identifyToken(tokens);

            BlockInstruction bi = new BlockInstruction();
            for (String token : identifiedTokens) {
                switch (token) {
                    case "INPUT":
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            bi = new BlockInstruction();
                            bi.addInstructionToBlock(new InputInstruction());
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        } else {
                            bi = this.instructionStorage.getBlockInstruction(instructionCounter);
                            if (bi.getInstructionGivenType("PRINT") == null) {
                                bi.addInstructionToBlock(new InputInstruction());
                                this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                            }
                        }
                        Instruction printInstruction = bi.getInstructionGivenType("PRINT");
                        if (printInstruction != null) {
                            PrintInstruction printInstr = (PrintInstruction) printInstruction;
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        }
                        break;
                    case "PRINT":
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            bi = new BlockInstruction();
                            bi.addInstructionToBlock(new PrintInstruction());
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        } else {
                            bi = this.instructionStorage.getBlockInstruction(instructionCounter);
                            bi.addInstructionToBlock(new PrintInstruction());
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        }
                        String str = getArrLsElement(identifiedTokens, "STRING");
                        if (str != null)
                            ((PrintInstruction) bi.getInstructionGivenType(token)).setData(new Variable("dataToPrint", str, "GLOBAL"));
                        break;
                    case "LOOP":
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            bi = new BlockInstruction();
                            bi.addInstructionToBlock(new LoopInstruction());
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        } else {
                            bi = this.instructionStorage.getBlockInstruction(instructionCounter);
                            bi.addInstructionToBlock(new LoopInstruction());
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        }
                        break;
                    case "ASSIGNMENT":

                        break;
                    case "IF":

                        break;
                    default:
                        unknownToken(token, identifiedTokens, bi);
                }
            }
        }

        HashMap<Integer, BlockInstruction> m = this.instructionStorage.getInstructions();

        for (Integer counter : m.keySet()) {
            BlockInstruction bi = m.get(counter);
            HashMap<Integer, Instruction> map = bi.getInstructionBlock();

            for (Integer c : map.keySet()) {
                Instruction ins = map.get(c);
                System.out.print(ins.getInstructionType() + "; ");
            }
            System.out.println();
        }

        return this.instructionStorage.getInstructions();
    }

    private void unknownToken(String identifiedToken, ArrayList<String> identifiedTokens, BlockInstruction bi) {
        if (this.instructionDetector.isNumber(identifiedToken) || this.instructionDetector.isArithmeticOperation(identifiedToken)) {
            if (identifiedTokens.contains("LOOP")) {
                LoopInstruction instruction = (LoopInstruction) bi.getInstructionGivenType("LOOP", false);
                instruction.setNumOfIteration(Integer.parseInt(identifiedToken));
            } else if (identifiedTokens.contains("PRINT")) {
                PrintInstruction instruction = (PrintInstruction) bi.getInstructionGivenType("PRINT", false);
                Variable var = instruction.getData();
                var.setValue(var.getValue() + identifiedToken);
                instruction.setData(var);
            }
        } else if (identifiedToken.contains("VARIABLE_NAME")) {
            String variableName = identifiedToken.split("=>")[1].trim();
        }
    }

    private String getArrLsElement(ArrayList<String> ls, String str) {
        String result = null;
        for (String token : ls)
            if (token.contains(str))
                return token;

        return result;
    }

    public void generateCode(String[] statements) {
        HashMap<Integer, BlockInstruction> tokenisedInstriction = this.lexicalAnalyser(statements);
        HashMap<Integer, ArrayList<String>> javaCode = this.codeGeneration(tokenisedInstriction);

        System.out.println("--- Java code is being generated ---");

        for (Integer instructionCounter : javaCode.keySet()) {
            ArrayList<String> instructionLs = javaCode.get(instructionCounter);
            for (String i : instructionLs) {
                System.out.println(i);
            }
        }
    }

    private HashMap<Integer, ArrayList<String>> codeGeneration(HashMap<Integer, BlockInstruction> tokenisedInstruction) {
        HashMap<Integer, ArrayList<String>> javaCode = new HashMap<>();
        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            BlockInstruction blockInstruction = tokenisedInstruction.get(instructionCounter);
            ArrayList<String> javaInstructions = new ArrayList<>();
            HashMap<Integer, Instruction> instructionMap = blockInstruction.getInstructionBlock();
            for (Integer counter : instructionMap.keySet()) { // 0 is the start of the block
                Instruction instruction = instructionMap.get(counter);
                switch (instruction.getInstructionType()) {
                    case InstructionSet.INPUT:
                        javaInstructions.add(instruction.generateCode());
                        break;
                    case InstructionSet.PRINT:
                        javaInstructions.add(instruction.generateCode());
                        break;
                    case InstructionSet.LOOP:
                        javaInstructions.add(instruction.generateCode());
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
