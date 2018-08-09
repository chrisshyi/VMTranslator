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
        // note that the "file" in inputFilePath is used interchangeably with directory
        String inputFilePath = args[0];
        // if the input file is a directory and its path ends with the separator,
        // truncate it
        if (inputFilePath.charAt(inputFilePath.length() - 1) == File.separatorChar) {
            inputFilePath = inputFilePath.substring(0, inputFilePath.length() - 1);
        }
        String outputFilePath;
        String inputFileName; // used for static memory segment
        File inputFile = new File(inputFilePath);
        File[] filesToTranslate;
        boolean inputIsDirectory = false;
        if (inputFile.isDirectory()) {
            inputIsDirectory = true;
            filesToTranslate = inputFile.listFiles();
            int lastSeparatorInPath = inputFilePath.lastIndexOf(File.separatorChar);
            inputFileName = inputFilePath.substring(lastSeparatorInPath + 1, inputFilePath.length());
            outputFilePath = inputFilePath + inputFileName + ".asm";
        } else {
            filesToTranslate = new File[1];
            filesToTranslate[0] = inputFile;
            int lastPeriodInPath = inputFilePath.lastIndexOf('.');
            int lastSeparatorInPath = inputFilePath.lastIndexOf(File.separatorChar);
            String inputFileFullPathNoExtension = inputFilePath.substring(0, lastPeriodInPath);
            inputFileName = inputFilePath.substring(lastSeparatorInPath + 1, lastPeriodInPath);
            outputFilePath = inputFileFullPathNoExtension + ".asm";
        }

        File outputFile = new File(outputFilePath);
        Parser parser = new Parser();
        Translator translator = new Translator();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            if (inputIsDirectory) {
                bw.write("SP=256\n" +
                        "call Sys.init\n");
            }
            for (File fileToTranslate : filesToTranslate) {
                try (BufferedReader br = new BufferedReader(new FileReader(fileToTranslate))) {
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
    }
}
