package Engine;

import GUI.Console;
import Utility.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

public class LexicalAnalyser {
    private InstructionSet instructionSet;

    public LexicalAnalyser(InstructionSet instructionSet) {
        this.instructionSet = instructionSet;
    }

    public String detectInstruction(String[] tokens) {
        HashMap<String, Integer> instructionScoreMap = new HashMap<String, Integer>();
        int highestSoFar = 0;
        String mostLikelyInstruction = "UNKNOWN";
        for (String token : tokens) {
            String annotatedToken = Helper.retrieveData(token);
            if (!annotatedToken.isEmpty()) token = annotatedToken;

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

    public HashMap<Integer, Pair> generateAnnotatedToken(String[] statements) {
        HashMap<Integer, Pair> instructionMap = new HashMap<>();
        int instructionCounter = 0;

        for (String statement : statements) {
            ArrayList<String> identifiedTokensList = annotateToken(statement);
            String detectedInstruction = this.detectInstruction(statement.toUpperCase().trim().split(" "));
            if (detectedInstruction.equals(InstructionSet.UNKNOWN)) {
                String[] identifiedTokensArr = identifiedTokensList.toArray(new String[identifiedTokensList.size()]);
                detectedInstruction = this.detectInstruction(identifiedTokensArr);
            }

            System.out.println(detectedInstruction);

            String value = "";
            if (detectedInstruction.equals(InstructionSet.ELSE)) {
                value+= InstructionSet.ELSE + " ";
            }

            for (String token : identifiedTokensList) {
                System.out.println(token);
                String retrievedValue = Helper.retrieveData(token);
                if (token.equals("") || token.equals("ERROR") || retrievedValue.equals(InstructionSet.UNKNOWN)) continue;
                if (token.equals(InstructionSet.ASSIGNMENT)) {
                    value+= InstructionSet.EQUAL;
                } else if (token.equals("EQ")) {
                    value+= InstructionSet.EQ;
                } else if (token.equalsIgnoreCase(InstructionSet.ELSE) || token.equalsIgnoreCase(InstructionSet.OTHERWISE) || token.equalsIgnoreCase("ORTXT")) {
                    value+= InstructionSet.ELSE + " ";
                } else if (token.equalsIgnoreCase(InstructionSet.LOOP_INCREMENT) && !value.contains(InstructionSet.LOOP_INCREMENT)) {
                    value+=InstructionSet.LOOP_INCREMENT + "=>";
                }

                value += retrievedValue;
            }

            System.out.println(value);

            instructionMap.put(instructionCounter, new Pair(detectedInstruction, value));
            instructionCounter+=1;
        }

        return instructionMap;
    }

    public String identifyToken(char charToken) {
        String strToken = charToken+"";
        HashMap<String, String> instructionMap = this.instructionSet.getInstructionMap();
        if (instructionMap.containsKey(strToken))
            return instructionMap.get(strToken);
        else if (Helper.isNumber(strToken))
            return strToken;

        return InstructionSet.UNKNOWN;
    }

    public ArrayList<String> annotateToken(String statement) {
        ArrayList<String> identifiedTokens = this.tokeniseText(statement);

        for (int i=0; i<identifiedTokens.size(); i++) {
            String identifiedToken = identifiedTokens.get(i);
            if (Helper.isNumber(identifiedToken))
                identifiedTokens.set(i, "INT =>" + identifiedToken);
            else if(Helper.isString(identifiedToken))
                identifiedTokens.set(i, "STRING =>" + identifiedToken);
            else if (identifiedToken.startsWith("$"))
                identifiedTokens.set(i, "VARIABLE_NAME =>" + identifiedToken);
            else if (Helper.isExpression(identifiedToken))
                identifiedTokens.set(i, "EXPRESSION =>" + identifiedToken);
            else if (Helper.isFunction(identifiedToken))
                identifiedTokens.set(i, "FUNCTION_NAME =>"+identifiedToken);
            else if (this.instructionSet.getInstructionMap().containsKey(identifiedToken.toUpperCase()))
                identifiedTokens.set(i, this.instructionSet.getInstructionMap().get(identifiedToken.toUpperCase()));
        }

        return identifiedTokens;
    }

    public ArrayList<String> tokeniseText(String text) {
        ArrayList<String> identifiedTokensLs = new ArrayList<>();
        Matcher matcher = this.instructionSet.getPattern().matcher(text);

        while (matcher.find()) {
            String token = matcher.group();
            identifiedTokensLs.add(token);
        }

        return identifiedTokensLs;
    }

    public static void main(String[] args) {
        LexicalAnalyser detector = new LexicalAnalyser(new InstructionSet());
//        System.out.println(instructionSet.annotateToken("$x=\"*\""));
//        System.out.println(instructionSet.annotateToken("$x = $y + 1"));
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

//        detector.generateAnnotatedToken(new String[]{"please print \"Hello World!\""});
//        detector.generateAnnotatedToken(new String[]{"call main():"});
//        detector.generateAnnotatedToken(new String[]{"print 5"});
//        detector.generateAnnotatedToken(new String[]{"$x = 1"});
//        detector.generateAnnotatedToken(new String[]{"if 2==2:"});
//        detector.generateAnnotatedToken(new String[]{"write what 2+2 to the console"});
//        detector.generateAnnotatedToken(new String[]{"print 2+2+3"});
//        detector.generateAnnotatedToken(new String[]{"$x = 5+4"});
//        detector.generateAnnotatedToken(new String[]{"$x = 45"});
//        detector.generateAnnotatedToken(new String[]{"if 4==4:"});
//        detector.generateAnnotatedToken(new String[]{"$x = 4/4"});
//        detector.generateAnnotatedToken(new String[]{"function main():"});
//        detector.generateAnnotatedToken(new String[]{"$x = \"*\""});
//        detector.generateAnnotatedToken(new String[]{"call main(4)"});
//        detector.generateAnnotatedToken(new String[]{"call main()"});
//        detector.generateAnnotatedToken(new String[]{"call main($x)"});
//        detector.generateAnnotatedToken(new String[]{"call main(\"Hello\")"});
//        detector.generateAnnotatedToken(new String[]{"call main(\"Hello\", 54)"});
//        detector.generateAnnotatedToken(new String[]{"$x = 234"});
//        detector.generateAnnotatedToken(new String[]{"$x = \"Hello\"Not"});
//        detector.generateAnnotatedToken(new String[]{"end"});
//        detector.generateAnnotatedToken(new String[]{"$x = get input"});
//        detector.generateAnnotatedToken(new String[]{"print $x + \" x 3  = \" + $x * 3"});
//        detector.generateAnnotatedToken(new String[]{"print 15 % 5"});
//        detector.generateAnnotatedToken(new String[]{"or if 3 == 3"});
//        detector.generateAnnotatedToken(new String[]{"else if 2 == 2"});
//        detector.generateAnnotatedToken(new String[]{"sef345dfrgdfg dfg 45t", "\"sefs\"", "+-/*%", "()", "function"});
//        detector.generateAnnotatedToken(new String[]{"print \"hello world ! 100 times\""});
//        detector.generateAnnotatedToken(new String[]{"loop 5 times by 2"});
//        System.out.println(Helper.isNumber("\"12\""));


        SynaticAnalyser synaticAnalyser = new SynaticAnalyser(new Console());
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():","$x = 4/4", "$x = 4"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "if 4==\"hello\":"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "write what 2+2 to the console"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "print"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "call main()"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "get input from"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x = \"*\"", "$y = \"h\"", "$x =$x+$y", "print $x"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x=$x+$y"})); //TODO: it is assignment whether there is a space or not
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "call main(343, 45, 567, \"Hello world\")"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "call hello(1, 2)", "function hello($x, $y):"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "call hello(223)", "function hello($x, $y):"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$s = 1", "call hello($s)", "function hello($x, $y):"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x = \"*\"", "loop 5 times:", "$x = $x+\"*\""}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x = 12", "$y = 12", "$x = $x + $y", "print $x"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x = 1", "loop 5 times", "end of loop", "print $x"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x = get user input", "print $x"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x = get user input", "print $x"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "$x = 1+1", "print $x + \" x 3  = \" + $x * 3"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "print 15 % 5"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "write a loop to repeat 5 times", "write a condition if 2==2", "print 123", "end the if", "write a condition if 3==3", "print 321", "end the if","end for loop"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "write a loop to repeat 5 times", "write a condition if 2==2", "print 123", "write a condition if 3==3", "print 321", "end the if", "end the if", "end for loop"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "write if 2 == 2", "print 2", "else", "print 1", "end"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "write if 2 == 2", "print 2", "else if 3 == 3", "print 1", "end"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "if 2 == 2", "print 2", "or if 3 == 3", "print 1", "end"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "loop 1 times", "print 12", "end", "print 1"}));
//        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "dkfhg 9854769 kjfdgn"}));
        synaticAnalyser.generateIntermediateRepresentation(detector.generateAnnotatedToken(new String[]{"function main():", "loop 5 times increment 23 by"}));
    }
}
