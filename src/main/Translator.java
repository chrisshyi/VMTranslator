package main;

import java.util.List;

public class Translator {
    // used to keep track of comparison operators so
    // that labels may be used properly
    private int numComparisons = 0;

    // keeps track of return labels so that
    // each label may be unique
    private int numReturnLabels = 0;

    // keeps track of the function currently being translated,
    // needed for labels and gotos
    // mutate this when a new function declaration is encountered in the VM code
    private String currFunction = "";

    // code fragment for ending the program in an infinite loop
    private final String endProgram = "(END)\n" +
            "@END\n" +
            "0;JMP\n";

    /*
     * Start by defining string templates to avoid code repetition
     */
    private final String incrementSP = "@SP\n" +
            "M=M+1\n";

    private final String decrementSP = "@SP\n" +
            "M=M-1\n";

    // retrieves the value at the top of the stack and decrements SP (effectively a pop)
    private final String getTopOfStack = decrementSP +
            "A=M\n" +
            "D=M\n";

    // template for eg, gt and lt
    // first %s is the comparison count (from numComparisons field)
    // second %s is the comparison operator (JEQ, JGT, or JLT)
    // third %s is the comparison count again
    // fourth %s is the comparison count again
    private final String comparisonTemplate = getTopOfStack +
            "A=A-1\n" +
            "D=M-D\n" +
            "@TRUE_%1$d\n" + // %1$s is a way of specifying a positional formatter, 1$ being the first arg
            "D;%2$s\n" +
            "@SP\n" + // go to where the boolean value should be placed
            "A=M\n" +
            "A=A-1\n" +
            "M=0\n" + // push false value
            "@REST_%1$d\n" + // goto rest of the code
            "0;JMP\n" +
            "(TRUE_%1$d)\n" +
            "@SP\n" +
            "A=M\n" +
            "A=A-1\n" +
            "M=-1\n" +
            "(REST_%1$d)\n";

    // template for binary arithmetic and logical operations
    // add, sub, and, or
    private final String binaryArithAndLogicTemplate = decrementSP +
            "A=M\n" +
            "D=M\n" +
            decrementSP +
            "A=M\n" +
            "M=M%sD\n" +
            incrementSP;

    // template for unary arithmetic and logical operations
    // neg, not
    private final String unaryArithAndLogicTemplate = decrementSP +
            "A=M\n" +
            "M=%sM\n" +
            incrementSP;

    // template for retrieving values from specified memory segments
    private final String retrieveValFromMem = "@%s\n" +
            "A=D+M\n" +
            "D=M\n";

    // code fragment for retrieving a value from the temporary memory segment
    private final String retrieveValFromTemp = "@5\n" +
            "A=D+A\n" +
            "D=M\n";

    // template for pushing a pointer value,
    // first %s is either THIS or THAT
    private final String pushPointer = "@%s\n" +
            "D=M\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            incrementSP;

    // template for popping into a pointer memory location
    // %s is either THIS or THAT
    private final String popPointer = decrementSP +
            "A=M\n" +
            "D=M\n" +
            "@%s\n" +
            "M=D\n";

    // template for pushing values from memory segment(s) onto the stack
    // first %s is the index (or constant)
    // second %s is the code fragment for retrieving values from memory
    private final String pushTemplate = "@%s\n" +
            "D=A\n" +
            "%s" + // put segment value retrieval here, or empty string if constant
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            incrementSP;

    // first %s is the index, second %s is the memory segment
    private final String popTemplate = "@%s\n" +
            "D=A\n" +
            "@%s\n" +
            "D=D+M\n" +
            "@R13\n" +
            "M=D\n" +
            decrementSP +
            "A=M\n" +
            "D=M\n" +
            "@R13\n" +
            "A=M\n" +
            "M=D\n";

    // pops the top of the stack into a temporary memory segment
    // %s is for the offset
    private final String popToTemp = "@5\n" +
            "D=A\n" +
            "@%s\n" +
            "D=D+A\n" +
            "@R13\n" +
            "M=D\n" +
            decrementSP +
            "A=M\n" +
            "D=M\n" +
            "@R13\n" +
            "A=M\n" +
            "M=D\n";

    private final String pushReturnAddr = "@RETURN_%d\n" +
            "D=A\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            incrementSP;

