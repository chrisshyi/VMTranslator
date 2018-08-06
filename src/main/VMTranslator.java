package main;

import java.io.*;
import java.nio.Buffer;
import java.util.List;

public class VMTranslator {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java VMTranslator inputFile");
            return;
        }
        String inputFilePath = args[0];
        File inputFile = new File(inputFilePath);
        int lastPeriodInPath = inputFilePath.lastIndexOf('.');
        String inputFileName = inputFilePath.substring(0, lastPeriodInPath);

        String outputFilePath =inputFileName + ".asm";
        File outputFile = new File(outputFilePath);
        Parser parser = new Parser();
        Translator translator = new Translator();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            // write code fragments for pushing true and false values onto the stack
//            bw.write(translator.getPushFalse());
//            bw.write(translator.getPushTrue());
            while ((line = br.readLine()) != null) {
                // skip comment line
                if (line.startsWith("/") || line.length() == 0) {
                    continue;
                }
                List<String> parsedLine = parser.parse(line);
                bw.write(translator.compileToAssembly(parsedLine, inputFileName));
            }
        }
    }
}
