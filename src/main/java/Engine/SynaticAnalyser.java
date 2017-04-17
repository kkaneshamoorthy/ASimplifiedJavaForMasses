package Engine;

import Instruction.*;
import Memory.FunctionStorage;
import Memory.VariableHolder;
import Utility.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SynaticAnalyser {
    private VariableHolder variableHolder;
    private FunctionStorage functionStorage;
    private InstructionDetector instructionDetector;

    private HashMap<String, ArrayList<Variable>> undefinedFunctionMap = new HashMap<>();

    public SynaticAnalyser() {
        this.variableHolder = new VariableHolder();
        this.functionStorage = new FunctionStorage();
        this.instructionDetector = new InstructionDetector(new InstructionSet());
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
        FunctionInstruction parentFunction = null;

        for (Integer instructionCounter : tokens.keySet()) {
            Pair instructionKeyPair = tokens.get(instructionCounter);
            String instruction = instructionKeyPair.getKey();
            String instructionValue = instructionKeyPair.getValue();

            switch (instruction) {
                case InstructionSet.FUNCTION:
                    String functionName = Helper.getFunctionName(instructionValue);
                    FunctionInstruction functionInstruction = new FunctionInstruction(functionName);
                    ArrayList<Variable> formalParameter = new ArrayList<Variable>();
                    ArrayList<Variable> actualParameter = new ArrayList<Variable>();
                    ArrayList<String> parameterList = this.getArgumentList(instructionValue);

                    for (String parameter : parameterList) {
                        //create variable and store it in the list

                        //TODO: do i need a parameter counter
                        Variable parameterVariable = new Variable(
                                parameter,
                                "",
                                functionInstruction.getInstructionID()
                        );

                        formalParameter.add(parameterVariable);
                        this.variableHolder.add(parameterVariable);
                    }

//                    if (this.undefinedFunctionMap.containsKey(functionName)) {
//                        for (int i=0; i<actualParameter.size(); i++) {
//                            formalParameter.get(i).setType(actualParameter.get(i).getValue());
//                        }
//
//                        this.undefinedFunctionMap.remove(functionName);
//                    }


                    functionInstruction.setParameter(formalParameter);

                    //TODO: call a method sort and link all the parameter variables through variableHolder
//                    this.resolveForwardReferences();

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
                    if (instructionValue.contains("$")) {
                        if (!Helper.isNumber(getValueFromFunctionDispatch(parentFunction, instructionValue))) {
                            this.setBody(parentInstruction, reportError(instructionCounter, "\"Loop iterator must be a number\""));
                            continue;
                        }
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
                    String varName = Helper.getVariableToBeAssigned(instructionValue);
                    Variable varToBeAssigned = this.variableHolder.getVariableGivenScopeAndName(varName, parentFunction.getInstructionID());
                    String value = this.getAssignmentExpression(instructionValue); //anything after the EQ = sign0
                    String varPreviousType = "";
                    String varPreviousValue = "";

                    boolean isDeclaration = false;
                    if (varToBeAssigned == null) {
                        isDeclaration = true;
                        varToBeAssigned = new Variable(varName, value, parentInstruction.getInstructionID());
                        this.variableHolder.add(varToBeAssigned);
                    } else {
                        varPreviousType = varToBeAssigned.getType();
                        varPreviousValue = varToBeAssigned.getValue();
                    }

                    String newValue = getExpression(value, parentFunction.getInstructionID());
                    varToBeAssigned.setValue(newValue);
                    AssignmentInstruction assignmentInstruction = new AssignmentInstruction(varToBeAssigned, newValue);
                    ArrayList<Variable> ls = getArgumentAsVariableList(newValue, parentFunction.getInstructionID());
                    assignmentInstruction.setExpression(ls);

                    assignmentInstruction.formatExpression();
                    if (!varPreviousValue.isEmpty()) {
                        String oldType = varPreviousType;
                        String newType = assignmentInstruction.getAssignedTo().getType();
                        System.out.println(oldType + " ===== " + newType);
                        if (!checkTypeCompatability(oldType, newType)) { //type is not same
                            System.out.println(varToBeAssigned.getValue() + " " + value);
                            setErrorMessage(parentInstruction, reportError(instructionCounter, "\"Incompatible type: can not assign " + newType + " to a variable of type " + oldType+ "\""));
                            continue;
                        }
                    }


                    assignmentInstruction.setDeclaration(isDeclaration);
                    this.setBody(parentInstruction, assignmentInstruction);
                    break;
                case InstructionSet.DISPATCH:
                    if (instructionValue.isEmpty()) {
                        this.setBody(parentInstruction, reportError(instructionCounter, "\"Detected an invalid function name\""));
                        continue;
                    }
                    functionName = Helper.getFunctionName(instructionValue);
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
                    FunctionInstruction funcInstr = this.functionStorage.get(functionName); //Check if function already defined

                    if (funcInstr == null) {
                        if (this.undefinedFunctionMap.containsKey(functionName)) {
                            //already defined in the undefined function
                        } else {
                            this.undefinedFunctionMap.put(functionName, argumentList);
                        }
                    } else {
                        if (funcInstr.getParameter().size() == argumentList.size()) {
                            this.setErrorMessage(parentInstruction, reportError(instructionCounter, "\"The actual and formal parameter does not match\""));
                        }
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

    public boolean checkTypeCompatability(String oldType, String newType) {
        if (oldType.equals(newType))
            return true;

        return false;
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

    private String getExpression(String expression, String scope) {
        StringBuilder sb = new StringBuilder();
        ArrayList<Variable> expressionData = this.getArgumentAsVariableList(expression, scope);
        for (Variable var : expressionData) {
            if (!var.getScope().equals("NONE") && !var.getScope().equals("OPERATION")) {
                sb.append(var.getName());
            } else sb.append(var.getValue());
        }

        return sb.toString();
    }

    private void loader(FunctionInstruction parentFunction) {
        ArrayList<Variable> argumentList = this.undefinedFunctionMap.get(parentFunction.getFunctionName());
        if (argumentList != null) {
            ArrayList<Variable> functionParameterList = this.functionStorage.get(parentFunction.getFunctionName()).getParameter();

            for (int i=0; i<functionParameterList.size(); i++) {
                functionParameterList.get(i).setValue(argumentList.get(i).getValue());
            }
        }
    }

    private String getValueFromFunctionDispatch(FunctionInstruction parentFunction, String variableName) {
        ArrayList<Variable> argumentList = this.undefinedFunctionMap.get(parentFunction.getFunctionName());
        if (argumentList != null) {
            ArrayList<Variable> functionParameterList = this.functionStorage.get(parentFunction.getFunctionName()).getParameter();

            for (int i=0; i<functionParameterList.size(); i++) {
                Variable variable = functionParameterList.get(i);
                if (variable.getName().equals(variableName)) {
                    return argumentList.get(i).getValue();
                }
            }
        }

        return "";
    }

    private void resolveForwardReferences() {
        for (String functionName : this.undefinedFunctionMap.keySet()) {
            setErrorMessage(this.functionStorage.get("main"), reportError(-1, "\"Call to undefined function. Make sure the function you called is defined:"+functionName+" \""));
            this.undefinedFunctionMap.remove(functionName);
        }
    }

    private void setErrorMessage(Instruction parentInstruction, ErrorMessage errorMessage) {
        this.setBody(parentInstruction, errorMessage);
    }

    private String getFunctionArgument(String value) {
        int startIndex = value.indexOf("(");
        int endIndex = value.indexOf(")");

        return value.substring(startIndex+1, endIndex);
    }

    private ArrayList<Variable> getArgumentAsVariableList(String value, String scope) {
        ArrayList<Variable> ls = new ArrayList<Variable>();

        StringBuilder expr = new StringBuilder();
        for (int i=0; i<value.length(); i++) {
            char c = value.charAt(i);

            if (i == value.length()-1) {
                if (Helper.isOperation(c)) {
                    ls.add(new Variable(c+"", c+"", "OPERATION")); //operation
                } else {
                    expr.append(c);

                    if (Helper.isVariable(expr.toString())) {
                        Variable var = this.variableHolder.getVariableGivenScopeAndName(expr.toString(), scope);
                        if (var != null) ls.add(var);
                        else {
                            //TODO: variable not defined
                            System.out.println("VARIABLE NOT DEFINED");
                        }
                    } else
                        ls.add(new Variable("", expr.toString(), "NONE")); //constant
                }
            } else if (Helper.isOperation(c)) {
                if (Helper.isVariable(expr.toString())) {
                    Variable var = this.variableHolder.getVariableGivenScopeAndName(expr.toString(), scope);
                    if (var != null) ls.add(var);
                    else {
                        //TODO: variable not defined
                        System.out.println("VARIABLE NOT DEFINED");
                    }
                } else
                    ls.add(new Variable("", expr.toString(), "NONE")); //constant

                ls.add(new Variable(c+"", c+"", "OPERATION")); //operation

                expr = new StringBuilder();
            } else expr.append(c);
        }

        return ls;
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

    private ErrorMessage reportError(Integer instructionCounter, String message) {
        return new ErrorMessage(new Variable(instructionCounter+"Err", message, message.hashCode()+""+instructionCounter));
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
