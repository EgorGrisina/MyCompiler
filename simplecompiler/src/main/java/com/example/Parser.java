package com.example;

public class Parser {

    private Scanner mScanner;
    private Generator mGenerator;
    private boolean isError = false;

    public Parser(Scanner scanner, Generator generator) {
        mScanner = scanner;
        mGenerator = generator;
    }

    public boolean parse(){
        next();
        program();
        if (isError) {
            System.out.println("End of Parsing with error!");
        }
        return !isError;
    }

    private void program(){
        mustBe(Token.TokenName.T_PROGRAM);
        mustBe(Token.TokenName.T_VAR);
        mustBe(Token.TokenName.T_SEMICOLON);

        if (see(Token.TokenName.T_VARIABLES)) {
            next();
            varlist();
        }

        mustBe(Token.TokenName.T_BEGIN);
        statementList();
        mustBe(Token.TokenName.T_END);
        mustBe(Token.TokenName.T_POINT);
        mGenerator.emit(Generator.Instruction.END, "start");
    }

    private void varlist() {
        while (see(Token.TokenName.T_VAR)) {
            mGenerator.addVariable(mScanner.getToken().getStringVal());
            next();
            mustBe(Token.TokenName.T_COLON);
            mustBe(Token.TokenName.T_INT);
            mustBe(Token.TokenName.T_SEMICOLON);
        }
    }

    // Разбор списка операторов.
    private void statementList() {
        //	  Если список операторов пуст, очередной лексемой будет одна из возможных "закрывающих скобок": END, ELSE, FI.
        //	  Если очередная лексема не входит в этот список, то ее мы считаем началом оператора и вызываем метод statement.
        //    Признаком последнего оператора является отсутствие после оператора точки с запятой.
            boolean more = !(see(Token.TokenName.T_END) || see(Token.TokenName.T_ELSE)
                    || see(Token.TokenName.T_FI) || see(Token.TokenName.T_EOF));
            while(more) {
                statement();
                more = !(see(Token.TokenName.T_END) || see(Token.TokenName.T_ELSE)
                        || see(Token.TokenName.T_FI) || see(Token.TokenName.T_EOF));
            }
    }

    //разбор оператора.
    private void statement() {
        // Если встречаем переменную, то запоминаем ее адрес или добавляем новую если не встретили.
        // Следующей лексемой должно быть присваивание. Затем идет блок expression, который возвращает значение на вершину стека.
        // Записываем это значение по адресу нашей переменной
        if (see(Token.TokenName.T_VAR)) {

            String varAddr = mGenerator.findVariable(
                    mScanner.getToken().getStringVal());
            if (varAddr == null) {
                isError = true;
                System.out.println("Error on line " + mScanner.getToken().getLineNumber()
                + " undefined symbol: " +mScanner.getToken().getStringVal());
            }
            next();
            mustBe(Token.TokenName.T_ASSIGN);
            expression();
            mustBe(Token.TokenName.T_SEMICOLON);
            mGenerator.emit(Generator.Instruction.STORE, String.valueOf(varAddr));

        } else if (see(Token.TokenName.T_IF)) {
            // Если встретили IF, то затем должно следовать условие.
            next();
            mustBe(Token.TokenName.T_LPAREN);
            relation();
            int jumpNoAddress = mGenerator.reserve();
            mustBe(Token.TokenName.T_RPAREN);

            mustBe(Token.TokenName.T_THEN);
            statementList();
            if (match(Token.TokenName.T_ELSE)) {
                int jumpAddress = mGenerator.reserve();
                mGenerator.emitAt(jumpNoAddress, Generator.Instruction.JUMP_NO,
                        String.valueOf(mGenerator.getCurrentAddress()));
                statementList();
                mGenerator.emitAt(jumpAddress, Generator.Instruction.JUMP,
                        String.valueOf(mGenerator.getCurrentAddress()));

            } else {
                mGenerator.emitAt(jumpNoAddress, Generator.Instruction.JUMP_NO,
                        String.valueOf(mGenerator.getCurrentAddress()));
            }
            mustBe(Token.TokenName.T_FI);

        } else if (see(Token.TokenName.T_WHILE)) {
            //запоминаем адрес начала проверки условия.
            int conditionAddress = mGenerator.getCurrentAddress();

            next();
            mustBe(Token.TokenName.T_LPAREN);
            relation();
            //резервируем место под инструкцию условного перехода для выхода из цикла.
            int jumpNoAddress = mGenerator.reserve();
            mustBe(Token.TokenName.T_RPAREN);

            mustBe(Token.TokenName.T_BEGIN);
            statementList();
            mustBe(Token.TokenName.T_END);
            //переходим по адресу проверки условия
            mGenerator.emit(Generator.Instruction.JUMP, String.valueOf(conditionAddress));
            //заполняем зарезервированный адрес инструкцией условного перехода на следующий за циклом оператор.
            mGenerator.emitAt(jumpNoAddress, Generator.Instruction.JUMP_NO,
                    String.valueOf(mGenerator.getCurrentAddress()));

        }

    }

    //разбор арифметического выражения.
    private void expression() {
        /*
         Арифметическое выражение описывается следующими правилами: <expression> -> <term> | <term> + <term> | <term> - <term>
         При разборе сначала смотрим первый терм, затем анализируем очередной символ. Если это '+' или '-',
		 удаляем его из потока и разбираем очередное слагаемое (вычитаемое). Повторяем проверку и разбор очередного
		 терма, пока не встретим за термом символ, отличный от '+' и '-'
     */
        term();
        while(see(Token.TokenName.T_ADDOP)) {
            String arithmeticOperation = mScanner.getToken().getStringVal();
            next();
            term();

            if(arithmeticOperation.equals("+")) {
                mGenerator.emit(Generator.Instruction.ADD);
            }
            else {
                mGenerator.emit(Generator.Instruction.SUB);
            }
        }
    }

