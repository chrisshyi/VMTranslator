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
        int lastSeparatorInPath = inputFilePath.lastIndexOf(File.separatorChar);
        String inputFileFullPathNoExtension = inputFilePath.substring(0, lastPeriodInPath);
        String inputFileName = inputFilePath.substring(lastSeparatorInPath + 1, lastPeriodInPath);

        String outputFilePath = inputFileFullPathNoExtension + ".asm";
        File outputFile = new File(outputFilePath);
        Parser parser = new Parser();
        Translator translator = new Translator();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
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
