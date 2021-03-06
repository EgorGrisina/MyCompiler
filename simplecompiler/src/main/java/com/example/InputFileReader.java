package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;

public class InputFileReader {

    String fileName;

    public InputFileReader(String fileName) {
        this.fileName = fileName;
    }

    public String readFile(){
        System.out.println("Reading input file");

        String inputCode = "";
        String buffer = "";
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Can't open input file");
            return null;
        }
        InputStreamReader reader = new InputStreamReader( stream );
        BufferedReader buffered_reader = new BufferedReader( reader );

        try {
            while ( (buffer = buffered_reader.readLine()) != null) {

                if (!buffer.equals("")) {
                    buffer = clearString(buffer)+"\n";
                    inputCode += buffer;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading input file");
            return null;
        }

        System.out.println("File reading completed");
        System.out.println(inputCode);

        return inputCode;
    }

    private String clearString(String string) {

        string = string.replace("\t", " ");
        string = string.replace("\r", " ");

        return string;
    }

    public void saveFile(String code, String outFileName) {
        File file = new File(outFileName);

        try {

            if(!file.exists()){
                file.createNewFile();
            }

            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                out.print(code);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
