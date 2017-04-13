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

    public String detectInstruction(String[] tokens) {
        HashMap<String, Integer> instructionScoreMap = new HashMap<String, Integer>();
        int highestSoFar = 0;
        String mostLikelyInstruction = "UNKNOWN";
        for (String token : tokens) {
            String annotatedToken = retrieveData(token);
            if (!annotatedToken.equals("")) token = annotatedToken;

            HashMap<String, ArrayList<String>> instructionWordMap = this.instructionSet.getInstructionSet();

            for (String function : instructionWordMap.keySet()) {
                int functionScore = -1;
                ArrayList<String> instructionKeywords = instructionWordMap.get(function);
                for (String keyword : instructionKeywords) {
                    if (keyword.equalsIgnoreCase(token)) {
                        int wordPoint = this.instructionSet.getPoints(token);
                        if (instructionScoreMap.containsKey(function)) {
                            functionScore = instructionScoreMap.get(function)+wordPoint;
                            instructionScoreMap.put(function, functionScore);
                        } else {
                            functionScore = wordPoint;
                            instructionScoreMap.put(function, functionScore);
                        }

                        if (highestSoFar < functionScore) {
                            highestSoFar = functionScore;
                            mostLikelyInstruction = function;
                        }
                    }
                }
            }
        }

        return mostLikelyInstruction;
    }

    public HashMap<Integer, Pair> detect(String[] statements) {
        HashMap<Integer, Pair> instructionMap = new HashMap<>();
        int instructionCounter = 0;

        for (String statement : statements) {
            String detectedInstruction = this.detectInstruction(statement.toUpperCase().trim().split(" "));
            if (detectedInstruction.equals("UNKNOWN")) {
                ArrayList<String> ls = identifyTokens(statement);
                String[] identifiedTokens = identifyTokens(statement).toArray(new String[ls.size()]);
                detectedInstruction = this.detectInstruction(identifiedTokens);
            }

            ArrayList<String> identifiedTokens = this.identifyTokens(statement);
            String value = "";

            for (String token : identifiedTokens) {
                String retrievedValue = this.retrieveData(token);
                if (token.equals("") || token.equals("ERROR") || retrievedValue.equals(InstructionSet.UNKNOWN)) continue;
                if (token.equals(InstructionSet.ASSIGNMENT)) {
                    value+= InstructionSet.EQUAL;
                } else if (token.equals("EQ")) {
                    value+= InstructionSet.EQ;
                }
                value += retrievedValue;
            }

            instructionMap.put(instructionCounter, new Pair(detectedInstruction, value));
            instructionCounter+=1;
        }

//        for (Integer key : instructionMap.keySet()) {
//            System.out.println(key + " " + instructionMap.get(key).getKey() + " " +instructionMap.get(key).getValue());
//        }

        return instructionMap;
    }

    private String retrieveData(String token) {
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

    public ArrayList<String> identifyTokens(String statement) {
        ArrayList<String> identifiedTokens = this.computeRegex(statement);

        for (int i=0; i<identifiedTokens.size(); i++) {
            String identifiedToken = identifiedTokens.get(i);
            if (isNumber(identifiedToken))
                identifiedTokens.set(i, "INT =>" + identifiedToken);
            else if(isString(identifiedToken))
                identifiedTokens.set(i, "STRING =>" + identifiedToken);
            else if (identifiedToken.startsWith("$"))
                identifiedTokens.set(i, "VARIABLE_NAME =>" + identifiedToken);
            else if (isExpression(identifiedToken))
                identifiedTokens.set(i, "EXPRESSION =>" + identifiedToken);
            else if (isFunction(identifiedToken))
                identifiedTokens.set(i, "FUNCTION_NAME =>"+identifiedToken);
            else if (this.instructionSet.getInstructionMap().containsKey(identifiedToken.toUpperCase()))
                identifiedTokens.set(i, this.instructionSet.getInstructionMap().get(identifiedToken.toUpperCase()));
        }

        return identifiedTokens;
    }

    public boolean isFunction(String token) {
        if (token.contains("(") && token.contains(")"))
            return true;

        return false;
    }

    public boolean isExpression(String token) {
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

    public ArrayList<String> computeRegex(String text) {
        ArrayList<String> identifiedTokensLs = new ArrayList<>();
        Matcher matcher = this.instructionSet.getPattern().matcher(text);

        while (matcher.find()) {
            String token = matcher.group();
            identifiedTokensLs.add(token);
        }

        return identifiedTokensLs;
    }

    public static void main(String[] args) {
        InstructionDetector detector = new InstructionDetector(new InstructionSet());
//        System.out.println(instructionSet.identifyTokens("$x=\"*\""));
//        System.out.println(instructionSet.identifyTokens("$x = $y + 1"));
//        System.out.println(detector.detectInstruction(("write i would like to print \" hello \"")));
//        System.out.println(detector.detectInstruction(("$s = 45")));
//        System.out.println(detector.detectInstruction(("write a function main ( ) :")));
//        System.out.println(detector.detectInstruction(("loop 5 times:")));
//        System.out.println(detector.detectInstruction(("$x = 5+4")));
//        System.out.println(detector.detectInstruction(("create $x")));
//        System.out.println(detector.detectInstruction(("write a function called Main():")));
//        System.out.println(detector.detectInstruction(("write what 2+2 to the console")));
//        System.out.println(detector.detectInstruction(("print 5")));
//        System.out.println(detector.detectInstruction(("\tprint 5")));
//        System.out.println(detector.detectInstruction(("call main()")));
//        System.out.println(detector.detectInstruction(("if (2==2):")));

//        detector.detect(new String[]{"please print \"Hello World!\""});
//        detector.detect(new String[]{"call main():"});
//        detector.detect(new String[]{"print 5"});
//        detector.detect(new String[]{"create $x"});
//        detector.detect(new String[]{"if 2==2:"});
//        detector.detect(new String[]{"write what 2+2 to the console"});
//        detector.detect(new String[]{"print 2+2+3"});
//        detector.detect(new String[]{"$x = 5+4"});
//        detector.detect(new String[]{"$x = 45"});
//        detector.detect(new String[]{"if 4==4:"});
//        detector.detect(new String[]{"$x = 4/4"});
//        detector.detect(new String[]{"function main():"});
//        detector.detect(new String[]{"$x = \"*\""});
//        detector.detect(new String[]{"call main(4)"});
//        detector.detect(new String[]{"call main()"});
//        detector.detect(new String[]{"call main($x)"});
//        detector.detect(new String[]{"call main(\"Hello\")"});
//        detector.detect(new String[]{"call main(\"Hello\", 54)"});
        detector.detect(new String[]{"$x = 234"});



        SynaticAnalyser synaticAnalyser = new SynaticAnalyser();
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():","$x = 4/4", "$x = 4"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "if 4==\"hello\":"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"write what 2+2 to the console"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "print"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "call main()"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "get input from"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "$x = \"*\"", "$y = \"h\"", "$x =$x+$y", "print $x"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"$x=$x+$y"})); //TODO: it is assignment whether there is a space or not
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "call main(343, 45, 567, \"Hello world\")"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "call hello(1, 2)", "function hello($x, $y):"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "call hello(223)", "function hello($x, $y):"}));
//        synaticAnalyser.generateInstructions(detector.detect(new String[]{"function main():", "$s = 1", "call hello($s)", "function hello($x, $y):"}));
    }
}
