package com.example.calculatorService.service.MathModels.Search;

import com.example.calculatorService.service.MathModels.Operation;
import com.example.calculatorService.service.Tools.AnaliseExpression;

import java.util.List;
/**
 * Политика поиска аргументов с правой стороны от индификатора
 */
public class RightSideSearchModel implements Operation {
    String operationIndex;

    /**
     * Введите индефикатор операции
     * @param operationIndex
     */
    public RightSideSearchModel(String operationIndex) {
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
    public List<String> searchArguments(List<String> expr, int positionIndex, AnaliseExpression anaizer) {
        try{
            if(!expr.get(positionIndex + 1).equals("(")){
                return List.of(expr.get(positionIndex + 1));
            }

            List<String> temp = anaizer.compare(expr, positionIndex + 1, "(", ")");
            return anaizer.analise(temp);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return expr;
        }
    }
}
