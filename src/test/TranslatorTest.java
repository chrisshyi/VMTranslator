package test;

import main.Translator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TranslatorTest {

    private static Translator translator;

    @BeforeAll
    static void setUp() {
        translator = new Translator();
    }

    @Test
    void translateAddition() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "M=M+D\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("add"), "");
        assertEquals(expected, translation);
    }

    @Test
    void translateAnd() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "M=M&D\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("and"), "");
        assertEquals(expected, translation);
    }

    @Test
    void translateSub() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "M=M-D\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("sub"), "");
        assertEquals(expected, translation);
    }

    @Test
    void translateOr() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "M=M|D\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("or"), "");
        assertEquals(expected, translation);
    }

    @Test
    void translateNeg() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "M=-M\n" +
                "@SP\n" +
                "M=M+1\n";

        assertEquals(expected, translator.compileToAssembly(List.of("neg"), ""));
    }

    @Test
    void translateNot() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "M=!M\n" +
                "@SP\n" +
                "M=M+1\n";

        assertEquals(expected, translator.compileToAssembly(List.of("not"), ""));
    }

    @Test
    void translatePointerPush() {
        String expected = "@THIS\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";
        assertEquals(expected, translator.compileToAssembly(List.of("push", "pointer", "0"), ""));
    }

    @Test
    void translatePointerPop() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@THAT\n" +
                "M=D\n";
        assertEquals(expected, translator.compileToAssembly(List.of("pop", "pointer", "1"), ""));
    }

    @Test
    void translateLabel() {
        translator.setCurrFunction("currFunc");
        String expected = "(currFunc$LABEL1)\n";
        assertEquals(expected, translator.compileToAssembly(List.of("label", "LABEL1"), ""));
    }

    @Test
    void translateGoto() {
        translator.setCurrFunction("currFunc");
        String expected = "@currFunc$LABEL1\n0;JMP\n";
        assertEquals(expected, translator.compileToAssembly(List.of("goto", "LABEL1"), ""));
    }

    @Test
    void translateIfGoto() {
        translator.setCurrFunction("currFunc");
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@currFunc$LABEL1\n" +
                "D;JNE\n";
        assertEquals(expected, translator.compileToAssembly(List.of("if-goto", "LABEL1"), ""));
    }
}