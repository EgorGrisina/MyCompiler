package com.example;

public class Scanner {

    private String inputCodeString;
    private int inputCharsPosition;

    public Scanner(){
    }

    public void setInputCodeString(String input) {
        inputCodeString = input;
        inputCharsPosition = 0;
    }

    private char nextChar(){
        char ch =  inputCodeString.charAt(inputCharsPosition);
        inputCharsPosition++;
        return ch;
    }


}
