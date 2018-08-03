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
                "M=D+M\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("add"));
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
                "M=D&M\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("and"));
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
                "M=D-M\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("sub"));
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
                "M=D|M\n" +
                "@SP\n" +
                "M=M+1\n";

        String translation = translator.compileToAssembly(List.of("or"));
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

        assertEquals(expected, translator.compileToAssembly(List.of("neg")));
    }

    @Test
    void translateNot() {
        String expected = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "M=!M\n" +
                "@SP\n" +
                "M=M+1\n";

        assertEquals(expected, translator.compileToAssembly(List.of("not")));
    }
}