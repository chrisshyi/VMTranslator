package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import main.Parser;

import java.util.List;

class ParserTest {

    private static Parser parser;

    @BeforeAll
    static void setUp() {
        parser = new Parser();
    }

    @Test
    void testParseArithmetic() {
        String line = "add   // add the two numbers";
        List<String> splitLine = parser.parse(line);
        assertEquals(1, splitLine.size());
        assertEquals("add", splitLine.get(0));
    }

    @Test
    void testParseLogical() {
        String line = "and   // and the top two values";
        List<String> splitLine = parser.parse(line);
        assertEquals(1, splitLine.size());
        assertEquals("and", splitLine.get(0));
    }

    @Test
    void testMemoryAccess() {
        String line = "pop local 2 // pops the top of the stack into local 2";
        List<String> splitLine = parser.parse(line);
        assertEquals(3, splitLine.size());
        assertEquals(List.of("pop", "local", "2"), splitLine);
    }
}