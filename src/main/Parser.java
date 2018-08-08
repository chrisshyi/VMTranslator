package main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    /**
     * Parses a line from the input file and splits it into its fields
     * @param line a line of VM code from the input file
     * @return the line split into its fields
     */
    public List<String> parse(String line) {
        // if the entire line is a comment, return an empty list
        if (line.startsWith("/")) {
            return new ArrayList<>();
        }
        // split the line at where the comment starts
        List<String> splitLine = new ArrayList<>(Arrays.asList(line.split("[/\\s]")));
        int desiredLength;
        String operation = splitLine.get(0);
        switch (operation) {
            case "push": case "pop": case "function": case "call":
                desiredLength = 3;
                break;
            case "label": case "goto": case "if-goto":
                desiredLength = 2;
                break;
            default:
                desiredLength = 1;
                break;
        }
        splitLine.subList(desiredLength, splitLine.size()).clear();
        return splitLine;
    }
}
