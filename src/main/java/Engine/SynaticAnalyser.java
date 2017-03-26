package Engine;

import Instruction.*;
import Memory.FunctionStorage;
import Memory.VariableHolder;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.HashMap;

public class SynaticAnalyser {
    private VariableHolder variableHolder;
    private FunctionStorage functionStorage;

    public SynaticAnalyser() {
        this.variableHolder = new VariableHolder();
        this.functionStorage = new FunctionStorage();
    }

    public HashMap<Integer, Instruction> generateInstructions(HashMap<Integer, Pair> tokens) {
        //Initialisation
        HashMap<Integer, Instruction> annotatedInstructions = new HashMap<Integer, Instruction>();
        Instruction parentInstruction = null;

        for (Integer instructionCounter : tokens.keySet()) {
            Pair instructionKeyPair = tokens.get(instructionCounter);
            String instruction = instructionKeyPair.getKey();
            String instructionValue = instructionKeyPair.getValue();

            switch (instruction) {
                case InstructionSet.FUNCTION:
                    FunctionInstruction functionInstruction = new FunctionInstruction(instructionValue.replace("(", "").replace(")", ""));
                    parentInstruction = functionInstruction;
                    this.functionStorage.add(functionInstruction);
                    annotatedInstructions.put(instructionCounter, functionInstruction);
                    break;
                case InstructionSet.PRINT:
                    break;
                case InstructionSet.LOOP:
                    break;
                case InstructionSet.VARIABLE:
                    break;
                case InstructionSet.IF:
                    break;
                case InstructionSet.ASSIGNMENT:
                    Variable assignedTo = this.variableisier(this.getVariableToBeAssigned(instructionValue), parentInstruction.getInstructionID());
                    AssignmentInstruction assignmentInstruction = new AssignmentInstruction(assignedTo, this.getAssignmentExpression(instructionValue));
                    this.setBody(parentInstruction, assignmentInstruction);
                    break;
                case InstructionSet.DISPATCH:
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
        for (int i=token.length()-1; i>=0; i--) {
            char c = token.charAt(i);

            if (c == '=') return expr.toString();
            expr.append(c);
        }

        return null;
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
