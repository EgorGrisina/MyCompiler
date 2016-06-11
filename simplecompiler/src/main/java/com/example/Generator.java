package com.example;

import java.util.ArrayList;
import java.util.List;

public class Generator {

    private List<Command> commandBuffer = new ArrayList();
    private List<String> variables = new ArrayList<>();

    // Добавление инструкции без аргументов в конец программы
    public void emit(Instruction instruction) {
        commandBuffer.add(new Command(instruction));
    }
    // Добавление инструкции с одним аргументом в конец программы
    public void emit(Instruction instruction, String arg) {
        commandBuffer.add(new Command(instruction, arg));
    }
    // Добавление инструкции с двумя аргументами в конец программы
    public void emit(Instruction instruction, String arg1, String arg2) {
        commandBuffer.add(new Command(instruction, arg1, arg2));
    }
    // Запись инструкции с одним аргументом по указанному адресу
    public void emitAt(int address, Instruction instruction) {
        commandBuffer.set(address, new Command(instruction));
    }
    public void emitAt(int address, Instruction instruction, String arg) {
        commandBuffer.set(address, new Command(instruction, arg));
    }
    public void emitAt(int address, Instruction instruction, String arg1, String arg2) {
        commandBuffer.set(address, new Command(instruction,arg1,arg2));
    }

    // Получение адреса, непосредственно следующего за последней инструкцией в программе
    public int getCurrentAddress(){
        return commandBuffer.size();
    }

    // Формирование "пустой" инструкции (NOP) и возврат ее адреса
    public int reserve(){
        emit(Instruction.NOP);
        return commandBuffer.size() - 1;
    }

    public String getAssemblerCode(){
        String result = "";

        if (variables.size() > 0) {
            result += " \t .data \n";
            for (String var : variables) {
                result += var + "\t"+"word"+"\t?\n";
            }
        }
        result += ";\n";
        result += "\t.code\nstart:\n";


        for (Command command : commandBuffer) {
            result += command.toString() + "\n";
        }
        return result;
    }

    public String addVariable(String varName) {

        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).equals(varName)) {
                return varName;
            }
        }
        variables.add(varName);
        return varName;
    }

    public String findVariable(String varName) {

        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).equals(varName)) {
                return varName;
            }
        }
        return null;
    }

    public enum Instruction{
        NOP,		// отсутствие операции
        END,		// остановка машины, завершение работы программы
        LOAD,		// LOAD addr - загрузка слова данных в стек из памяти по адресу addr
        STORE,		// STORE addr - запись слова данных с вершины стека в память по адресу addr
        PUSH,		// PUSH n - загрузка в стек константы n
        ADD,		// сложение двух слов на вершине стека и запись результата вместо них
        SUB,		// вычитание двух слов на вершине стека и запись результата вместо них
        MULT,		// умножение двух слов на вершине стека и запись результата вместо них
        DIV,		// деление двух слов на вершине стека и запись результата вместо них
        INVERT,		// изменение знака слова на вершине стека
        CMP,	    // сравнение двух слов на вершине стека с помощью операции сравнения с кодом cmp
        JUMP,		// JUMP addr - безусловный переход по адресу addr
        JUMP_YES,	// JUMP_YES addr - переход по адресу addr, если на вершине стека значение 1
        JUMP_NO,	// JUMP_NO addr - переход по адресу addr, если на вершине стека значение 0

    }

    class Command {
        Instruction mInstruction;
        String arg1;
        String arg2;

        public Command(Instruction instruction) {
            mInstruction = instruction;
            arg1 = null;
            arg2 = null;
        }

        public Command(Instruction instruction, String a1) {
            mInstruction = instruction;
            arg1 = a1;
            arg2 = null;
        }

        public Command(Instruction instruction, String a1, String a2) {
            mInstruction = instruction;
            arg1 = a1;
            arg2 = a2;
        }

        public String toString(){
            String command = mInstruction.name().toLowerCase();
            if (arg1 != null) {
                command += " "+arg1;
            }
            if (arg2 != null) {
                command += ", "+arg2;
            }
            if (mInstruction != Instruction.END) {
                command = "\t"+command;
            }
            return command;
        }
    }

}
