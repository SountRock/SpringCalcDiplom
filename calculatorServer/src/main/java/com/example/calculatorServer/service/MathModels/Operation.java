package com.example.calculatorServer.service.MathModels;

import com.example.calculatorServer.service.Tools.AnaliseExpression;

import java.util.List;

/**
 * Интерфейс для моделей операций
 */
public interface Operation {

    /**
     * Выполнить операцию в указанной позиции
     * @param expression
     * @param positionIndex
     * @return positionToContinue
     */
    int operation(List<String> expression, int positionIndex, AnaliseExpression analizer);

    /**
     * Проверяет соотвестие операции
     * @param operation
     * @return is this operation
     */
    boolean isThisOperation(String operation);
}
