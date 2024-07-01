package com.example.functionRepositoryServer.service.MathModels.Search;

import com.example.functionRepositoryServer.service.MathModels.Operation;
import com.example.functionRepositoryServer.service.Tools.AnaliseExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Политика простого поиска аргументов с двух сторон от индификатора
 */
public class TwoSidesSearchModel implements Operation {
    String operationIndex;

    /**
     * Введите индефикатор операции
     * @param operationIndex
     */
    public TwoSidesSearchModel(String operationIndex) {
        this.operationIndex = operationIndex;
    }

    public TwoSidesSearchModel() {}

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression anaizer) {
        return 0;
    }

    @Override
    public boolean isThisOperation(String operation) {
        return operation.equals(operationIndex);
    }

    /**
     * Найти аргументы с левой строны
     * @param expr
     * @param positionIndex
     * @return
     */
    public List<String> searchLeftArguments(List<String> expr, int positionIndex) {
        try {
            List<String> argLeft = new ArrayList<>();
            if (!expr.get(positionIndex - 1).equals(")")) {
                argLeft.add(expr.get(positionIndex - 1));
            } else {
                argLeft = compareLeft(expr, positionIndex - 1);
                //arguments.addAll(argLeft);
            }

            return argLeft;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Собрать выраженив в скобках с левой стороны
     * @param exArr
     * @param indexStart
     * @return
     */
    public List<String> compareLeft(List<String> exArr, int indexStart){
        List<String> temp = new ArrayList<>();

        byte Continue = 0;
        byte closeCount = 1;
        int index = indexStart - 1;
        while(Continue < closeCount && index > -1){
            if(exArr.get(index).equals(")")){
                closeCount++;
            }

            temp.add(exArr.get(index));
            Continue += exArr.get(index).equals("(") ? 1 : 0;
            index--;
        }
        temp.remove(temp.size() - 1);

        Collections.reverse(temp);
        return temp;
    }

    /**
     * Найти аргументы с правой строны
     * @param expr
     * @param positionIndex
     * @return
     */
    public List<String> searchRightArguments(List<String> expr, int positionIndex) {
        try {
            List<String> argRight = new ArrayList<>();
            if (!expr.get(positionIndex + 1).equals("(")) {
                argRight.add(expr.get(positionIndex + 1));
            } else {
                argRight = compareRight(expr, positionIndex + 1);
                //arguments.addAll(argRight);
            }

            return argRight;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Собрать выраженив в скобках с правой стороны
     * @param exArr
     * @param indexStart
     * @return
     */
    public List<String> compareRight(List<String> exArr, int indexStart){
        List<String> temp = new ArrayList<>();

        byte Continue = 0;
        byte closeCount = 1;
        int index = indexStart + 1;
        while(Continue < closeCount && index < exArr.size()){
            if(exArr.get(index).equals("("))
                closeCount++;

            temp.add(exArr.get(index));
            Continue += exArr.get(index).equals(")") ? 1 : 0;
            index++;
        }
        temp.remove(temp.size() - 1);

        return temp;
    }

    /**
     * Инкапслировано ли уже выражение
     * @param start
     * @param end
     * @param expression
     * @return
     */
    protected boolean inBrackets(int start, int end, List<String> expression){
        String[] bracketsStart = "[{(".split("");
        String[] bracketsEnd= "]})".split("");
        for (int i = 0; i < 3; i++) {
            if(start > 0 && expression.get(start).equals(bracketsStart[i]) ||
                    end < expression.size() && expression.get(end).equals(bracketsEnd[i])){
                return true;
            }
        }

        return false;
    }

    /**
     * Евляеться ли элемент скобкой
     * @param testIndex
     * @param expression
     * @return
     */
    protected boolean isBracket(int testIndex, List<String> expression){
        String[] brackets = "[{(]})".split("");
        try {
            for (int i = 0; i < 6; i++) {
                if(expression.get(testIndex).equals(brackets[i])) {
                    return true;
                }
            }
        } catch (IndexOutOfBoundsException e){
            return false;
        }

        return false;
    }

    /**
     * Инкапсулировать выражение
     * @param positionIndex
     * @param expression
     * @return
     */
    protected int encapsulateUncertainty(int positionIndex, List<String> expression){
        int stepIndex = positionIndex + 2;
        //Обораичвание части с незвестной переменной
        //Если часть уже инкапсулировано или один из аргуметов является скобокой
        //или впереди вообще другая функция, то игнорируем ее
        if(inBrackets(positionIndex - 2, positionIndex + 2, expression) ||
                isBracket(positionIndex + 1, expression) ||
                isBracket(positionIndex - 1, expression) ||
                isBracket(positionIndex - 2, expression)
        ){
            return stepIndex;
        }

        //Если нет - икапсулируем
        List<String> insertExpressionWithWrapper = new ArrayList<>();
        insertExpressionWithWrapper.add("{");
        insertExpressionWithWrapper.add(expression.get(positionIndex - 1));
        insertExpressionWithWrapper.add(operationIndex);
        insertExpressionWithWrapper.add(expression.get(positionIndex + 1));
        insertExpressionWithWrapper.add("}");
        for (int k = positionIndex - 1; k < positionIndex + 2; k++) {
            expression.remove(positionIndex - 1);
        }
        expression.addAll(positionIndex - 1, insertExpressionWithWrapper);

        return stepIndex;
    }
}
