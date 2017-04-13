package Engine;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionSet {
    static final String PRINT = "PRINT";
    static final String LOOP = "LOOP";
    static final String LOOP_END = "LOOP-END";
    static final String LOOP_CONTENT = "LOOP-CONTENT";
    static final String VARIABLE = "VARIABLE";
    static final String PRINT_VARIABLE = "PRINT-VARIABLE";
    static final String CREATE = "CREATE";
    static final String CONSOLE = "CONSOLE";
    static final String EXP = "EXP";
    static final String IF = "IF";
    static final String ARITHMETIC = "ARITHMETIC";
    static final String INPUT = "INPUT";
    static final String FUNCTION = "FUNCTION";
    static final String ASSIGNMENT = "ASSIGNMENT";
    static final String DISPATCH = "CALL";

    static final String UNKNOWN = "UNKNOWN";

    private final String PLUS = "+";
    private final String ADDTXT = "ADD";
    private final String MINUS = "-";
    private final String MINUSTXT = "MINUS";
    static final String EQUAL = "=";
    private final String EQUALTXT = "EQUAL";
    private final String MULTI = "*";
    private final String DIVI = "/";
    private final String DIVIDETXT = "DIVIDE";
    private final String MOD = "%";

    private final String LT = "<";
    private final String GT = ">";
    private final String LTET = "<=";
    private final String GTET = ">=";
    static final String EQ = "==";
    private final String NEQ = "!=";
    private final String NOT = "!";
    private final String AND = "&&";
    private final String OR = "||";
    private final String ANDTXT = "AND";
    private final String ORTXT = "OR";
    private final String TRUE = "TRUE";
    private final String FALSE = "FALSE";

    private final String ZERO = "0";
    private final String ONE = "1";
    private final String TWO = "2";
    private final String THREE = "3";
    private final String FOUR = "4";
    private final String FIVE = "5";
    private final String SIX = "6";
    private final String SEVEN = "7";
    private final String EIGHT = "8";
    private final String NINE = "9";

    public ArrayList<String> expressionKeyword;
    public ArrayList<String> arithmeticKeyword;
    public ArrayList<String> printKeyWord;
    public ArrayList<String> loopKeyword;
    public ArrayList<String> variableKeyWord;
    public HashMap<String, ArrayList<String>> keywordAndFunction;
    public HashMap<String, String> instructionMap;

    public ArrayList<String> getExpressionKeyword() {
        return this.expressionKeyword;
    }

    private static final String[] KEYWORDS = new String[] {
            PRINT, LOOP, IF, INPUT, FUNCTION, "$", "equal", "\\/", "\\%", "add", "true", "false", "else", "call", "=", "store", "in", "go", "through"
    };

    private static final String[] RELATIONAL_OPERATION_PATTERN = new String[] {
            "==", "<", ">", "<=", ">=", "!", "!=", "TRUE", "FALSE"
    };

    private static final String[] BITWISE_OPERATION_PATTERN = new String[] {
            "&&", "and", "or", "||"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String RELATIONAL_PATTERN = "\\b(" + String.join("|", RELATIONAL_OPERATION_PATTERN) + ")\\b";
    private static final String BITWISE_PATTERN = "\\b(" + String.join("|", BITWISE_OPERATION_PATTERN) + ")\\b";
    private static final String NUMBER_PATTERN =  "[0-9]+";
    private static final String STRING_PATTERN= "\"[a-zA-Z0-9\\-#\\.\\(\\)\\*\\+\\/%&\\s!]{0,19}\\s*(\\*\\+\\*\\s\"[a-zA-Z0-9\\-#\\.\\(\\)\\/%&\\s!]{0,19}\")*";
    private static final String VARIABLE_NAMING_PATTERN = "\\$[a-z][0-9a-zA-Z_]";
    private static final String ID_PATTERN = VARIABLE_NAMING_PATTERN+"*";
    private static final String NAME_PATTERN = "[a-zA-Z0-9]*";
    private static final String FUNCTION_NAME_PATTERN = NAME_PATTERN + "\\([a-zA-Z0-9\\s*\\$\",]*\\)";
    private static final String EQUAL_PATTERN = "=";
    private static final String ARITHEMETRIC_PATTERN = "[\\+\\-\\*\\/\\s+]";
    private static final String TEXT_PATTERN = "[a-zA-Z0-9\\-#\\.\\(\\)\\*\\+\\/%&\\s!]{0,19}\\s*(\\*\\+\\*\\s\"[a-zA-Z0-9\\-#\\.\\(\\)\\/%&\\s!]{0,19})*";

    private static final Pattern PATTERN = Pattern.compile(
            "(" + KEYWORD_PATTERN
                    + "|" + FUNCTION_NAME_PATTERN
                    + "|" + ARITHEMETRIC_PATTERN
                    + "|" + ID_PATTERN
                    + "|" + TEXT_PATTERN
                    + "|" + STRING_PATTERN
                    + "|" + NUMBER_PATTERN
                    + "|" + RELATIONAL_PATTERN
                    + "|" + EQUAL_PATTERN + ")", Pattern.CASE_INSENSITIVE
    );

    public InstructionSet() {
        this.intialise();
        this.initialisePointsForKeyword();
    }


    private HashMap<String, Integer> wordPointMap = new HashMap<>();

    public int getPoints(String word) {
        word = word.toUpperCase();
        if (this.wordPointMap.containsKey(word))
            return this.wordPointMap.get(word);

        return 0;
    }

    public void initialisePointsForKeyword() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(this.getClass().getResource("/data/wordWithPoints.csv").getPath())));
            String line = "";

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                this.wordPointMap.put(data[0].toUpperCase(), Integer.parseInt(data[1]));
            }
        } catch (FileNotFoundException e) {
            //TODO: show dialog
            System.out.println("File not found");
        } catch (IOException e) {
            //TODO: show dialog
            System.out.println("IOException");
        }
    }

    public String computeRegex(String text) {
        Matcher matcher = PATTERN.matcher(text);
        String word = "";
        while (matcher.find()) {
            word = matcher.group(0);
        }

        return word;
    }

    public Pattern getPattern() { return PATTERN; }

    public HashMap<String, ArrayList<String>> getInstructionSet() { return this.keywordAndFunction; }

    public HashMap<String, String> getInstructionMap() { return this.instructionMap; }

    public ArrayList<String> getArithmeticInstructions() { return this.arithmeticKeyword; }

    private void intialise() {
        expressionKeyword = new ArrayList<String >();
//        expressionKeyword.add("<");
//        expressionKeyword.add(">");
//        expressionKeyword.add("<=");
//        expressionKeyword.add(">=");
//        expressionKeyword.add("==");
//        expressionKeyword.add("EQUAL");
//        expressionKeyword.add("!=");
//        expressionKeyword.add("!");
//        expressionKeyword.add("&&");
//        expressionKeyword.add("||");
//        expressionKeyword.add("AND");
//        expressionKeyword.add("OR");
//        expressionKeyword.add("NOT");
//        expressionKeyword.add("TRUE");
//        expressionKeyword.add("FALSE");


        //Print
        printKeyWord = new ArrayList<String>();
        printKeyWord.add(this.PRINT);
        printKeyWord.add("WRITE");
        printKeyWord.add("\"");

        //Variable
        variableKeyWord = new ArrayList<String>();
        variableKeyWord.add(this.VARIABLE);
        variableKeyWord.add(this.CREATE);
        variableKeyWord.add("$");

        //Assignment
        ArrayList<String> assignment = new ArrayList<>();
        assignment.add("=");
        assignment.add("$");
//        assignment.add("+");
//        assignment.add("-");
//        assignment.add("*");
//        assignment.add("/");
//        assignment.add("%");

        //function
        ArrayList<String> function = new ArrayList<>();
        function.add("function");
        function.add("method");
        function.add("(");
        function.add(")");
        function.add(":");

        //loop
        loopKeyword = new ArrayList<String>();
        loopKeyword.add("LOOP");
        loopKeyword.add(":");
        loopKeyword.add("LOOP-END");
        loopKeyword.add("GO");
        loopKeyword.add("THROUGH");

        //function call
        ArrayList<String> functionCall = new ArrayList<>();
        functionCall.add("CALL");
        functionCall.add("(");
        functionCall.add(")");

        //if
        ArrayList<String> ifKeyword = new ArrayList<>();
        ifKeyword.add("IF");
        ifKeyword.add("CONDITION");
        ifKeyword.add(":");

        //input
        ArrayList<String> inputKeyword = new ArrayList<>();
        inputKeyword.add("INPUT");
        inputKeyword.add("USER");
        inputKeyword.add("GET");

        keywordAndFunction = new HashMap<String, ArrayList<String>>();
        keywordAndFunction.put(this.PRINT, printKeyWord);
        keywordAndFunction.put(this.LOOP, loopKeyword);
        keywordAndFunction.put(this.VARIABLE, variableKeyWord);
        keywordAndFunction.put(this.EXP, expressionKeyword);
        keywordAndFunction.put("ASSIGNMENT", assignment);
        keywordAndFunction.put("FUNCTION", function);
        keywordAndFunction.put("CALL", functionCall);
        keywordAndFunction.put("IF", ifKeyword);
        keywordAndFunction.put("INPUT", inputKeyword);

        this.init();
    }

    private void init() {
        this.instructionMap = new HashMap<String, String>();

        this.instructionMap.put(this.PRINT, this.PRINT);
        this.instructionMap.put(this.LOOP, this.LOOP);
        this.instructionMap.put(this.LOOP_END, this.LOOP_END);
        this.instructionMap.put(":", this.LOOP_CONTENT);
        this.instructionMap.put(this.INPUT, this.INPUT);
        this.instructionMap.put(this.IF, this.IF);
        this.instructionMap.put("$", "$");

        this.instructionMap.put(this.PLUS, "ARITHMETIC_OPERATION =>+");
        this.instructionMap.put(this.ADDTXT, this.PLUS);
        this.instructionMap.put(this.MINUS, "ARITHMETIC_OPERATION =>-");
        this.instructionMap.put(this.MINUSTXT, this.MINUS);
        this.instructionMap.put(this.EQUAL, "ASSIGNMENT");
        this.instructionMap.put(this.MULTI, "ARITHMETIC_OPERATION => *");
        this.instructionMap.put(this.DIVI, "ARITHMETIC_OPERATION => /");
        this.instructionMap.put(this.DIVIDETXT, this.DIVI);
        this.instructionMap.put(this.MOD, "ARITHMETIC_OPERATION => %");

        this.instructionMap.put(this.LT, "LT");
        this.instructionMap.put(this.GT, "GT");
        this.instructionMap.put(this.LTET, "LTET");
        this.instructionMap.put(this.GTET, "GTET");
        this.instructionMap.put(EQ, "EQ");
        this.instructionMap.put(this.EQUALTXT, "EQ");
        this.instructionMap.put(this.NEQ, "NEQ");
        this.instructionMap.put(this.NOT, "NOT");
        this.instructionMap.put(this.AND, "AND");
        this.instructionMap.put(this.OR, "OR");
        this.instructionMap.put(this.ANDTXT, "ANDTXT");
        this.instructionMap.put(this.ORTXT, "ORTXT");
        this.instructionMap.put(this.TRUE, "TRUE");
        this.instructionMap.put(this.FALSE, "FALSE");

        this.instructionMap.put(this.ZERO, this.ZERO);
        this.instructionMap.put(this.ONE, this.ONE);
        this.instructionMap.put(this.TWO, this.TWO);
        this.instructionMap.put(this.THREE, this.THREE);
        this.instructionMap.put(this.FOUR, this.FOUR);
        this.instructionMap.put(this.FIVE, this.FIVE);
        this.instructionMap.put(this.SIX, this.SIX);
        this.instructionMap.put(this.SEVEN, this.SEVEN);
        this.instructionMap.put(this.EIGHT, this.EIGHT);
        this.instructionMap.put(this.NINE, this.NINE);
    }
}

