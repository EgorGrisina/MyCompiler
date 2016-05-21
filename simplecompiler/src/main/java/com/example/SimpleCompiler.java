package com.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleCompiler {

    public static void main(String[] args) {
        System.out.println("Start MyCompiler");

        System.out.println("Reading input file");

        String inputCode = "";
        String buffer = "";
        FileInputStream stream = null;
        try {
            stream = new FileInputStream("input.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Can't open input file");
            return;
        }
        InputStreamReader reader = new InputStreamReader( stream );
        BufferedReader buffered_reader = new BufferedReader( reader );

        try {
            while ( (buffer = buffered_reader.readLine()) != null) {
                buffer.replace(" ",""); // replacing spaces
                inputCode += buffer;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading input file");
            return;
        }

        System.out.println("File reading completed");
        System.out.println(inputCode);


    }

}
