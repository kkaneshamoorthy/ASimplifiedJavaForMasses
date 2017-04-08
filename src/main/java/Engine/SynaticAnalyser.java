package Engine;

import Instruction.*;
import Memory.FunctionStorage;
import Memory.VariableHolder;

import java.util.HashMap;

public class SynaticAnalyser {
    private VariableHolder variableHolder;
    private FunctionStorage functionStorage;

    public SynaticAnalyser() {
        this.variableHolder = new VariableHolder();
        this.functionStorage = new FunctionStorage();
    }

    public FunctionStorage getFunctionStorage() {
        return this.functionStorage;
    }

    public VariableHolder getVariableHolder() {
        return this.variableHolder;
    }

    public HashMap<Integer, Instruction> generateInstructions(HashMap<Integer, Pair> tokens) {
        //Initialisation
        HashMap<Integer, Instruction> annotatedInstructions = new HashMap<Integer, Instruction>();
        Instruction parentInstruction = null;
        Instruction parsentFunction = null;

        for (Integer instructionCounter : tokens.keySet()) {
            Pair instructionKeyPair = tokens.get(instructionCounter);
            String instruction = instructionKeyPair.getKey();
            String instructionValue = instructionKeyPair.getValue();

            switch (instruction) {
                case InstructionSet.FUNCTION:
                    FunctionInstruction functionInstruction = new FunctionInstruction(instructionValue.replace("(", "").replace(")", ""));
                    parentInstruction = functionInstruction;
                    parsentFunction = functionInstruction;
                    this.functionStorage.add(functionInstruction);
                    annotatedInstructions.put(instructionCounter, functionInstruction);
                    break;
                case InstructionSet.PRINT:
                    if (instructionValue.isEmpty()) {
                        this.setBody(parentInstruction, reportError(instructionCounter, "\"Please enter a valid data to print\""));
                        continue;
                    }
                    PrintInstruction printInstruction = new PrintInstruction();
                    printInstruction.setData(new Variable(parentInstruction.getInstructionID()+"PRINT"+instructionCounter, instructionValue, parentInstruction.getInstructionID()));
                    this.setBody(parentInstruction, printInstruction);
                    break;
                case InstructionSet.LOOP: //TODO: once finished executing loop - change scope to function
                    if (instructionValue.isEmpty()) {
                        this.setBody(parentInstruction, reportError(instructionCounter, "\"Please enter a valid number of iterations\""));
                        continue;
                    }
                    LoopInstruction loopInstruction = new LoopInstruction();
                    loopInstruction.setBody(new BlockInstruction());
                    loopInstruction.setNumOfIteration(new Variable(parentInstruction.getInstructionID()+"LOOP"+instructionCounter, instructionValue, parentInstruction.getInstructionID()));
                    this.setBody(parentInstruction, loopInstruction);
                    parentInstruction = loopInstruction;
                    break;
                case InstructionSet.IF:
                    IfInstruction ifInstruction = new IfInstruction();
                    ifInstruction.setCondition(instructionValue);
                    ifInstruction.setBody(new BlockInstruction());
                    this.setBody(parentInstruction, ifInstruction);
                    parentInstruction = ifInstruction;
                    break;
                case InstructionSet.ASSIGNMENT:
                    String varName = getVariableToBeAssigned(instructionValue);
                    Variable varToBeAssigned = this.variableHolder.getVariableGivenScopeAndName(varName, parsentFunction.getInstructionID());
                    if (varToBeAssigned == null) varToBeAssigned = this.variableHolder.getVariableGivenScopeAndName(varName, parentInstruction.getInstructionID());

                    boolean isDeclaration = false;
                    if (varToBeAssigned == null) {
                        isDeclaration = true;
                        varToBeAssigned = new Variable(varName, varName, parentInstruction.getInstructionID());
                        this.variableHolder.add(varToBeAssigned);
                    }


                    String value = this.getAssignmentExpression(instructionValue);
                    varToBeAssigned.setValue(value);
                    AssignmentInstruction assignmentInstruction = new AssignmentInstruction(varToBeAssigned, this.getAssignmentExpression(instructionValue));
                    assignmentInstruction.setDeclaration(isDeclaration);
                    this.setBody(parentInstruction, assignmentInstruction);
                    break;
                case InstructionSet.DISPATCH:
                    if (instructionValue.isEmpty()) {
                        this.setBody(parentInstruction, reportError(instructionCounter, "\"Detected an invalid function name\""));
                        continue;
                    }
                    FunctionDispatchInstruction functionDispatchInstruction = new FunctionDispatchInstruction(instructionValue.replace("(","").replace(")",""));
                    this.setBody(parentInstruction, functionDispatchInstruction);
                    break;
                case InstructionSet.INPUT:
                    break;
            }
        }


        CodeGeneration codeGeneration = new CodeGeneration();
        HashMap<Integer, String> ls = codeGeneration.generateJavaCode(annotatedInstructions);

        for (Integer key : ls.keySet()) {
            System.out.println(ls.get(key));
        }

        return annotatedInstructions;
    }

