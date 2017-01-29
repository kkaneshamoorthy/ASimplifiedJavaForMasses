package Engine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kowrishankar on 08/01/2017.
 */
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

    public boolean isNumber(String identifiedToken) {
        try {
            Integer.parseInt(identifiedToken);
        } catch (NumberFormatException e) { return false; }

        return true;
    }

    public boolean isArithmeticOperation(String token) {
        ArrayList<String> arithmeticInstructions = this.instructionSet.getArithmeticInstructions();

        return arithmeticInstructions.contains(token);
    }
}
