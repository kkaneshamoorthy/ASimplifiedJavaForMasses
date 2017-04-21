package Utility;

import Engine.InstructionSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static String getVariableName(String statement) {
        String variableName = "";
        Pattern p = Pattern.compile("\\$\\s*(\\w+)");
        Matcher m = p.matcher(statement);
        while (m.find()) {
            variableName = m.group(0);
        }

        return variableName;
    }

    public static boolean isFunction(String token) {
        if (token.contains("(") && token.contains(")"))
            return true;

        return false;
    }

    public static boolean isExpression(String token) {
        return new InstructionSet().getExpressionPredefinedKeyword().contains(token.toUpperCase()) ? true : false;
    }

    public static boolean isBoolean(String identifiedToken) {
        if (identifiedToken.equalsIgnoreCase("true") || identifiedToken.equalsIgnoreCase("false"))
            return true;

        return false;
    }

    public static boolean isNumber(String identifiedToken) {
        identifiedToken = identifiedToken.replace("\"", "");
        if (identifiedToken.startsWith("INT =>")) return true;
        try {
            Integer.parseInt(identifiedToken);
        } catch (NumberFormatException e) { return false; }

        return true;
    }

    public static boolean isOperation(char c) {
        if (c == '+' || c == '-' || c == '/' || c == '*' || c == '%')
            return true;

        return false;
    }

    public static boolean isBooleanOperation(String c) {
        if (c.equals("==") || c.equals("<=") || c.equals(">="))
            return true;

        return false;
    }

    public static String getVariableToBeAssigned(String tokens) {
        StringBuilder varName = new StringBuilder();
        for (char c : tokens.toCharArray()) {
            if (c == '=') return varName.toString();
            varName.append(c);
        }

        return null;
    }

    public static String getFunctionName(String value) {
        int startIndex = 0;
        int endIndex = value.indexOf("(");

        if (endIndex == -1) return null;

        return value.substring(startIndex, endIndex);
    }

    public static boolean isVariable(String expr) {
        if (expr.startsWith("$"))
            return true;
        return false;
    }

    public static boolean isNumberWithoutQuote(String identifiedToken) {
        if (identifiedToken.startsWith("INT =>")) return true;
        try {
            Integer.parseInt(identifiedToken);
        } catch (NumberFormatException e) { return false; }

        return true;
    }

    public static String getType(String value) {

        if (isNumberWithoutQuote(value)) return "int";
        if (isString(value)) return "String";
        if (isBoolean(value)) return "boolean";

        return "String";
    }

    public static boolean isString(String identifiedToken) {
        return identifiedToken.startsWith("\"");
    }

    public static String retrieveLoopIterateBy(String loopAnnotatedValue) {
        int start = loopAnnotatedValue.lastIndexOf("=>")+2;

        return loopAnnotatedValue.substring(start);
    }

    public static String getLoopNumIteration(String loopAnnotatedValue) {
        int end = loopAnnotatedValue.indexOf(InstructionSet.LOOP_INCREMENT);

        return loopAnnotatedValue.substring(0, end);
    }

    public static String retrieveData(String token) {
        if (token == null)
            return "ERROR";

        String variableValue = "";
        if (token.startsWith("INT =>"))
            return token.replace("INT =>", "").trim();
        else if (token.startsWith("STRING =>"))
            return token.replace("STRING =>", "").trim();
        else if (token.startsWith("EXPRESSION =>"))
            return token.replace("EXPRESSION =>", "").trim();
        else if (token.startsWith("FUNCTION_NAME =>"))
            return token.replace("FUNCTION_NAME =>", "").trim();
        else if (token.startsWith("ARITHMETIC_OPERATION =>"))
            return token.replace("ARITHMETIC_OPERATION =>", "").trim();
        else if (token.startsWith("VARIABLE_NAME =>"))
            return token.replace("VARIABLE_NAME =>", "").trim();

        return variableValue;
    }

}
