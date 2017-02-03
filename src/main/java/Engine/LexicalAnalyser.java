package Engine;

import Memory.InstructionStorage;
import Memory.JavaProgramTemplate;
import Memory.Variable;
import Memory.VariableHolder;

import Instruction.Instruction;
import Instruction.InputInstruction;
import Instruction.PrintInstruction;
import Instruction.LoopInstruction;
import Instruction.IfInstruction;
import Instruction.AssignmentInstruction;
import Instruction.FunctionInstruction;
import Utility.FileUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class LexicalAnalyser {
    private HashMap<Integer, HashMap<String, String>> instructions;
    private int instructionCounter = 0;
    private InstructionDetector instructionDetector;
    private InstructionSet instructionSet;
    private VariableHolder variableHolder;
    private InstructionStorage instructionStorage;

    public LexicalAnalyser() {
        this.instructionSet = new InstructionSet();
        this.instructions = new HashMap<Integer, HashMap<String, String>>();
        this.instructionDetector = new InstructionDetector(this.instructionSet);
        this.variableHolder = new VariableHolder();
        this.instructionStorage = new InstructionStorage();
    }

    public InstructionStorage getInstructionStorage() { return this.instructionStorage; }
    public VariableHolder getVariableHolder() { return this.variableHolder; }

    public void generateCode(HashMap<Integer, Instruction> tokenisedInstriction ) {
        HashMap<Integer, String> javaCode = this.codeGeneration(tokenisedInstriction);

        System.out.println("--- Java code is being generated ---");

        for (Integer instructionCounter : javaCode.keySet()) {
            String instruction = javaCode.get(instructionCounter);
            System.out.println(instruction);
        }

        FileUtility.saveJavaProgram(null, new JavaProgramTemplate(this.instructionStorage, this.variableHolder));

        System.out.println("--- Finished generating code ---");
    }

    public HashMap<Integer, Instruction> lexicalAnalyser(String[] statements) {
        int instructionCounter = -1;
        Instruction previousInstruction = null;
        for (String statement : statements) {
            ArrayList<String> identifiedTokens = this.instructionDetector.identifyTokens(statement);

            System.out.print("LEXER: "); //TESTING PURPOSES
            for (String t : identifiedTokens)
                System.out.print(t + " ");
            System.out.println();

            for (String token : identifiedTokens) {
                token = token.toUpperCase();
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
                        if (statement.startsWith("\t")) {
                            printInstruction = new PrintInstruction();
                            this.setBody(previousInstruction, printInstruction);
                        } else {
                            instructionCounter++;
                            printInstruction = new PrintInstruction();
                            this.instructionStorage.addInstruction(instructionCounter, printInstruction);
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
                        if (statement.startsWith("\t")) {
                            LoopInstruction loopInstruction = new LoopInstruction();
                            loopInstruction.setBody(new BlockInstruction());
                            this.setBody(previousInstruction, loopInstruction);
                            previousInstruction = loopInstruction;
                        } else {
                            instructionCounter++;
                            previousInstruction = new LoopInstruction();
                            this.instructionStorage.addInstruction(instructionCounter, previousInstruction);
                        }
                        break;
                    case "ASSIGNMENT":
                        instructionCounter++;
                        AssignmentInstruction assignmentInstruction = createAssignment(identifiedTokens);
                        this.instructionStorage.addInstruction(instructionCounter, assignmentInstruction);
                        break;
                    case "IF":
                        if (statement.startsWith("\t")) {
                            IfInstruction ifInstruction = new IfInstruction();
                            ifInstruction.setBody(new BlockInstruction());
                            this.setBody(previousInstruction, ifInstruction);
                            previousInstruction = ifInstruction;
                        } else {
                            instructionCounter++;
                            previousInstruction = new IfInstruction();
                            this.instructionStorage.addInstruction(instructionCounter, previousInstruction);
                        }
                        break;
                    case "FUNCTION":
                        if (identifiedTokens.contains("main")) {
                            FunctionInstruction functionInstruction = new FunctionInstruction("main");
                            previousInstruction = functionInstruction;
                            this.instructionStorage.addInstruction(instructionCounter, functionInstruction);
                        }
                        break;
                    default:
                        unknownToken(token, identifiedTokens, previousInstruction);
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
                if (whichInstruction(generalInstruction).equals("FUNCTION")) {

                } else if (whichInstruction(generalInstruction).equals("LOOP")) {
                    LoopInstruction instruction = (LoopInstruction) generalInstruction;
                    instruction.setNumOfIteration(Integer.parseInt(identifiedToken.replace("INT =>", "").trim()));
                }
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

    private void setBody(Instruction previousInstruction, Instruction instruction) {
        BlockInstruction bi = null;

        switch (whichInstruction(previousInstruction)) {
            case InstructionSet.FUNCTION:
                FunctionInstruction functionInstruction = (FunctionInstruction) previousInstruction;
                bi = functionInstruction.getBody();
                if (bi != null) bi.addInstructionToBlock(instruction);
                else  functionInstruction.setBody(new BlockInstruction(instruction));
                break;
            case InstructionSet.LOOP:
                LoopInstruction loopInstruction = (LoopInstruction) previousInstruction;
                bi = loopInstruction.getBody();
                if (bi != null) bi.addInstructionToBlock(instruction);
                else loopInstruction.setBody(new BlockInstruction(instruction));
                break;
            case InstructionSet.IF:
                IfInstruction ifInstruction = (IfInstruction) previousInstruction;
                bi = ifInstruction.getBody();
                if (bi != null) bi.addInstructionToBlock(instruction);
                else ifInstruction.setBody(new BlockInstruction(instruction));
                break;
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
        else if (generalInstruction instanceof FunctionInstruction)
            return InstructionSet.FUNCTION;
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

    public HashMap<Integer, String> codeGeneration(HashMap<Integer, Instruction> tokenisedInstruction) {
        HashMap<Integer, String> javaCode = new HashMap<>();

        for (Integer instructionCounter : tokenisedInstruction.keySet()) {
            Instruction instruction = tokenisedInstruction.get(instructionCounter);
            javaCode.put(instructionCounter, instruction.generateCode());
        }

        return javaCode;
    }

    private void codeExecution(HashMap<Integer, HashMap<String, String>> tokenisedInstruction) {
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
}
