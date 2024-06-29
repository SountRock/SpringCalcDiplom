package com.example.calculatorServer.service.Tools;

import java.util.ArrayList;
import java.util.List;

import com.example.calculatorServer.service.MathModels.Operation;
import lombok.Data;

/**
 * Класс анализирующий выражение на выполнение указанных операций.
 */
public class AnaliseExpression {
    private List<Operation> operations;

    private static int limitOfTheNumberOfCalculations = 3000;
    private static int currentCountOfIteration = 0;

    /**
     * Введите лист моделей операций типа Operation
     * @param operations
     */
    public AnaliseExpression(List<Operation> operations){
        this.operations = operations;
    }

    /**
     * Анализировать выражение
     * @param exArr
     * @return analize result
     */
    public List<String> analise(List<String> exArr){
        for (int i = 0; i < operations.size(); i++){
            for (int j = 0; j < exArr.size(); j++){
                if(currentCountOfIteration > limitOfTheNumberOfCalculations){
                    currentCountOfIteration = 0;

                    return exArr;
                }
                if(exArr.get(j).equals("(")){
                    List<String> temp = compare(exArr, j, "(", ")");
                    List<String> result = analise(temp);

                    if(result.size() > 1){
                        result.add(0,"(");
                        result.add(")");
                    }
                    exArr.addAll(j, result);
                    j += result.size();
                    currentCountOfIteration++;
                } else if(operations.get(i).isThisOperation(exArr.get(j))){
                    j = operations.get(i).operation(exArr, j, this);
                    currentCountOfIteration++;
                }

            }
        }

        return exArr;
    }

    /**
     * Собрать выражение по указаному параметру входа(символ начала) и выхода(символ выхода)
     * @param exArr
     * @param indexStart
     * @param paramContinue
     * @param paramClose
     * @return compare expression
     */
    public List<String> compare(List<String> exArr, int indexStart, String paramContinue, String paramClose){
        List<String> temp = new ArrayList<>();

        exArr.remove(indexStart);
        byte Continue = 0;
        byte closeCount = 1;
        while(Continue < closeCount){
            try {
                if(exArr.get(indexStart).equals(paramContinue))
                    closeCount++;

                temp.add(exArr.remove(indexStart));

               Continue += exArr.get(indexStart).equals(paramClose) ? 1 : 0;
            } catch (IndexOutOfBoundsException e) {
                return temp;
            }
        }
        exArr.remove(indexStart);

        return temp;
    }
}
