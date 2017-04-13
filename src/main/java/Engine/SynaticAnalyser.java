package Engine;

import Instruction.*;
import Memory.FunctionStorage;
import Memory.VariableHolder;
import org.reactfx.value.Var;

import java.util.ArrayList;
import java.util.Arrays;
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
        Instruction parentFunction = null;

        for (Integer instructionCounter : tokens.keySet()) {
            Pair instructionKeyPair = tokens.get(instructionCounter);
            String instruction = instructionKeyPair.getKey();
            String instructionValue = instructionKeyPair.getValue();

            switch (instruction) {
                case InstructionSet.FUNCTION:
                    String functionName = this.getFunctionName(instructionValue);
                    String functionArgName = "UNDEFINED";
                    FunctionInstruction functionInstruction = this.functionStorage.get("UNDEFINED");

                    if (functionInstruction == null) {
                        functionInstruction = new FunctionInstruction(functionName);
                        functionArgName = functionName;
                    } else {
                        functionInstruction.setFunctionName(functionName);
                    }

                    System.out.println("PARAMETER:"+instructionValue);

                    ArrayList<String> parameterList = this.getArgumentList(instructionValue);
                    ArrayList<Variable> parameterVariableList = new ArrayList<Variable>();
                    int parameterCounter = 0;
                    for (String parameter : parameterList) {
                        System.out.println(functionArgName+"=>"+parameterCounter+": "+parameter);
                        Variable argumentVariable = this.variableHolder.getVariableGivenScopeAndName(
                                functionArgName+"=>"+parameterCounter,
                                functionInstruction.getInstructionID()
                        );

                        //TODO: if no pass method passes anything, meaning no one calls, then argument variable is empty value
                        if (argumentVariable == null) {
                            argumentVariable = new Variable(parameter, "", functionInstruction.getInstructionID());
                        } else {
                            argumentVariable.setName(parameter);
                        }

                        parameterVariableList.add(argumentVariable);
                        parameterCounter++;
                    }

                    functionInstruction.setParameter(parameterVariableList);

                    parentInstruction = functionInstruction;
                    parentFunction = functionInstruction;
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
                    System.out.println("NAME"+varName);
                    Variable varToBeAssigned = this.variableHolder.getVariableGivenScopeAndName(varName, parentFunction.getInstructionID());

                    boolean isDeclaration = false;
                    if (varToBeAssigned == null) {
                        isDeclaration = true;
                        varToBeAssigned = new Variable(varName, varName, parentInstruction.getInstructionID());
                        this.variableHolder.add(varToBeAssigned);
                    }

                    String value = this.getAssignmentExpression(instructionValue); //anything after the EQ = sign0
                    AssignmentInstruction assignmentInstruction = new AssignmentInstruction(varToBeAssigned, value);
                    ArrayList<Variable> ls = getArgumentAsVariableList(value, parentFunction.getInstructionID());
                    assignmentInstruction.setExpression(ls);

                    assignmentInstruction.setDeclaration(isDeclaration);
                    this.setBody(parentInstruction, assignmentInstruction);
                    break;
                case InstructionSet.DISPATCH:
                    if (instructionValue.isEmpty()) {
                        this.setBody(parentInstruction, reportError(instructionCounter, "\"Detected an invalid function name\""));
                        continue;
                    }
                    functionName = getFunctionName(instructionValue);
                    if (functionName == null) {
                        this.setBody(parentInstruction, reportError(instructionCounter, "\"Detected an invalid function name\""));
                        continue;
                    }

                    String functionArgument = this.getFunctionArgument(instructionValue);
                    ArrayList<Variable> argumentList = getArgumentAsVariableList(functionArgument, parentFunction.getInstructionID());
                    if (argumentList == null) {
                        this.setBody(parentInstruction, reportError(instructionCounter, "\"Syntax error at function call\""));
                        continue;
                    }

                    FunctionDispatchInstruction functionDispatchInstruction = new FunctionDispatchInstruction(functionName);
                    this.setBody(parentInstruction, functionDispatchInstruction);
                    FunctionInstruction funcInstr = this.functionStorage.get(functionName);

                    if (funcInstr == null) {
                        funcInstr = new FunctionInstruction("UNDEFINED"); //TODO: make the functionName unique
                        this.functionStorage.add(funcInstr);
                    }

                    for (Variable argument : argumentList) {
                        functionDispatchInstruction.addArgument(argument);
                    }

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

    private String getFunctionArgument(String value) {
        int startIndex = value.indexOf("(");
        int endIndex = value.indexOf(")");

        return value.substring(startIndex+1, endIndex);
    }


//    write a function called main():
//    print what 345 + 345 is to the console
//    call printStar(12)
//
//    write a function called printStar($num):
//    store $star = '*'
//    write a loop to go through $num times:
//    and then print $star
//    $star = $star+"*"


    private ArrayList<Variable> getArgumentAsVariableList(String value, String scope) {
        ArrayList<Variable> ls = new ArrayList<Variable>();

        StringBuilder expr = new StringBuilder();
        for (int i=0; i<value.length(); i++) {
            char c = value.charAt(i);

            System.out.println(c + " " + expr.toString() + " " + value + " " + i + " " + value.length());

            if (i == value.length()-1) {
                if (isOperation(c)) {
                    ls.add(new Variable(c+"", c+"", "OPERATION")); //operation
                } else {
                    expr.append(c);

                    if (isVariable(expr.toString())) {
                        Variable var = this.variableHolder.getVariableGivenScopeAndName(expr.toString(), scope);
                        if (var != null) ls.add(var);
                        else {
                            //TODO: variable not defined
                        }
                    } else
                        ls.add(new Variable("", expr.toString(), "NONE")); //constant
                }
            } else if (isOperation(c)) {
                ls.add(new Variable(c+"", c+"", "OPERATION")); //operation
                expr.append(c);
                if (isVariable(expr.toString())) {
                    Variable var = this.variableHolder.getVariableGivenScopeAndName(expr.toString(), scope);
                    if (var != null) ls.add(var);
                    else {
                        //TODO: variable not defined
                    }
                } else
                    ls.add(new Variable("", expr.toString(), "NONE")); //constant

                expr = new StringBuilder();
            } else expr.append(c);
        }

        return ls;
    }

    private boolean isOperation(char c) {
        if (c == '+' || c == '-' || c == '/' || c == '*' || c == '%')
            return true;

        return false;
    }

    private ArrayList<String> getArgumentList(String value) {
        ArrayList<String> argumentList = new ArrayList<>();
        if (!value.contains("(")) return null;

        int startIndex = value.indexOf("(")+1;
        int endIndex = value.indexOf(")");
        String args = value.substring(startIndex, endIndex).trim();
        if (args.isEmpty()) return argumentList;
        String[] arguments = args.split(",");
        argumentList.addAll(Arrays.asList(arguments));

        return argumentList;
    }

    private String getFunctionName(String value) {
        int startIndex = 0;
        int endIndex = value.indexOf("(");

        if (endIndex == -1) return null;

        return value.substring(startIndex, endIndex);
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
