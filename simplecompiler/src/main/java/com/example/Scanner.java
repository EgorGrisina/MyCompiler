package com.example;

public class Scanner {

    private String inputCodeString;
    private int inputCharsPosition;
    private char ch_;

    public Scanner() {
    }

    public void setInputCodeString(String input) {
        inputCodeString = input;
        inputCharsPosition = 0;
        nextChar();
    }

    private void nextChar() {
        if (inputCharsPosition < inputCodeString.length()) {
            ch_ = inputCodeString.charAt(inputCharsPosition);
            inputCharsPosition++;
        } else {
            ch_ = 0;
        }
    }

    public Token getToken() {

        skipSpace();

        Token mToken = new Token();

        //изначально все лексемы считаем неизвестными
        mToken.setTokenName(Token.TokenName.T_ILLEGAL);

        //Если встречен конец файла, считаем за лексему конца файла.
        if (ch_ == 0) {
            mToken.setTokenName(Token.TokenName.T_EOF);
            return mToken;
        }

        // Пропускаем комментарии
        // Если встречаем "/", то за ним должна идти "*". Если "*" не встречена, считаем, что встретили операцию деления
        // и лексему - операция типа умножения. Дальше смотрим все символы, пока не находим звездочку или символ конца файла.
        // Если нашли * - проверяем на наличие "/" после нее. Если "/" не найден - ищем следующую "*".
        if (ch_ == '/') {
            nextChar();
            if (ch_ == '*') {
                nextChar();
                boolean inside = true;
                while (inside) {
                    while (ch_ != '*' || ch_ != 0) {
                        nextChar();
                    }

                    if (ch_ == 0) {
                        mToken.setTokenName(Token.TokenName.T_EOF);
                        return mToken;
                    }

                    nextChar();
                    if (ch_ == '/') {
                        inside = false;
                        nextChar();
                        skipSpace();
                    }
                }
            } else {
                mToken.setTokenName(Token.TokenName.T_MULOP);
                mToken.setStringVal("/");
                nextChar();
                return mToken;
            }
        }

        //Если встретили цифру, то до тех пока дальше идут цифры - считаем как продолжение числа.
        //Запоминаем полученное целое, а за лексему считаем целочисленный литерал
        if (isDigit(ch_)) {
            String digit = "";
            while (isDigit(ch_)) {
                digit += ch_;
                nextChar();
            }

            int value = Integer.valueOf(digit);
            mToken.setTokenName(Token.TokenName.T_INT);
            mToken.setIntVal(value);
            return mToken;
        }

        //Если же следующий символ - буква ЛА - тогда считываем до тех пор, пока дальше буквы ЛА или цифры.
        //Как только считали имя переменной, сравниваем ее со списком зарезервированных слов. Если не совпадает ни с одним из них,
        //считаем, что получили переменную, имя которой запоминаем, а за текущую лексему считаем лексему идентификатора.
        //Если совпадает с каким-либо словом из списка - считаем что получили лексему, соответствующую этому слову.
        if (isIdentifierStart(ch_)) {
            String buffer = "";
            while (isIdentifierBody(ch_)) {
                buffer += ch_;
                nextChar();
            }

            for (Token.KeyWords keyWord : Token.KeyWords.values()) {

                if (buffer.equals(keyWord.name())) {

                    mToken.setTokenName(getTokenName(keyWord));
                    return mToken;

                } else {

                    mToken.setTokenName(Token.TokenName.T_VAR);
                    mToken.setStringVal(buffer);
                    return mToken;
                }
            }
        }

        //Символ не является буквой, цифрой, "/" или признаком конца файла
        switch (ch_) {
            //Признак лексемы открывающей скобки - встретили "("
            case '(':
                mToken.setTokenName(Token.TokenName.T_LPAREN);
                nextChar();
                break;
            //Признак лексемы закрывающей скобки - встретили ")"
            case ')':
                mToken.setTokenName(Token.TokenName.T_RPAREN);
                nextChar();
                break;
            //Признак лексемы ";" - встретили ";"
            case ';':
                mToken.setTokenName(Token.TokenName.T_SEMICOLON);
                nextChar();
                break;
            //Если встречаем "=", то дальше смотрим наличие символа "=". Если находим, то считаем что нашли лексему сравнения "=="
            //инчае лексема присваивания "="
            case '=':
                nextChar();
                if (ch_ == '=') {
                    mToken.setTokenName(Token.TokenName.T_CMP);
                    mToken.setStringVal("==");
                    nextChar();

                } else {
                    mToken.setTokenName(Token.TokenName.T_ASSIGN);
                    nextChar();
                }
                break;
            //Если встретили символ "<", то либо следующий символ "=", тогда лексема нестрогого сравнения. Иначе - строгого.
            case '<':
                mToken.setTokenName(Token.TokenName.T_CMP);
                nextChar();
                if (ch_ == '=') {
                    mToken.setStringVal("<=");
                    nextChar();
                } else {
                    mToken.setStringVal("<");
                }
                break;
            //Аналогично предыдущему случаю
            case '>':
                mToken.setTokenName(Token.TokenName.T_CMP);
                nextChar();
                if (ch_ == '=') {
                    mToken.setStringVal(">=");
                    nextChar();
                } else {
                    mToken.setStringVal(">");
                }
                break;
            //Если встретим "!", то дальше должно быть "=", тогда считаем, что получили лексему сравнения
            //и знак "!=" иначе считаем, что у нас лексема ошибки
            case '!':
                nextChar();
                if (ch_ == '=') {
                    nextChar();
                    mToken.setTokenName(Token.TokenName.T_CMP);
                    mToken.setStringVal("!=");
                } else {
                    mToken.setTokenName(Token.TokenName.T_ILLEGAL);
                }
                break;
            //Знаки операций. Для "+"/"-" получим лексему операции типа сложнения, и соответствующую операцию.
            //для "*" - лексему операции типа умножения
            case '+':
                mToken.setTokenName(Token.TokenName.T_ADDOP);
                mToken.setStringVal("+");
                nextChar();
                break;

            case '-':
                mToken.setTokenName(Token.TokenName.T_ADDOP);
                mToken.setStringVal("-");
                nextChar();
                break;

            case '*':
                mToken.setTokenName(Token.TokenName.T_MULOP);
                mToken.setStringVal("*");
                nextChar();
                break;
            //Иначе лексема ошибки.
            default:
                mToken.setTokenName(Token.TokenName.T_ILLEGAL);
                nextChar();
                break;
        }

        return mToken;
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c);
    }

    private boolean isIdentifierBody(char c) {
        return isIdentifierStart(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private Token.TokenName getTokenName(Token.KeyWords keyWord) {

        if (keyWord == Token.KeyWords.BEGIN) {

            return (Token.TokenName.T_BEGIN);

        } else if (keyWord == Token.KeyWords.END) {

            return (Token.TokenName.T_END);

        } else if (keyWord == Token.KeyWords.WHILE) {

            return (Token.TokenName.T_WHILE);

        } else if (keyWord == Token.KeyWords.IF) {

            return (Token.TokenName.T_IF);

        } else if (keyWord == Token.KeyWords.THEN) {

            return (Token.TokenName.T_THEN);

        } else if (keyWord == Token.KeyWords.ELSE) {

            return (Token.TokenName.T_ELSE);

        } else if (keyWord == Token.KeyWords.FI) {

            return (Token.TokenName.T_FI);
        }

        return Token.TokenName.T_ILLEGAL;

    }

    public void skipSpace() {
        while (ch_ == ' ') {
            nextChar();
        }
    }

}
