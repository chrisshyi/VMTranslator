package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
//
//    private File inputFile; // TODO: Does Parser need to wrap the file?? Maybe it does not
//    private BufferedReader reader;
//
//    public Parser(File inputFile) throws FileNotFoundException {
//        this.inputFile = inputFile;
//        this.reader = new BufferedReader(new FileReader(inputFile));
//    }

    /**
     * Empty constructor for testing
     */
    public Parser() {

    }

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
        if (splitLine.get(0).equals("push") || splitLine.get(0).equals("pop")) {
            desiredLength = 3;
            // if attempting to change pointer, change it to this or that
            if (splitLine.get(1).equals("pointer")) {
                splitLine.subList(1, splitLine.size());
                if (splitLine.get(2).equals("0")) {
                    splitLine.add("this");
                    splitLine.add("0");
                } else {
                    splitLine.add("that");
                    splitLine.add("0");
                }
            }
        } else {
            desiredLength = 1;
        }
        splitLine.subList(desiredLength, splitLine.size()).clear();
        return splitLine;
    }
}
