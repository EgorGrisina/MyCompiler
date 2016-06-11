package com.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleCompiler {

    final static String INPUT_FILE_NAME = "files/inputIFWHILE.txt";
    final static String OUTPUT_FILE_NAME = "files/outputASM.txt";

    public static void main(String[] args) {
        System.out.println("Start MyCompiler");

        InputFileReader mInputFileReader = new InputFileReader(INPUT_FILE_NAME);
        String inputCode = mInputFileReader.readFile();

        Scanner mScanner = new Scanner();
        mScanner.setInputCodeString(inputCode);

        Generator mGenerator = new Generator();

        Parser mParser = new Parser(mScanner, mGenerator);
        System.out.println("Start parsing");
        boolean done = mParser.parse();
        System.out.println("End parsing");

        if (done) {
            mInputFileReader.saveFile(mGenerator.getAssemblerCode(), OUTPUT_FILE_NAME);
            System.out.println("Generated code saved to file: "+OUTPUT_FILE_NAME);
        }


    }

}
