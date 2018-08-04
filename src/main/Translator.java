package main;

import java.util.List;

public class Translator {
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

    // pushes a true value onto the stack
    private final String pushTrue = "(TRUE)\n" + "@SP\n" +
            "A=M\n" +
            "M=-1\n" +
            incrementSP;

    // pushes a false value onto the stack
    private final String pushFalse = "(FALSE)\n" + "@SP\n" +
            "A=M\n" +
            "M=0\n" +
            incrementSP;
    // template for eg, gt and lt
    private final String comparisonTemplate = getTopOfStack +
            decrementSP +
            "A=M\n" +
            "D=D-M\n" +
            "@TRUE\n" +
            "D;%s\n" +
            "@FALSE\n" +
            "0;JMP\n";

    // template for binary arithmetic and logical operations
    // add, sub, and, or
    private final String binaryArithAndLogicTemplate = decrementSP +
            "A=M\n" +
            "D=M\n" +
            decrementSP +
            "A=M\n" +
            "M=D%sM\n" +
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
        switch (operator) {
            case "eq": case "gt": case "lt":
                translationTemplate = comparisonTemplate;
                if (operator.equals("eq")) {
                    translatedOperator = "JEQ";
                } else if (operator.equals("gt")) {
                    translatedOperator = "JGT";
                } else {
                    translatedOperator = "JLT";
                }
                translatedAssembly = String.format(translationTemplate, translatedOperator);
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
                if (operator.equals("neg")) {
                    translatedOperator = "-";
                } else {
                    translatedOperator = "!";
                }
                translatedAssembly = String.format(translationTemplate, translatedOperator);
                break;
            case "push": case "pop":
                String memSeg = VMCode.get(1);
                String index = VMCode.get(2);
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
                    case "temp":
                        memAddr = "5";
                        break;
                    case "static":
                        memAddr = String.format("%s.%s", fileName, index);
                        break;
                }
                if (operator.equals("push")) {
                    String memoryRetrievalFragment;
                    if (memSeg.equals("constant")) {
                        memoryRetrievalFragment = "";
                    } else {
                        memoryRetrievalFragment = String.format(retrieveValFromMem, memAddr);
                    }
                    translationTemplate = pushTemplate;
                    translatedAssembly = String.format(translationTemplate, index, memoryRetrievalFragment);
                    break;
                } else {
                    translationTemplate = popTemplate;
                    translatedAssembly = String.format(translationTemplate, index, memAddr);
                }
        }
        return translatedAssembly;
    }

    /**
     * @return the code fragment that pushes a true value onto the stack
     */
    public String getPushTrue() {
        return this.pushTrue;
    }

    /**
     * @return the code fragment that pushes a false value onto the stack
     */
    public String getPushFalse() {
        return this.pushFalse;
    }
}
