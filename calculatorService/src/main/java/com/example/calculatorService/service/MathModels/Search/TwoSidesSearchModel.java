package com.example.calculatorService.service.MathModels.Search;

import com.example.calculatorService.service.MathModels.Operation;
import com.example.calculatorService.service.Tools.AnaliseExpression;

import java.util.ArrayList;
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


    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression anaizer) {
        return 0;
    }

    @Override
    public boolean isThisOperation(String operation) {
        return operation.equals(operationIndex);
    }

    /**
     * Метод поиска аргументов
     * @param expr
     * @param positionIndex
     * @return argument list
     */
    public List<String> searchArguments(List<String> expr, int positionIndex) {
        int startPosition = positionIndex - 1;

        try{
            List<String> arguments = new ArrayList<>();
            String argument = expr.get(startPosition);
            arguments.add(argument);
            argument = expr.get(startPosition + 2);
            arguments.add(argument);

            return arguments;
        } catch (NumberFormatException | IndexOutOfBoundsException e){
            return expr;
        }
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
     * Являеться ли элемет функцией или оператором
     * @param testIndex
     * @param expression
     * @return
     */
    protected boolean isAnotherFunction(int testIndex, List<String> expression){
        try {
            Double.parseDouble(expression.get(testIndex));

            return false;
        } catch (IndexOutOfBoundsException e){
            return false;
        } catch (NumberFormatException e){
            return true;
        }
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
