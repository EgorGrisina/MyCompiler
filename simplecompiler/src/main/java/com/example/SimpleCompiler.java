package com.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleCompiler {

    final static String INPUT_FILE_NAME = "files/input.txt";

    public static void main(String[] args) {
        System.out.println("Start MyCompiler");

        InputFileReader mInputFileReader = new InputFileReader(INPUT_FILE_NAME);
        String inputCode = mInputFileReader.readFile();

        Scanner mScanner = new Scanner();
        mScanner.setInputCodeString(inputCode);




    }

}
