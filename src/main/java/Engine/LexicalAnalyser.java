package Engine;

import Memory.*;

import Instruction.Instruction;
import Instruction.InputInstruction;
import Instruction.PrintInstruction;
import Instruction.LoopInstruction;
import Instruction.IfInstruction;
import Instruction.AssignmentInstruction;
import Instruction.FunctionInstruction;
import Instruction.FunctionDispatchInstruction;
import Utility.FileUtility;
import org.reactfx.value.Var;

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
    private FunctionStorage functionStorage;

    public LexicalAnalyser() {
        this.instructionSet = new InstructionSet();
        this.instructions = new HashMap<Integer, HashMap<String, String>>();
        this.instructionDetector = new InstructionDetector(this.instructionSet);
        this.variableHolder = new VariableHolder();
        this.instructionStorage = new InstructionStorage();
        this.functionStorage = new FunctionStorage();
    }

    public InstructionStorage getInstructionStorage() { return this.instructionStorage; }
    public VariableHolder getVariableHolder() { return this.variableHolder; }

    public HashMap<Integer, Instruction> lexicalAnalyser(String[] statements) {
        int instructionCounter = -1;
        Instruction previousInstruction = null;
        for (String statement : statements) {
            ArrayList<String> identifiedTokens = this.instructionDetector.identifyTokens(statement);

            System.out.print("LEXER: "); //TESTING PURPOSES
            for (String t : identifiedTokens)
                System.out.print(t + ", ");
            System.out.println();

            for (String token : identifiedTokens) {
                token = token.toUpperCase();
                switch (token) {
                    case "INPUT":
                        if (!statement.startsWith("\t")) {
                            instructionCounter++;
                            InputInstruction inputInstruction = new InputInstruction();
                            Variable var = createVariable("userData", "", previousInstruction.getInstructionID());
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
                        String varName = getArrLsElement(identifiedTokens, "VARIABLE_NAME");
                        String scope = previousInstruction.getInstructionID();
                        if (str != null && str.startsWith("STRING")) {
                            Variable var = createVariable("str", str.replace("STRING =>", "").trim(), scope);
                            printInstruction.setData(var);
                        } else if (intStr != null && intStr.startsWith("INT")) {
                            Variable var = createVariable("intVar", intStr.replace("INT =>", "").trim(), scope);
                            printInstruction.setData(var);
                        } else if (varName != null && varName.startsWith("VARIABLE_NAME")) {
                            String variableName = varName.replace("VARIABLE_NAME =>", "").replace("$", "").trim();
                            System.out.println(variableName + " " + scope);
                            Variable var = this.variableHolder.getVariableGivenScopeAndName(variableName, scope);
                            System.out.println("VARIABLE NAME ----- " + var);
                            printInstruction.setData(new Variable(var.getName(), var.getValue(), var.getScope()));
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
                        if (statement.startsWith("\t")) {
                            AssignmentInstruction assignmentInstruction = createAssignment(identifiedTokens, previousInstruction.getInstructionID());
                            if (assignmentInstruction == null) continue;
                            this.setBody(previousInstruction, assignmentInstruction);
//                            instructionCounter++;
//                            this.instructionStorage.addInstruction(instructionCounter, assignmentInstruction);
                        }
                        break;
                    case "IF":
                        if (statement.startsWith("\t")) {
                            IfInstruction ifInstruction = new IfInstruction();
                            ifInstruction.setCondition(getExpression(identifiedTokens));
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
                        //TODO: add function parameters to the variable holder with function name being the scope
                        String functionName = getArrLsElement(identifiedTokens, "FUNCTION_NAME");
                        if (functionName != null) {
                            functionName = this.retrieveData(functionName).replace("()", "").trim();
                            FunctionInstruction functionInstruction = new FunctionInstruction(functionName);
                            previousInstruction = functionInstruction;
                            this.instructionStorage.addInstruction(instructionCounter, functionInstruction);
                            this.functionStorage.add(functionInstruction);
                            instructionCounter++;
                        }
                        break;
                    case "CALL":
                        functionName = retrieveData(getArrLsElement(identifiedTokens, "FUNCTION_NAME =>")).replace("(", "").replace(")", "");
                        FunctionDispatchInstruction functionDispatchInstruction = new FunctionDispatchInstruction(functionName);
                        setBody(previousInstruction, functionDispatchInstruction);
                        break;
                    case "ELSE":
                        break;
                    default:
                        unknownToken(token, identifiedTokens, previousInstruction);
                }
            }
        }

        return this.instructionStorage.getInstructions();
    }

    private AssignmentInstruction createAssignment(ArrayList<String> identifiedTokens, String scope) {
        String variableName = "";
        String variableValue = "";
        System.out.println(identifiedTokens);
        for (int i=0; i<identifiedTokens.size(); i++) {
            String token = identifiedTokens.get(i);
            if (token.startsWith("VARIABLE_NAME"))
                variableName = token.replace("VARIABLE_NAME =>", "").replace("$", "").trim();
            else
                variableValue += retrieveData(token);
        }

        System.out.println("VARIABLE VALUE: " + variableValue);

        if (this.variableHolder.hasVariable(variableName, scope)) {
            this.variableHolder.getVariableGivenScopeAndName(variableName, scope)
                    .setValue(variableValue);

            return null;
        }

        Variable assignedTo = createVariable(variableName, variableValue, scope);

        return new AssignmentInstruction(assignedTo, variableValue);
    }

    private String getExpression(ArrayList<String> tokens) {
        StringBuilder exp = new StringBuilder();
        for (String token: tokens)
            exp.append(retrieveData(token));

        return exp.toString();
    }

    private String retrieveData(String token) {
        String variableValue = "";
        if (token.startsWith("INT =>"))
            variableValue = token.replace("INT =>", "").trim();
        else if (token.startsWith("STRING =>"))
            variableValue = token.replace("STRING =>", "").trim();
        else if (token.startsWith("EXPRESSION =>"))
            variableValue = token.replace("EXPRESSION =>", "").trim();
        else if (token.startsWith("FUNCTION_NAME =>"))
            variableValue = token.replace("FUNCTION_NAME =>", "").trim();
        else if (token.startsWith("ARITHMETIC_OPERATION =>"))
            variableValue = token.replace("ARITHMETIC_OPERATION =>", "").trim();

        return variableValue;
    }

    private Variable createVariable(String variableName, String variableValue, String scope) {
        Variable var = new Variable(variableName, variableValue, scope);
        boolean isSucessful = this.variableHolder.add(var);

        return (isSucessful) ? var : null;
    }

    private void unknownToken(String identifiedToken, ArrayList<String> identifiedTokens, Instruction previousInstruction) {
        if (this.instructionDetector.isNumber(identifiedToken) || this.instructionDetector.isArithmeticOperation(identifiedToken)) {
            if (identifiedTokens.contains("LOOP")) {
                if (whichInstruction(previousInstruction).equals("LOOP")) {
                    LoopInstruction instruction = (LoopInstruction) previousInstruction;
                    instruction.setNumOfIteration(Integer.parseInt(identifiedToken.replace("INT =>", "").trim()));
                }
            }
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
}
