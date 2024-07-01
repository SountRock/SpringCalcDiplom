package com.example.functionRepositoryServer.service.MathModels;


import com.example.functionRepositoryServer.domain.CustomFunction;
import com.example.functionRepositoryServer.domain.CustomFunctionVar;
import com.example.functionRepositoryServer.domain.TypeVar;
import com.example.functionRepositoryServer.service.MathModels.Search.TwoSidesSearchModel;
import com.example.functionRepositoryServer.service.Tools.AnaliseExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель для вычисления пользовательских функции с типом TwoSidesSearch
 */
public class ModelCustomTwoSides extends TwoSidesSearchModel implements CustomOperation {
    private CustomFunction cFunc;

    public ModelCustomTwoSides(CustomFunction cFunc) {
        this.cFunc = cFunc;
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression analizer) {
        try {
            List<String> leftArguments = searchLeftArguments(expression, positionIndex);
            List<String> rightArguments = searchRightArguments(expression, positionIndex);
            int startPosition = leftArguments.size() > 1 ?
                    positionIndex - leftArguments.size() - 2 : positionIndex - 1;
            int endPosition = rightArguments.size() > 1 ?
                    positionIndex + rightArguments.size() + 3 : positionIndex + 2;

            List<CustomFunctionVar> inputVars = cFunc.getSteps().stream().filter(v -> v.getType() == TypeVar.INPUT).toList();
            List<CustomFunctionVar> innerVars = cFunc.getSteps().stream().filter(v -> v.getType() == TypeVar.INNER).toList();

            //Заполняем входные переменные аргументами
            leftArguments = analizer.analise(leftArguments);
            rightArguments = analizer.analise(rightArguments);
            inputVars.get(0).setValue(List.of(leftArguments.get(0)));
            inputVars.get(1).setValue(List.of(rightArguments.get(0)));

            //Заполняем ссылки на внешние переменные в внутрених (ссылки на входные переменныем могут быть на любом шаге).
            for (int i = 0; i < innerVars.size(); i++) {
                List<String> temp1 = new ArrayList<>(innerVars.get(i).getDefaultValue());
                List<String> temp2 = new ArrayList<>(innerVars.get(i).getExpression());

                temp1 = findStepsReferences(inputVars, temp1);
                temp1 = analizer.analise(temp1);
                temp2 = findStepsReferences(inputVars, temp2);

                innerVars.get(i).setValue(temp1);
                innerVars.get(i).setWorkExpression(temp2);
            }

            long count = 1;
            boolean isFind = false;
            String repeatCount = cFunc.getRepeatCount();
            try {
                for (int i = 0; !isFind && i < inputVars.size(); i++) {
                    if(inputVars.get(i).getName().equals(repeatCount)){
                        count = (long) (Double.parseDouble(inputVars.get(i).getValue().get(0)));
                        isFind = true;
                    }
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e){}

            //Расчитываем функцию по шагам нужное количесво циклов
            int i = 1;
            while (i < count) {
                for (int j = 0; j < innerVars.size(); j++) {
                    //Заполняем ссылки на предыдущие шаги переменные в внутрених.
                    //List<String> result = findPreviousStepsReferences(innerVars, innerVars.get(j));
                    List<String> result = findStepsReferences(innerVars, innerVars.get(j).getWorkExpression());

                    result = analizer.analise(result);
                    innerVars.get(j).setValue(result);
                }
                i++;
            }

            List<String> result = innerVars.get(innerVars.size() - 1).getValue();
            //Вставляем значение из последней внутреней переменной
            expression.addAll(startPosition, result);

            //Удаляем больше ненужное "обращение"
            for (int k = 0; k < endPosition - startPosition; k++) {
                expression.remove(startPosition + 1);
            }

            return startPosition;
        } catch (IndexOutOfBoundsException e){
            return positionIndex + 1;
        }
    }
}

