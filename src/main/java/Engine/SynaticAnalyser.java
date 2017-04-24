package Engine;

import GUI.Console;
import Instruction.*;
import Memory.*;
import Utility.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SynaticAnalyser {
    private VariableHolder variableHolder;
    private FunctionStorage functionStorage;
    private ScopeStack scopeStack;
    private Console console;

    private static String SYNTAX_ERROR = "Syntax error:";
    private static String LOOP_ERROR = "Error in Loop:";
    private static String METHOD_CALL_ERROR = "Error at Method call:";

    private HashMap<String, ArrayList<Variable>> undefinedFunctionMap = new HashMap<>();

    private SynaticAnalyser() {
        this.variableHolder = new VariableHolder();
        this.functionStorage = new FunctionStorage();
        this.scopeStack = new ScopeStack();
    }

    public SynaticAnalyser(Console console) {
        this();
        this.console = console;
    }

    public HashMap<Integer, Instruction> generateIntermediateRepresentation(HashMap<Integer, Pair> tokens) {
        HashMap<Integer, Instruction> intermediateRepresentation = new HashMap<Integer, Instruction>();

        Instruction parentInstruction = null;
        FunctionInstruction parentFunction = null;

        for (Integer instructionCounter : tokens.keySet()) {
            Pair instructionKeyPair = tokens.get(instructionCounter);
            String instruction = instructionKeyPair.getKey();
            String instructionValue = instructionKeyPair.getValue();

            switch (instruction) {
                case InstructionSet.FUNCTION:
                    //pops the current scope
                    if (!this.scopeStack.isEmpty())
                        this.scopeStack.pop();

                    String functionName = Helper.getFunctionName(instructionValue);
                    ArrayList<String> parameterList = this.getArgumentList(instructionValue);

                    if (functionName == null) {
                        this.reportError(SynaticAnalyser.SYNTAX_ERROR + "make sure the method is defined properly");
                        return null;
                    }

                    if (parameterList == null) {
                        this.reportError(SynaticAnalyser.SYNTAX_ERROR +  "make sure the method parameter are defined properly");
                        return null;
                    }

                    FunctionInstruction functionInstruction = new FunctionInstruction(functionName);
                    ArrayList<Variable> formalParameter = new ArrayList<Variable>();

                    for (String parameter : parameterList) {
                        Variable parameterVariable = new Variable(parameter, "", functionInstruction.getInstructionID());
                        formalParameter.add(parameterVariable);
                        this.variableHolder.add(parameterVariable);
                    }

                    if (this.undefinedFunctionMap.containsKey(functionName))
                        this.undefinedFunctionMap.remove(functionName);

                    functionInstruction.setParameter(formalParameter);
                    parentInstruction = functionInstruction;
                    parentFunction = functionInstruction;

                    //adds function to a storage -> that keeps track of it
                    boolean isAdded = this.functionStorage.add(functionInstruction);

                    //checks if function already defined
                    if (!isAdded) {
                        this.reportError("Function " + functionName + " already defined");
                        return null;
                    }

                    //push function to the stack -> becomes the current scope
                    scopeStack.push(new Scope(functionInstruction.getInstructionID(), functionInstruction));
                    intermediateRepresentation.put(instructionCounter, functionInstruction);
                    break;
                case InstructionSet.PRINT:
                    if (instructionValue.isEmpty()) {
                        this.reportError(SynaticAnalyser.SYNTAX_ERROR + "Enter a valid data to print");
                        return null;
                    }

                    PrintInstruction printInstruction = new PrintInstruction();
                    Variable data = this.variableHolder.getVariableGivenScopeAndName(instructionValue, parentInstruction.getInstructionID());
                    printInstruction.setData(new Variable(parentInstruction.getInstructionID()+"PRINT"+instructionCounter, instructionValue, parentInstruction.getInstructionID()));
                    this.setBody(parentInstruction, printInstruction);
                    break;
                case InstructionSet.LOOP:
                    if (!this.scopeStack.isEmpty())
                        parentInstruction = this.scopeStack.top().getScopeInstruction();

                    if (instructionValue.isEmpty()) {
                        this.reportError(SynaticAnalyser.LOOP_ERROR + "Please enter a valid number of iterations");
                        return null;
                    }

                    String iterateBy = "1";
                    String totalNumOfIteration = instructionValue;

                    if (instructionValue.contains(InstructionSet.LOOP_INCREMENT)) {
                        iterateBy = Helper.retrieveLoopIterateBy(instructionValue);
                        totalNumOfIteration = Helper.getLoopNumIteration(instructionValue);
                    }

                    LoopInstruction loopInstruction = new LoopInstruction();
                    loopInstruction.setBody(new BlockInstruction());
                    loopInstruction.setNumOfIteration(new Variable(parentInstruction.getInstructionID()+"LOOP"+instructionCounter, totalNumOfIteration, parentInstruction.getInstructionID()));
                    loopInstruction.setIterateBy(iterateBy);
                    this.setBody(parentInstruction, loopInstruction);
                    parentInstruction = loopInstruction;
                    this.scopeStack.push(new Scope(loopInstruction.getInstructionID(), loopInstruction));
                    break;
                case InstructionSet.IF:
                    boolean isElse = false;

                    if (instructionValue.contains(InstructionSet.ELSE)) {
                        this.endBody();
                        instructionValue = instructionValue.replace("ELSE ", "");
                        isElse = true;
                    }

                    if (!this.scopeStack.isEmpty())
                        parentInstruction = this.scopeStack.top().getScopeInstruction();

                    ArrayList<Variable> conditionAsList = retrieveExpressionAsVariable(instructionValue, parentFunction.getInstructionID());
                    IfInstruction ifInstruction = new IfInstruction();
                    ifInstruction.setConditionVar(conditionAsList);
                    ifInstruction.setCondition(instructionValue);
                    ifInstruction.setBody(new BlockInstruction());
                    ifInstruction.setPartOfElse(isElse);
                    this.setBody(parentInstruction, ifInstruction);
                    parentInstruction = ifInstruction;
                    this.scopeStack.push(new Scope(ifInstruction.getInstructionID(), ifInstruction));
                    break;
                case InstructionSet.ASSIGNMENT:
                    String varName = Helper.getVariableToBeAssigned(instructionValue);
                    Variable varToBeAssigned = this.variableHolder.getVariableGivenScopeAndName(varName, parentFunction.getInstructionID());
                    String value = this.getAssignmentExpression(instructionValue); //anything after the EQ = sign

                    if (value.isEmpty()) {
                        this.reportError("The variable is not being assigned any value");
                        return null;
                    }

                    boolean isDeclaration = false;
                    if (varToBeAssigned == null) {
                        isDeclaration = true;
                        varToBeAssigned = new Variable(varName, value, parentInstruction.getInstructionID());
                        this.variableHolder.add(varToBeAssigned);
                    }

                    String newValue = getExpression(value, parentFunction.getInstructionID());
                    varToBeAssigned.setValue(newValue);
                    AssignmentInstruction assignmentInstruction = new AssignmentInstruction(varToBeAssigned, newValue);
                    ArrayList<Variable> ls = getArgumentAsVariableList(newValue, parentFunction.getInstructionID());
                    assignmentInstruction.setExpression(ls);

                    assignmentInstruction.formatExpression();
                    assignmentInstruction.setDeclaration(isDeclaration);
                    this.setBody(parentInstruction, assignmentInstruction);
                    break;
                case InstructionSet.METHOD_CALL:
                    if (instructionValue.isEmpty()) {
                        this.reportError(SynaticAnalyser.METHOD_CALL_ERROR+"Detected an invalid function name");
                        return null;
                    }
                    functionName = Helper.getFunctionName(instructionValue);
                    if (functionName == null) {
                        this.reportError(SynaticAnalyser.METHOD_CALL_ERROR+"Detected an invalid function name");
                        return null;
                    }

                    String functionArgument = this.getFunctionArgument(instructionValue);
                    ArrayList<Variable> argumentList = getArgumentAsVariableList(functionArgument, parentFunction.getInstructionID());
                    if (argumentList == null) {
                        this.reportError(SynaticAnalyser.METHOD_CALL_ERROR+"Syntax error at function call. Make sure the method call arguments are defined properly");
                        return null;
                    }

                    FunctionDispatchInstruction functionDispatchInstruction = new FunctionDispatchInstruction(functionName);
                    this.setBody(parentInstruction, functionDispatchInstruction);
                    FunctionInstruction funcInstr = this.functionStorage.get(functionName); //Check if function already defined

                    if (funcInstr == null) {
                        if (!this.undefinedFunctionMap.containsKey(functionName)) {
                            this.undefinedFunctionMap.put(functionName, argumentList);
                        }
                    } else {
                        if (funcInstr.getParameter().size() == argumentList.size()) {
                            this.reportError(SynaticAnalyser.METHOD_CALL_ERROR+"The actual and formal parameter does not match");
                            return null;
                        }
                    }

                    for (Variable argument : argumentList) {
                        functionDispatchInstruction.addArgument(argument);
                    }

                    break;
                case InstructionSet.INPUT:
                    String variableName = Helper.getVariableToBeAssigned(instructionValue);
                    Variable assignedTo = this.variableHolder.getVariableGivenScopeAndName(variableName, parentInstruction.getInstructionID());

                    if (variableName == null) {
                        this.reportError("Define a variable to store data from user");
                        return null;
                    }

                    isDeclaration = false;
                    if (assignedTo == null) {
                        isDeclaration = true;
                        assignedTo = new Variable(variableName, "", parentInstruction.getInstructionID());
                        this.variableHolder.add(assignedTo);
                    }

                    InputInstruction inputInstruction = new InputInstruction();
                    inputInstruction.setAssignedTo(assignedTo);
                    inputInstruction.setVariableDeclaration(isDeclaration);
                    this.setBody(parentInstruction, inputInstruction);
                    break;
                case InstructionSet.LOOP_END:
                    this.endBody();
                    if (!this.scopeStack.isEmpty())
                            parentInstruction = this.scopeStack.top().getScopeInstruction();

                    break;
                case InstructionSet.ELSE:
                    this.endBody();
                    if (!this.scopeStack.isEmpty())
                        parentInstruction = this.scopeStack.top().getScopeInstruction();

                    ElseInstruction elseInstruction = new ElseInstruction();
                    elseInstruction.setBody(new BlockInstruction());
                    this.setBody(parentInstruction, elseInstruction);
                    parentInstruction = elseInstruction;
                    this.scopeStack.push(new Scope(elseInstruction.getInstructionID(), elseInstruction));

                    break;
            }
        }

        //Call to an undefined function
        if (this.undefinedFunctionMap.size() > 0) {
            this.reportError("Make the function you are calling is defined");

            return null;
        }

        //check if main method defined
        if (this.functionStorage.get("main") == null) {
            this.reportError("The \"main\" function is not defined");

            return null;
        }

        return intermediateRepresentation;
    }

    private void endBody() {
        if (!this.scopeStack.isEmpty())
            this.scopeStack.pop();
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

    //Gets method argument
    private String getFunctionArgument(String value) {
        int startIndex = value.indexOf("(");
        int endIndex = value.indexOf(")");

        return value.substring(startIndex+1, endIndex);
    }

    //returns a list of variable from a string expression
    private ArrayList<Variable> retrieveExpressionAsVariable(String value, String scope) {
        ArrayList<Variable> ls = new ArrayList<Variable>();
        StringBuilder expr = new StringBuilder();
        String operation = "";

        for (int i=0; i<value.length(); i++) {
            char c = value.charAt(i);

            if (c == '=' || c == '<' || c == '>') {
                operation +=c;
            } else {
                expr.append(c);
            }

            String expression = expr.toString();

            if (Helper.isBooleanOperation(operation)) {
                ls.add(new Variable(expression, expression, "NONE"));
                ls.add(new Variable(operation, operation, "OPERATION"));
                operation = "";
                expr = new StringBuilder();
                continue;
            }

            if (Helper.isVariable(expression) && !expression.equals("$")) {
                Variable variable = retrieveVariable(expression, scope);

                if (variable!=null) ls.add(variable);
                else System.out.println("Variable undefined");

                expr = new StringBuilder();
            } else if (Helper.isBooleanOperation(expression)) {
                ls.add(new Variable(expression, expression, "OPERATION"));
                expr = new StringBuilder();
            }
        }

        for (Variable v : ls) {
            System.out.println("Name:"+v.getName() + " Value:" + v.getValue() + " Scope " + v.getScope());
        }

        return ls;
    }

    private Variable retrieveVariable(String variableName, String scope) {
        return this.variableHolder.getVariableGivenScopeAndName(variableName, scope);
    }

    private ArrayList<Variable> getArgumentAsVariableList(String value, String scope) {
        ArrayList<Variable> ls = new ArrayList<Variable>();
        StringBuilder expr = new StringBuilder();
        for (int i=0; i<value.length(); i++) {
            char c = value.charAt(i);

            if (i == value.length()-1) {
                if (Helper.isOperation(c)) {
                    ls.add(new Variable(c+"", c+"", "OPERATION")); //operation
                } else if (expr.toString().equals("==")) {
                    ls.add(new Variable(expr.toString()+"", expr.toString()+"", "OPERATION"));
                    expr = new StringBuilder();
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
            } else if (expr.toString().equals("==")) {
                ls.add(new Variable(expr.toString()+"", expr.toString()+"", "OPERATION"));
                expr = new StringBuilder();
            } else expr.append(c);
        }

        for (Variable v : ls) {
            System.out.println("list:"+v.getValue() + " " + v.getName());
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

    private void reportError(String message) {
        this.console.reportError(message);
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
            case InstructionSet.ELSE:
                ElseInstruction elseInstruction = (ElseInstruction) parentInstruction;
                bi = elseInstruction.getBody();
                if (bi != null) bi.addInstructionToBlock(childInstruction);
                else elseInstruction.setBody(new BlockInstruction(childInstruction));
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
        else if (instruction instanceof  ElseInstruction)
            return InstructionSet.ELSE;
        else
            return "UNKOWN";
    }
}