    //разбор слагаемого.
    void term() {
	 /*
		 Терм описывается следующими правилами: <expression> -> <factor> | <factor> + <factor> | <factor> - <factor>
         При разборе сначала смотрим первый множитель, затем анализируем очередной символ. Если это '*' или '/',
		 удаляем его из потока и разбираем очередное слагаемое (вычитаемое). Повторяем проверку и разбор очередного
		 множителя, пока не встретим за ним символ, отличный от '*' и '/'
	*/
        factor();
        while(see(Token.TokenName.T_MULOP)) {
            String arithmeticOperation = mScanner.getToken().getStringVal();
            next();
            factor();

            if(arithmeticOperation.equals("*")) {
                mGenerator.emit(Generator.Instruction.MULT);
            }
            else {
                mGenerator.emit(Generator.Instruction.DIV);
            }
        }
    }
    //разбор множителя.
    void factor() {
    /*
		Множитель описывается следующими правилами:
		<factor> -> number | identifier | -<factor> | (<expression>)
	*/
        if(see(Token.TokenName.T_INT)) {
            int value = mScanner.getToken().getIntVal();
            next();
            mGenerator.emit(Generator.Instruction.PUSH, String.valueOf(value));
            //Если встретили число, то преобразуем его в целое и записываем на вершину стека
        }
        else if(see(Token.TokenName.T_VAR)) {
            String varAddress = mGenerator.findVariable(mScanner.getToken().getStringVal());
            if (varAddress == null) {
                isError = true;
                System.out.println("Error on line " + mScanner.getToken().getLineNumber()
                        + " undefined symbol: " +mScanner.getToken().getStringVal());
            }
            next();
            mGenerator.emit(Generator.Instruction.LOAD, String.valueOf(varAddress));
            //Если встретили переменную, то выгружаем значение, лежащее по ее адресу, на вершину стека
        }
        else if(see(Token.TokenName.T_ADDOP) && mScanner.getToken().getStringVal().equals("-")) {
            next();
            factor();
            mGenerator.emit(Generator.Instruction.INVERT);
            //Если встретили знак "-", и за ним <factor> то инвертируем значение, лежащее на вершине стека
        }
        else if(match(Token.TokenName.T_LPAREN)) {
            expression();
            mustBe(Token.TokenName.T_RPAREN);
            //Если встретили открывающую скобку, тогда следом может идти любое арифметическое выражение и обязательно
            //закрывающая скобка.
        }
        else {
            System.out.println("Error on line: "+mScanner.getToken().getLineNumber()+" expression expected.");
            isError = true;
        }
    }

    //разбор условия.
    void relation() {
        //Условие сравнивает два выражения по какому-либо из знаков. Каждый знак имеет свой номер. В зависимости от
        //результата сравнения на вершине стека окажется 0 или 1.
        expression();
        if (see(Token.TokenName.T_CMP)) {
            String cmp = mScanner.getToken().getStringVal();
            next();
            expression();

            if (cmp.equals("=")) {
                mGenerator.emit(Generator.Instruction.CMP, "0");
            } else if (cmp.equals("!=")) {
                mGenerator.emit(Generator.Instruction.CMP, "1");
            } else if (cmp.equals("<")) {
                mGenerator.emit(Generator.Instruction.CMP, "2");
            } else if (cmp.equals(">")) {
                mGenerator.emit(Generator.Instruction.CMP, "3");
            } else if (cmp.equals("<=")) {
                mGenerator.emit(Generator.Instruction.CMP, "4");
            } else if (cmp.equals(">=")) {
                mGenerator.emit(Generator.Instruction.CMP, "5");
            }

        } else {
            System.out.println("Error on line: "+mScanner.getToken().getLineNumber()+" comparison operator expected.");
            isError = true;
        }

    }

    //проверяем, совпадает ли данная лексема с образцом. Если да, то лексема изымается из потока.
    //Иначе создаем сообщение об ошибке и пробуем восстановиться
    private void mustBe(Token.TokenName tokenName) {
        if (!match(tokenName)) {
            isError = true;
            System.out.println("Error on line: "+mScanner.getToken().getLineNumber()
            +" " + mScanner.getToken().getTokenName().toString()+ " founded while " +
            tokenName.toString()+ " expected.");

            // Попытка восстановления после ошибки.
            recover(tokenName);
        } else {
            System.out.println("mustBe and founded: "+tokenName.toString());
        }
    }

    //восстановление после ошибки: идем по коду до тех пор,
    //пока не встретим эту лексему или лексему конца файла.
    private void recover(Token.TokenName tokenName) {
        while(!see(tokenName) && !see(Token.TokenName.T_EOF)) {
            next();
        }

        if(see(tokenName)) {
            System.out.println("Recovered, "+tokenName.toString()+ " founded");
            next();
        } else {
            System.out.println("Don't recovered, EOF");
        }
    }

    // Сравнение текущей лексемы с образцом. Текущая позиция в потоке лексем не изменяется.
    private boolean see(Token.TokenName tokenName) {
        return  (mScanner.getToken().getTokenName() == tokenName);
    }
    // Проверка совпадения текущей лексемы с образцом. Если лексема и образец совпадают,
    // лексема изымается из потока.
    private boolean match(Token.TokenName tokenName) {
        if (mScanner.getToken().getTokenName() == tokenName) {
            mScanner.nextToken();
            return true;
        } else {
            return false;
        }
    }

    // Переход к следующей лексеме.
    private void next() {
        mScanner.nextToken();
    }
}
