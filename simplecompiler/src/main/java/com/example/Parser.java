package com.example;

public class Parser {

    private Scanner mScanner;
    private boolean isError = false;

    public Parser(Scanner scanner) {
        mScanner = scanner;
    }

    public void parse(){
        next();
        program();
        if (!isError) {
            //end of code generation;
        } else {
            System.out.println("End of Parsing with error!");
        }
    }

    private void program(){
        mustBe(Token.TokenName.T_BEGIN);
        statementList();
        mustBe(Token.TokenName.T_END);
        // generateCode;
    }

    // Разбор списка операторов.
    private void statementList() {
        //	  Если список операторов пуст, очередной лексемой будет одна из возможных "закрывающих скобок": END, ELSE, FI.
        //	  Если очередная лексема не входит в этот список, то ее мы считаем началом оператора и вызываем метод statement.
        //    Признаком последнего оператора является отсутствие после оператора точки с запятой.
            boolean more = !(see(Token.TokenName.T_END) || see(Token.TokenName.T_ELSE) || see(Token.TokenName.T_FI));
            while(more) {
                statement();
                mustBe(Token.TokenName.T_SEMICOLON);
                more = !(see(Token.TokenName.T_END) || see(Token.TokenName.T_ELSE) || see(Token.TokenName.T_FI));
            }
    }

    //разбор оператора.
    private void statement() {
        // Если встречаем переменную, то запоминаем ее адрес или добавляем новую если не встретили.
        // Следующей лексемой должно быть присваивание. Затем идет блок expression, который возвращает значение на вершину стека.
        // Записываем это значение по адресу нашей переменной
        if (see(Token.TokenName.T_VAR)) {
            //int varAddr = findAdd(mScanner.getToken().getStringVal());
            next();
            mustBe(Token.TokenName.T_ASSIGN);
            expression();
            //TODO generate code
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
                //TODO generate code
            }
            else {
                //TODO generate code
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
                //TODO generate code
            }
            else {
                //TODO generate code
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
            //TODO generate code
            //Если встретили число, то преобразуем его в целое и записываем на вершину стека
        }
        else if(see(Token.TokenName.T_VAR)) {
            //int varAddress = findOrAddVariable(mScanner.getToken().getStringVal());
            next();
            //TODO generate code
            //Если встретили переменную, то выгружаем значение, лежащее по ее адресу, на вершину стека
        }
        else if(see(Token.TokenName.T_ADDOP) && mScanner.getToken().getStringVal().equals("-")) {
            next();
            factor();
            //TODO generate code
            //Если встретили знак "-", и за ним <factor> то инвертируем значение, лежащее на вершине стека
        }
        else if(match(Token.TokenName.T_LPAREN)) {
            expression();
            mustBe(Token.TokenName.T_RPAREN);
            //Если встретили открывающую скобку, тогда следом может идти любое арифметическое выражение и обязательно
            //закрывающая скобка.
        }
        else {
            System.out.println("expression expected.");
        }
    }

    //разбор условия.
    void relation() {

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
