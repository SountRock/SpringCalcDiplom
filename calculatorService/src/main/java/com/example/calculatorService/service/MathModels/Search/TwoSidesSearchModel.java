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
}