    private ErrorMessage reportError(Integer instructionCounter, String message) {
        return new ErrorMessage(new Variable(instructionCounter+"Err", message, message.hashCode()+""+instructionCounter));
    }

    private Variable variableisier(String varName, String scope) {
        if (this.variableHolder.hasVariable(varName, scope)) {
            return this.variableHolder.getVariableGivenScopeAndName(varName, scope);
        }

        Variable variable = new Variable(varName, varName, scope);
        this.variableHolder.add(variable);
        return variable;
    }

    private boolean isVariable(String expr) {
        if (expr.startsWith("$"))
            return true;
        return false;
    }

    public String getVariableToBeAssigned(String tokens) {
        StringBuilder varName = new StringBuilder();
        for (char c : tokens.toCharArray()) {
            if (c == '=') return varName.toString();
            varName.append(c);
        }

        return null;
    }

    public String getAssignmentExpression(String token) {
        StringBuilder expr = new StringBuilder();
        boolean isValue = false;
        for (int i=0; i<=token.length()-1; i++) {
            char c = token.charAt(i);

            if (isValue) expr.append(c);
            if (c == '=') isValue = true;
        }

        return expr.toString();
    }

    private void setBody(Instruction parentInstruction, Instruction childInstruction) {
        BlockInstruction bi = null;
        switch (whichInstruction(parentInstruction)) {
            case InstructionSet.FUNCTION:
                FunctionInstruction functionInstruction = (FunctionInstruction) parentInstruction;
                bi = functionInstruction.getBody();
                if (bi != null) bi.addInstructionToBlock(childInstruction);
                else  functionInstruction.setBody(new BlockInstruction(childInstruction));
                break;
            case InstructionSet.LOOP:
                LoopInstruction loopInstruction = (LoopInstruction) parentInstruction;
                bi = loopInstruction.getBody();
                if (bi != null) bi.addInstructionToBlock(childInstruction);
                else loopInstruction.setBody(new BlockInstruction(childInstruction));
                break;
            case InstructionSet.IF:
                IfInstruction ifInstruction = (IfInstruction) parentInstruction;
                bi = ifInstruction.getBody();
                if (bi != null) bi.addInstructionToBlock(childInstruction);
                else ifInstruction.setBody(new BlockInstruction(childInstruction));
                break;
        }
    }

    private String whichInstruction(Instruction instruction) {
        if (instruction instanceof LoopInstruction)
            return InstructionSet.LOOP;
        else if (instruction instanceof PrintInstruction)
            return InstructionSet.PRINT;
        else if (instruction instanceof IfInstruction)
            return InstructionSet.IF;
        else if (instruction instanceof InputInstruction)
            return InstructionSet.INPUT;
        else if (instruction instanceof FunctionInstruction)
            return InstructionSet.FUNCTION;
        else
            return "UNKOWN";
    }
}
