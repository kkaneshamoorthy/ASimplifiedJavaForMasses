package Engine;

import Memory.InstructionStorage;
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

            for (String token : identifiedTokens)
                System.out.print(token + " ; ");
            System.out.println();

            BlockInstruction bi = new BlockInstruction();
            for (String token : identifiedTokens) {
                switch (token) {
                    case "INPUT":
                        if (!statement.startsWith("\t")) {
                            System.out.println("reached INPUT NOT TAB");
                            instructionCounter++;
                            bi = new BlockInstruction();
                            bi.addInstructionToBlock(new Instruction("INPUT", "GET_INPUT"));
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        } else {
                            System.out.println("reached INPUT PRINT TAB");
                            bi = this.instructionStorage.getBlockInstruction(instructionCounter);
                            if (bi.getInstructionGivenType("PRINT") == null) {
                                bi.addInstructionToBlock(new Instruction("INPUT", "GET_INPUT"));
                                this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                            }
                        }
                        Instruction printInstruction = bi.getInstructionGivenType("PRINT");
                        if (printInstruction != null) {
                            System.out.println("PREV PRINT");
                            printInstruction.setAdditionalInfo("PRINT_VARIABLE => ");
//                            bi.getInstructionGivenType(token).setAdditionalInfo("PRINT_INPUT");
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        }
                        break;
                    case "PRINT":
                        System.out.println("reached PRINT");
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            bi = new BlockInstruction();
                            bi.addInstructionToBlock(new Instruction("PRINT"));
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        } else {
                            System.out.println("reached PRINT TAB");
                            bi = this.instructionStorage.getBlockInstruction(instructionCounter);
                            bi.addInstructionToBlock(new Instruction("PRINT"));
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        }
                        String str = getArrLsElement(identifiedTokens, "STRING");
                        if (str != null)
                            bi.getInstructionGivenType(token).setAdditionalInfo(str);
                        break;
                    case "LOOP":
                        System.out.println("reached LOOP");
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            bi = new BlockInstruction();
                            bi.addInstructionToBlock(new Instruction("LOOP"));
                            this.instructionStorage.addInstructionBlock(instructionCounter, bi);
                        } else {
                            bi = this.instructionStorage.getBlockInstruction(instructionCounter);
                            bi.addInstructionToBlock(new Instruction("LOOP"));
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
                System.out.print(ins.getInstructionType() + " , " + ins.getAdditionalInfo()+"; ");
            }
            System.out.println();
        }

        return this.instructionStorage.getInstructions();
    }

    private void unknownToken(String identifiedToken, ArrayList<String> identifiedTokens, BlockInstruction bi) {
        if (this.instructionDetector.isNumber(identifiedToken) || this.instructionDetector.isArithmeticOperation(identifiedToken)) {
            if (identifiedTokens.contains("LOOP")) {
                Instruction instruction = bi.getInstructionGivenType("LOOP", false);
                if (instruction != null)
                    if (instruction.isFullyDefined())
                        instruction.setAdditionalInfo(instruction.getAdditionalInfo()+identifiedToken);
                    else
                        instruction.setAdditionalInfo(identifiedToken);
            } else if (identifiedTokens.contains("PRINT")) {
                Instruction instruction = bi.getInstructionGivenType("PRINT", false);
                if (instruction != null)
                    if (instruction.isFullyDefined())
                        instruction.setAdditionalInfo(instruction.getAdditionalInfo()+identifiedToken);
                    else
                        instruction.setAdditionalInfo("INT => " + identifiedToken);
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
                        String additionalInfo = instruction.getAdditionalInfo();
                        if (additionalInfo.equals("GET_INPUT")) {
                            javaInstructions.add("Scanner s = new Scanner(System.in);");
                            javaInstructions.add("String tempVariableValue = s.nextLine();");
                        }
                        break;
                    case InstructionSet.PRINT:
                        String addInfo = instruction.getAdditionalInfo();
                        if (addInfo.contains("PRINT_VARIABLE")) {
                            javaInstructions.add("System.out.println(tempVariableValue);");
                        } else if (addInfo.contains("STRING =>")) {
                            javaInstructions.add("System.out.println("+addInfo.split("=>")[1].trim()+");");
                        } else if(addInfo.contains("INT => ")) {
                            javaInstructions.add("System.out.println("+addInfo.split("=>")[1].trim()+");");
                        } else {
                            System.out.println("NOTHING TO PRINT");
                        }
                        break;
                    case InstructionSet.LOOP:
                        String addIndo = instruction.getAdditionalInfo();
                        javaInstructions.add("for (int i=0; i<" +addIndo+"; i++) {");
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
