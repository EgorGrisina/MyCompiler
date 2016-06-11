package com.example;

public class Token {

    public enum TokenName {
        T_EOF,			// Конец текстового потока
        T_ILLEGAL,		// Признак недопустимого символа
        T_VAR,   		// Переменная
        T_INT,		// Целочисленный литерал
        T_BEGIN,		// Ключевое слово "begin"
        T_END,			// Ключевое слово "end"
        T_IF,			// Ключевое слово "if"
        T_THEN,			// Ключевое слово "then"
        T_ELSE,			// Ключевое слово "else"
        T_FI,			// Ключевое слово "fi"
        T_WHILE,		// Ключевое слово "while"
        T_ASSIGN,		// Оператор ":="
        T_ADDOP,		// Сводная лексема для "+" и "-" (операция типа сложения)
        T_MULOP,		// Сводная лексема для "*" и "/" (операция типа умножения)
        T_CMP,			// Сводная лексема для операторов отношения
        T_LPAREN,		// Открывающая скобка
        T_RPAREN,		// Закрывающая скобка
        T_SEMICOLON,	// ";"
        T_COLON,	    // ";"
        T_PROGRAM,
        T_VARIABLES,
        T_POINT
    }

    public enum KeyWords {
        WHILE,
        BEGIN,
        END,
        ELSE,
        THEN,
        FI,
        IF,
        PROGRAM,
        Integer,
        VAR
    }

    private Integer intVal = null;
    private String stringVal = null;
    private TokenName tokenName = null;
    private int lineNumber = 0;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getIntVal() {
        return intVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public TokenName getTokenName() {
        return tokenName;
    }

    public void setTokenName(TokenName tokenName) {
        this.tokenName = tokenName;
    }


    public String toString(){
        String string = tokenName.name();
        if (stringVal != null) {
            string+="("+stringVal+")";
        } else if (intVal != null) {
            string+="("+intVal+")";
        }
        string+="[line:"+lineNumber+"]";
        return string;
    }
}
