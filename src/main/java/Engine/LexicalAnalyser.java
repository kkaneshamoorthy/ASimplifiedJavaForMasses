package Engine;

import Memory.*;

import Instruction.Instruction;
import Instruction.Variable;
import Instruction.InputInstruction;
import Instruction.PrintInstruction;
import Instruction.LoopInstruction;
import Instruction.IfInstruction;
import Instruction.BlockInstruction;
import Instruction.AssignmentInstruction;
import Instruction.FunctionInstruction;
import Instruction.FunctionDispatchInstruction;

import java.util.ArrayList;
import java.util.HashMap;

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

//            System.out.print("LEXER: "); //TESTING PURPOSES
//            for (String t : identifiedTokens)
//                System.out.print(t + ", ");
//            System.out.println();

            String detectedInstruction = this.instructionDetector.detectInstruction(statement);
            System.out.println("Detected Instruction: " + detectedInstruction);
//            for (String token : identifiedTokens) {
//                token = token.toUpperCase();
                switch (detectedInstruction) {
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
                            String variableName = varName.replace("VARIABLE_NAME =>", "").trim();
                            Variable var = this.variableHolder.getVariableGivenScopeAndName(variableName, scope);
                            printInstruction.setData(new Variable(var.getName(), var.getValue(), var.getScope()));
                        }
                        break;
                    case "LOOP":
                        String previousInstructionID = previousInstruction.getInstructionID();
                        if (statement.startsWith("\t")) {
                            LoopInstruction loopInstruction = new LoopInstruction();
                            loopInstruction.setBody(new BlockInstruction());
                            loopInstruction.setNumOfIteration(variableisier(this.getExpression(identifiedTokens), previousInstructionID));
                            this.setBody(previousInstruction, loopInstruction);
                            previousInstruction = loopInstruction;
                        } else {
                            instructionCounter++;
                            previousInstruction = new LoopInstruction();
                            this.instructionStorage.addInstruction(instructionCounter, previousInstruction);
                        }
                        previousInstruction.setId(previousInstructionID);
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
                        unknownToken(detectedInstruction, identifiedTokens, previousInstruction);
                }
//            }
        }

        return this.instructionStorage.getInstructions();
    }

    private Variable variableisier(String expr, String scope) {
        if (this.isVariable(expr)) {
            return this.variableHolder.getVariableGivenScopeAndName(expr, scope);
        }

        Variable variable = new Variable(expr+scope, expr, scope);
        this.variableHolder.add(variable);
        return variable;
    }

    private boolean isVariable(String expr) {
        if (expr.startsWith("$"))
            return true;
        return false;
    }

    private AssignmentInstruction createAssignment(ArrayList<String> identifiedTokens, String scope) {
        String variableName = getVariableToBeAssigned(identifiedTokens);
        String variableValue = getAssignExpression(identifiedTokens, scope);

        if (this.variableHolder.hasVariable(variableName, scope)) {
            this.variableHolder.getVariableGivenScopeAndName(variableName, scope)
                    .setValue(variableValue);

            AssignmentInstruction assignmentInstruction = new AssignmentInstruction(
                    this.variableHolder.getVariableGivenScopeAndName(variableName, scope),
                    variableValue
            );

            assignmentInstruction.setDeclaration(false);
            return assignmentInstruction;
        }

        Variable assignedTo = createVariable(variableName, variableValue, scope);
        this.variableHolder.add(assignedTo);

        return new AssignmentInstruction(assignedTo, variableValue);
    }

    public String getVariableToBeAssigned(ArrayList<String> tokens) {
        for (String token : tokens) {
            if (token.startsWith("VARIABLE_NAME"))
                return token.replace("VARIABLE_NAME =>", "").trim();
        }

        return "";
    }

    public String getAssignExpression(ArrayList<String> tokens, String scope) {
        StringBuilder variableName = new StringBuilder();
        boolean visited = false;

        for (String token : tokens) {
            if (token.equals("ASSIGNMENT")) {
                visited = true;
                continue;
            }
            if (visited) {
                if (token.startsWith("VARIABLE_NAME =>")) {
                    String varN = token.replace("VARIABLE_NAME =>", "");
                    String val = this.variableHolder.getVariableGivenScopeAndName(varN, scope).getValue();
                    variableName.append(val);
                } else {
                    variableName.append(retrieveData(token));
                }
            }
        }

        return variableName.toString();
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
        else if (token.startsWith("VARIABLE_NAME =>"))
            variableValue = token.replace("VARIABLE_NAME =>", "").trim();

        return variableValue;
    }

    public Variable createVariable(String variableName, String variableValue, String scope) {
        Variable var = new Variable(variableName, variableValue, scope);
        boolean isSucessful = this.variableHolder.add(var);

        return (isSucessful) ? var : null;
    }

    private void unknownToken(String identifiedToken, ArrayList<String> identifiedTokens, Instruction previousInstruction) {
        if (this.instructionDetector.isNumber(identifiedToken)) {
            if (identifiedTokens.contains("LOOP")) {
                if (whichInstruction(previousInstruction).equals("LOOP")) {
                    LoopInstruction instruction = (LoopInstruction) previousInstruction;
                    instruction.setNumOfIteration(variableisier(identifiedToken.replace("INT =>", "").trim(), previousInstruction.getInstructionID()));
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