    private final String repositionARG = "D=0\n" +
            "@%d\n" +
            "D=D-A\n" +
            "@5\n" +
            "D=D-A\n" +
            "@SP\n" +
            "D=M-D\n" +
            "@ARG\n" +
            "M=D\n";
    /**
     * Translates VM code to Hack assembly code
     * @param VMCode the virtual machine code to be translated, split into its fields
     * @param fileName the name of the VM code file, needed for static memory access
     * @return the input translated into Hack assembly code
     */
    public String compileToAssembly(List<String> VMCode, String fileName) {
        String operator = VMCode.get(0);
        String translationTemplate;
        String translatedOperator = "";
        String translatedAssembly = "";
        // give cases that declare additional variables their own scope
        switch (operator) {
            case "eq": case "gt": case "lt":
                this.numComparisons += 1;
                translationTemplate = comparisonTemplate;
                switch (operator) {
                    case "eq":
                        translatedOperator = "JEQ";
                        break;
                    case "gt":
                        translatedOperator = "JGT";
                        break;
                    case "lt":
                        translatedOperator = "JLT";
                        break;
                }
                translatedAssembly = String.format(translationTemplate,
                        this.numComparisons, translatedOperator);
                break;
            case "add": case "sub": case "and": case "or":
                translationTemplate = binaryArithAndLogicTemplate;
                switch (operator) {
                    case "add":
                        translatedOperator = "+";
                        break;
                    case "sub":
                        translatedOperator = "-";
                        break;
                    case "and":
                        translatedOperator = "&";
                        break;
                    case "or":
                        translatedOperator = "|";
                        break;
                }
                translatedAssembly = String.format(translationTemplate, translatedOperator);
                break;
            case "neg": case "not":
                translationTemplate = unaryArithAndLogicTemplate;
                switch (operator) {
                    case "neg":
                        translatedOperator = "-";
                        break;
                    case "not":
                        translatedOperator = "!";
                        break;
                }
                translatedAssembly = String.format(translationTemplate, translatedOperator);
                break;
            case "push": case "pop": {
                String memSeg = VMCode.get(1);
                String index = VMCode.get(2);
                if (memSeg.equals("pointer")) {
                    String pointerTemplate;
                    if (operator.equals("push")) {
                        pointerTemplate = pushPointer;
                    } else {
                        pointerTemplate = popPointer;
                    }
                    if (index.equals("0")) {
                        translatedAssembly = String.format(pointerTemplate, "THIS");
                    } else {
                        translatedAssembly = String.format(pointerTemplate, "THAT");
                    }
                    break;
                }
                String memAddr = ""; // the translated memory address location, e.g. local -> LCL
                switch (memSeg) {
                    // don't need to handle the "pointer" case since it's already been taken
                    // care of when parser parsed the command
                    case "local":
                        memAddr = "LCL";
                        break;
                    case "argument":
                        memAddr = "ARG";
                        break;
                    case "this":
                        memAddr = "THIS";
                        break;
                    case "that":
                        memAddr = "THAT";
                        break;
                    case "static":
                        memAddr = String.format("%s.%s", fileName, index);
                        break;
                }
                if (operator.equals("push")) {
                    String memoryRetrievalFragment;
                    switch (memSeg) {
                        case "constant":
                            memoryRetrievalFragment = "";
                            break;
                        case "temp":
                            memoryRetrievalFragment = retrieveValFromTemp;
                            break;
                        default:
                            memoryRetrievalFragment = String.format(retrieveValFromMem, memAddr);
                            break;
                    }
                    translationTemplate = pushTemplate;
                    translatedAssembly = String.format(translationTemplate, index, memoryRetrievalFragment);
                    break;
                } else {
                    if (memSeg.equals("temp")) {
                        translatedAssembly = String.format(popToTemp, index);
                        break;
                    }
                    translationTemplate = popTemplate;
                    translatedAssembly = String.format(translationTemplate, index, memAddr);
                    break;
                }
            }
            case "label":
                translatedAssembly = String.format("(%s$%s)\n", this.currFunction, VMCode.get(1));
                break;
            case "goto":
                translatedAssembly = String.format("@%s$%s\n0;JMP\n", this.currFunction, VMCode.get(1));
                break;
            case "if-goto":
                translatedAssembly = String.format(getTopOfStack + "@%s$%s\nD;JNE\n", this.currFunction, VMCode.get(1));
                break;
            case "call": {
                String funcName = VMCode.get(1);
                int numArgs = Integer.parseInt(VMCode.get(2));
                this.numReturnLabels += 1;
                StringBuilder sb = new StringBuilder();
                // push return address
                sb.append(String.format(pushReturnAddr, this.numReturnLabels));
                // save the caller's memory segments
                for (String memorySegment : List.of("local", "argument", "this", "that")) {
                    sb.append(compileToAssembly(List.of("push", memorySegment, "0"), fileName));
                }
                sb.append(String.format(repositionARG, numArgs));
                sb.append("@SP\nD=M\n@LCL\nM=D\n"); // set LCL = SP
                sb.append(compileToAssembly(List.of("goto", funcName), fileName));
                sb.append(String.format("(RETURN_%d)\n", this.numReturnLabels));
                translatedAssembly = sb.toString();
                break;
            }
            case "function": {
                String funcName = VMCode.get(1);
                int localVars = Integer.parseInt(VMCode.get(2));
                this.currFunction = funcName;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("(%s)\n", funcName));
                for (int i = 0; i < localVars; i++) {
                    sb.append(compileToAssembly(List.of("push", "constant", "0"), fileName));
                }
                break;
            }


        }
        return translatedAssembly;
    }

    /**
     * @return the code fragment that terminates the program
     */
    public String getEndProgram() {
        return this.endProgram;
    }

    /**
     * Setter for testing purposes
     */
    public void setCurrFunction(String function) {
        this.currFunction = function;
    }
}
