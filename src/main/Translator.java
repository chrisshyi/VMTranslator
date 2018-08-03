package main;

import java.util.List;

public class Translator {
    /*
     * Start by defining string templates to avoid code repetition
     */
    private String incrementSP = "@SP\n" +
            "M=M+1\n";

    private String decrementSP = "@SP\n" +
            "M=M-1\n";

    // retrieves the value at the top of the stack and decrements SP (effectively a pop)
    private String getTopOfStack = decrementSP +
            "A=M\n" +
            "D=M\n";

    // pushes a true value onto the stack
    private String pushTrue = "(TRUE)\n" + "@SP\n" +
            "A=M\n" +
            "M=-1\n" +
            incrementSP;

    // pushes a false value onto the stack
    private String pushFalse = "(FALSE)\n" + "@SP\n" +
            "A=M\n" +
            "M=0\n" +
            incrementSP;
    // template for eg, gt and lt
    private String comparisonTemplate = getTopOfStack +
            decrementSP +
            "A=M\n" +
            "D=D-M\n" +
            "@TRUE\n" +
            "D;%s\n" +
            "@FALSE\n" +
            "0;JMP\n";

    // template for binary arithmetic and logical operations
    // add, sub, and, or
    private String binaryArithAndLogicTemplate = decrementSP +
            "A=M\n" +
            "D=M\n" +
            decrementSP +
            "A=M\n" +
            "M=D%sM\n" +
            incrementSP;

    // template for unary arithmetic and logical operations
    // neg, not
    private String unaryArithAndLogicTemplate = decrementSP +
            "A=M\n" +
            "M=%sM\n" +
            incrementSP;

    // template for pushing values from memory segment(s) onto the stack
    private String pushTemplate = "@%s\n" +
            "D=A\n" +
            "@%s\n" +
            "A=D+M\n" +
            "D=M\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            incrementSP;

    private String popTemplate = "@%s\n" +
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
     * @return the input translated into Hack assembly code
     */
    public String compileToAssembly(List<String> VMCode) {
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
                if (operator.equals("push")) {
                    translationTemplate = pushTemplate;
                } else {
                    translationTemplate = popTemplate;
                }
                translatedAssembly = String.format(translationTemplate, VMCode.get(2), VMCode.get(1));
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
