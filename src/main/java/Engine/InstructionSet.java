package Engine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class InstructionSet {
    public static final String PRINT = "PRINT";
    public static final String LOOP = "LOOP";
    public static final String LOOP_END = "LOOP-END";
    public static final String LOOP_CONTENT = "LOOP-CONTENT";
    public static final String VARIABLE = "VARIABLE";
    public static final String CREATE = "CREATE";
    public static final String EXP = "EXP";
    public static final String IF = "IF";
    public static final String INPUT = "INPUT";
    public static final String FUNCTION = "FUNCTION";
    public static final String ASSIGNMENT = "ASSIGNMENT";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String INCREMENT = "INCREMENT";
    public static final String LOOP_INCREMENT = "LOOP_INCREMENT";
    public static final String BY = "BY";

    public static final String PLUS = "+";
    public static final String ADDTXT = "ADD";
    public static final String MINUS = "-";
    public static final String MINUSTXT = "MINUS";
    public static final String EQUAL = "=";
    public static final String EQUALTXT = "EQUAL";
    public static final String MULTI = "*";
    public static final String DIVI = "/";
    public static final String DIVIDETXT = "DIVIDE";
    public static final String MOD = "%";

    public static final String LT = "<";
    public static final String GT = ">";
    public static final String LTET = "<=";
    public static final String GTET = ">=";
    public static final String EQ = "==";
    public static final String NEQ = "!=";
    public static final String NOT = "!";
    public static final String AND = "&&";
    public static final String OR = "||";
    public static final String ANDTXT = "AND";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";

    public static final String DOLLAR = "$";
    public static final String METHOD_CALL = "CALL";
    public static final String STORE = "STORE";
    public static final String IN = "IN";
    public static final String GO = "GO";
    public static final String THROUGH = "THROUGH";
    public static final String END = "END";
    public static final String ELSE = "ELSE";
    public static final String ORTXT = "OR";
    public static final String OTHERWISE = "OTHERWISE";

    public ArrayList<String> expressionKeyword;
    public ArrayList<String> printKeyWord;
    public ArrayList<String> loopKeyword;
    public ArrayList<String> variableKeyWord;
    public HashMap<String, ArrayList<String>> keywordAndInstruction;
    public HashMap<String, String> instructionMap;

    public ArrayList<String> getExpressionPredefinedKeyword() {
        return this.expressionKeyword;
    }

    private static final String NUMBER_PATTERN =  "[0-9]+";
    private static final String TEXT_PATTERN = "[a-zA-Z0-9.]*";
    private static final String SINGLE_STRING_PATTERN  = "[a-zA-Z0-9\\-#\\.\\(\\)\\=\\*\\+\\/%&\\s!]*";
    private static final String STRING_PATTERN= "\""+SINGLE_STRING_PATTERN+"\\s*"+SINGLE_STRING_PATTERN+"\"";
    private static final String VARIABLE_NAMING_PATTERN = "\\$[a-z][0-9a-zA-Z_]";
    private static final String ID_PATTERN = VARIABLE_NAMING_PATTERN+"*";
    private static final String NAME_PATTERN = "[a-zA-Z0-9]*";
    private static final String FUNCTION_NAME_PATTERN = NAME_PATTERN + "\\([a-zA-Z0-9\\s*\\$\",]*\\)";
    private static final String EQUAL_PATTERN = "=";
    private static final String ARITHEMETRIC_PATTERN = "[\\%\\+\\-\\*\\/\\s+]";

    private static final Pattern PATTERN = Pattern.compile(
            "(" + FUNCTION_NAME_PATTERN
                    + "|" + ARITHEMETRIC_PATTERN
                    + "|" + ID_PATTERN
                    + "|" + STRING_PATTERN
                    + "|" + NUMBER_PATTERN
                    + "|" + EQUAL_PATTERN
                    + "|" + TEXT_PATTERN +
                    ")", Pattern.CASE_INSENSITIVE
    );

    public InstructionSet() {
        this.initialise();
        this.initialisePointsForKeyword();

        System.out.println("data loaded");
        System.out.println("starting...");
    }

    private HashMap<String, Integer> wordPointMap = new HashMap<String, Integer>();

    public int getPoints(String word) {
        word = word.toLowerCase();
        if (this.wordPointMap.containsKey(word))
            return this.wordPointMap.get(word);

        return 0;
    }

    public void initialisePointsForKeyword() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(getClass().getClassLoader().getResource("data/wordWithPoints.csv").getPath())));
            String line = "";

            System.out.println("Initialising");
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                System.out.println("loading data: "+ line);

                this.wordPointMap.put(data[0], Integer.parseInt(data[1]));
            }
        } catch (Exception e) {
            System.out.println("File not found");
            System.out.println("Loading data from alternatives...");
            loadAlternativeData();
        }
    }

    private void loadAlternativeData() {
        this.wordPointMap.put("print",5);
        this.wordPointMap.put("show",5);
        this.wordPointMap.put("display",5);
        this.wordPointMap.put("alert",5);
        this.wordPointMap.put("write",2);
        this.wordPointMap.put("loop",5);
        this.wordPointMap.put("create",1);
        this.wordPointMap.put("variable",2);
        this.wordPointMap.put("console",1);
        this.wordPointMap.put("function",5);
        this.wordPointMap.put("call",3);
        this.wordPointMap.put("if",3);
        this.wordPointMap.put("condition",3);
        this.wordPointMap.put("input",5);
        this.wordPointMap.put("user",4);
        this.wordPointMap.put("get",3);
        this.wordPointMap.put("go",5);
        this.wordPointMap.put("through",2);
        this.wordPointMap.put("loop-end",5);
        this.wordPointMap.put("end",5);
        this.wordPointMap.put("else",5);
        this.wordPointMap.put("otherwise",4);
        this.wordPointMap.put("or",2);
        this.wordPointMap.put("repeat",5);
        this.wordPointMap.put("==",3);
        this.wordPointMap.put("=",5);
        this.wordPointMap.put("+",2);
        this.wordPointMap.put("-",2);
        this.wordPointMap.put("/",2);
        this.wordPointMap.put("*",2);
        this.wordPointMap.put("%",2);
        this.wordPointMap.put("$",1);
    }

    public Pattern getPattern() { return PATTERN; }

    public HashMap<String, ArrayList<String>> getInstructionSet() { return this.keywordAndInstruction; }

    public HashMap<String, String> getInstructionMap() { return this.instructionMap; }

    static final String SHOW = "SHOW";
    static final String DISPALY= "DISPLAY";
    static final String ALERT = "ALERT";
    static final String WRITE = "WRITE";
    static final String DOUBLE_QUOTE = "\"";
    static final String METHOD = "METHOD";
    static final String OPEN_PARENT = "(";
    static final String CLOSE_PARENT = ")";
    static final String COLON = ":";
    static final String END_LOOP = "END-LOOP";
    static final String END_IF = "END-IF";
    static final String CONDITION = "CONDITION";
    static final String NOT_EQUAL = "!=";
    static final String USER = "USER";

    private void initialise() {
        expressionKeyword = new ArrayList<String >();

        //Print Instruction
        printKeyWord = new ArrayList<String>();
        printKeyWord.add(this.PRINT);
        printKeyWord.add(SHOW);
        printKeyWord.add(DISPALY);
        printKeyWord.add(ALERT);
        printKeyWord.add(WRITE);
        printKeyWord.add(DOUBLE_QUOTE);

        System.out.println("Loading instructions print");

        //function Instruction
        ArrayList<String> function = new ArrayList<>();
        function.add(FUNCTION);
        function.add(METHOD);
        function.add(OPEN_PARENT);
        function.add(CLOSE_PARENT);
        function.add(COLON);

        System.out.println("Loading instructions function");

        //Variable Instruction
        variableKeyWord = new ArrayList<String>();
        variableKeyWord.add(this.VARIABLE);
        variableKeyWord.add(this.CREATE);
        variableKeyWord.add(DOLLAR);

        System.out.println("Loading instructions variable");

        //loop
        loopKeyword = new ArrayList<String>();
        loopKeyword.add(LOOP);
        loopKeyword.add(COLON);
        loopKeyword.add(GO);
        loopKeyword.add(THROUGH);
        loopKeyword.add(INCREMENT);
        loopKeyword.add(BY);
        loopKeyword.add("REPEAT");

        System.out.println("Loading instructions loop");

        //Assignment Instruction
        ArrayList<String> assignment = new ArrayList<>();
        assignment.add(EQUAL);
        assignment.add(DOLLAR);

        System.out.println("Loading instructions assignment");

        //Loop end
        ArrayList<String> endLoop = new ArrayList<String>();
        endLoop.add(END);
        endLoop.add(LOOP_END);
        endLoop.add(END_LOOP);
        endLoop.add(END_IF);

        System.out.println("Loading instructions end");

        //function call
        ArrayList<String> functionCall = new ArrayList<>();
        functionCall.add(METHOD_CALL);
        functionCall.add(OPEN_PARENT);
        functionCall.add(CLOSE_PARENT);

        System.out.println("Loading instructions function call");

        //if
        ArrayList<String> ifKeyword = new ArrayList<>();
        ifKeyword.add(IF);
        ifKeyword.add(CONDITION);
        ifKeyword.add(EQ);
        ifKeyword.add(LTET);
        ifKeyword.add(GTET);
        ifKeyword.add(NOT_EQUAL);
        ifKeyword.add(LT);
        ifKeyword.add(GT);
        ifKeyword.add(COLON);

        System.out.println("Loading instructions if");

        //else
        ArrayList<String> elseKeyword = new ArrayList<>();
        elseKeyword.add(ELSE);
        elseKeyword.add(ORTXT);
        elseKeyword.add(OTHERWISE);

        System.out.println("Loading instructions else");

        //input
        ArrayList<String> inputKeyword = new ArrayList<>();
        inputKeyword.add(INPUT);
        inputKeyword.add(USER);

        System.out.println("Loading instructions input");

        keywordAndInstruction = new HashMap<String, ArrayList<String>>();
        keywordAndInstruction.put(PRINT, printKeyWord);
        keywordAndInstruction.put(LOOP, loopKeyword);
        keywordAndInstruction.put(VARIABLE, variableKeyWord);
        keywordAndInstruction.put(EXP, expressionKeyword);
        keywordAndInstruction.put(ASSIGNMENT, assignment);
        keywordAndInstruction.put(FUNCTION, function);
        keywordAndInstruction.put(METHOD_CALL, functionCall);
        keywordAndInstruction.put(IF, ifKeyword);
        keywordAndInstruction.put(ELSE, elseKeyword);
        keywordAndInstruction.put(INPUT, inputKeyword);
        keywordAndInstruction.put(LOOP_END, endLoop);

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

        this.instructionMap.put(BY, LOOP_INCREMENT);
        this.instructionMap.put(INCREMENT, LOOP_INCREMENT);

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
    }
}

