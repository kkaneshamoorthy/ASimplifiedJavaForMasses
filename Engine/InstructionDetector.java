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
        //System.out.println(statement);
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
                            //System.out.println("func " + mostLikelyFunction + " " + highestSoFar);
                        }
                    }
                }
            }
//            if (instructionsWithKeyword.containsKey(key)) {
//                int functionScore = -1;
//
//                if (functionDetection.containsKey(key)) {
//                    functionScore = functionDetection.get(key)+1;
//                    functionDetection.put(key, functionScore);
//                } else {
//                    functionDetection.put(key, 1);
//                    functionScore = 1;
//                }
//
//                if (highestSoFar < functionScore) {
//                    highestSoFar = functionScore;
//                    mostLikelyFunction = instructionsWithKeyword.get(key);
//                }
//            }
        }

        return mostLikelyFunction;
    }

    public String identifyToken(String token) {
        String identifiedToken = this.UNKNOWN;
        HashMap<String, String> instructionMap = this.instructionSet.getInstructionMap();
        if (instructionMap.containsKey(token))
            return instructionMap.get(token);

        return identifiedToken;
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
            identifiedToken.add(this.identifyToken(token.toUpperCase()));
        }

        return identifiedToken;
    }
}
