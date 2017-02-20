package Engine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionDetector {

    public final String UNKNOWN = "UNKNOWN";

    private InstructionSet instructionSet;

    public InstructionDetector(InstructionSet instructionSet) {
        this.instructionSet = instructionSet;
    }

    public String detectInstruction(String statement) {
        String[] words = statement.toUpperCase().split(" ");

        HashMap<String, Integer> functionDetection = new HashMap<String, Integer>();
        int highestSoFar = 0;
        String mostLikelyFunction = "";
        for (int i=0; i<words.length; i++) {
            String key = words[i];
            HashMap<String, ArrayList<String>> instructionsWithKeyword = this.instructionSet.getInstructionSet();

            for (String function : instructionsWithKeyword.keySet()) {
                int functionScore = -1;
                ArrayList<String> functionKeywords = instructionsWithKeyword.get(function);
                for (String keyword : functionKeywords) {
                    if (keyword.equals(key)) {
                        if (functionDetection.containsKey(function)) {
                            functionScore = functionDetection.get(function)+1;
                            functionDetection.put(function, functionScore);
                        } else {
                            functionDetection.put(function, 1);
                            functionScore = 1;
                        }

                        if (highestSoFar < functionScore) {
                            highestSoFar = functionScore;
                            mostLikelyFunction = function;
                        }
                    }
                }
            }
        }

        return mostLikelyFunction;
    }

    public ArrayList<String> identifyToken(String[] tokens) {
        ArrayList<String> identifiedToken = new ArrayList<>();

        for (int i=0; i<tokens.length; i++) {
            String token = tokens[i];
            if (token.contains("\"")) {
                String temp = token;
                int j = i+1;
                for (; j<tokens.length; j++) {
                    if (!tokens[j].contains("\""))
                        temp += " "+tokens[j];
                    else {
                        temp += " "+tokens[j];
                        break;
                    }
                }
                i = j;
                identifiedToken.add("STRING => " + temp);
            }
            identifiedToken = this.identifyToken(token, identifiedToken);
        }

        return identifiedToken;
    }

    public ArrayList<String> identifyToken(String token, ArrayList<String> identifiedTokens) {
        HashMap<String, String> instructionMap = this.instructionSet.getInstructionMap();
        if (instructionMap.containsKey(token.toUpperCase()))
            identifiedTokens.add(instructionMap.get(token.toUpperCase()));
        else if (this.isNumber(token))
            identifiedTokens.add("INT => " + token);
        else
            for (char c : token.toCharArray()) {
                String identifiedToken = this.identifyToken(c);
                if (identifiedToken.equals("$")) {
                    String varIns = getVariableName(token).replace("$", "");
                    identifiedTokens.add("VARIABLE_NAME => " + varIns);
                    continue;
                }

                identifiedTokens.add(identifiedToken);
            }

        return identifiedTokens;
    }

    public String getVariableName(String statement) {
        String variableName = "";
        Pattern p = Pattern.compile("\\$\\s*(\\w+)");
        Matcher m = p.matcher(statement);
        while (m.find()) {
            variableName = m.group(0);
        }

        return variableName;
    }

    public String identifyToken(char charToken) {
        String strToken = charToken+"";
        HashMap<String, String> instructionMap = this.instructionSet.getInstructionMap();
        if (instructionMap.containsKey(strToken))
            return instructionMap.get(strToken);
        else if (this.isNumber(strToken))
            return strToken;

        return this.UNKNOWN;
    }

    public ArrayList<String> identifyTokens(String tokens) {
        ArrayList<String> identifiedTokens = this.computeRegex(tokens);

        for (int i=0; i<identifiedTokens.size(); i++) {
            String identifiedToken = identifiedTokens.get(i);

            if (isNumber(identifiedToken))
                identifiedTokens.set(i, "INT => " + identifiedToken);
            else if(isString(identifiedToken))
                identifiedTokens.set(i, "STRING => " + identifiedToken);
            else if (identifiedToken.startsWith("$"))
                identifiedTokens.set(i, "VARIABLE_NAME => " + identifiedToken);
            else if (isExpression(identifiedToken))
                identifiedTokens.set(i, "EXPRESSION =>" + identifiedToken);
            else if (this.instructionSet.getInstructionMap().containsKey(identifiedToken.toUpperCase()))
                identifiedTokens.set(i, this.instructionSet.getInstructionMap().get(identifiedToken.toUpperCase()));
        }

        return identifiedTokens;
    }

    private boolean isExpression(String token) {
        return this.instructionSet.getExpressionKeyword().contains(token.toUpperCase()) ? true : false;
    }

    public boolean isBoolean(String identifiedToken) {
        if (identifiedToken.equalsIgnoreCase("true") || identifiedToken.equalsIgnoreCase("false"))
            return true;

        return false;
    }

    public boolean isNumber(String identifiedToken) {
        if (identifiedToken.startsWith("INT =>")) return true;
        try {
            Integer.parseInt(identifiedToken);
        } catch (NumberFormatException e) { return false; }

        return true;
    }

    public boolean isString(String identifiedToken) {
        return identifiedToken.startsWith("\"");
    }

    public boolean isArithmeticOperation(String token) {
        ArrayList<String> arithmeticInstructions = this.instructionSet.getArithmeticInstructions();

        return arithmeticInstructions.contains(token);
    }

    public ArrayList<String> computeRegex(String text) {
        ArrayList<String> identifiedTokensLs = new ArrayList<>();
        Matcher matcher = this.instructionSet.getPattern().matcher(text);

        while (matcher.find()) {
            String token = matcher.group();
            identifiedTokensLs.add(token);

            System.out.println("FOUND: " + token);
        }

        return identifiedTokensLs;
    }

    public static void main(String[] args) {
        InstructionDetector instructionSet = new InstructionDetector(new InstructionSet());
        System.out.println(instructionSet.computeRegex("true false"));
    }
}
