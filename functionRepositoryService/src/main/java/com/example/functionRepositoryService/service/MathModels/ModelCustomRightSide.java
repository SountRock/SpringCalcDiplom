package com.example.functionRepositoryService.service.MathModels;

import com.example.functionRepositoryService.domain.CustomFunction;
import com.example.functionRepositoryService.domain.CustomFunctionVar;
import com.example.functionRepositoryService.domain.TypeVar;
import com.example.functionRepositoryService.service.MathModels.Search.RightSideSearchModel;
import com.example.functionRepositoryService.service.ReferenceService;
import com.example.functionRepositoryService.service.Tools.AnaliseExpression;
import com.example.functionRepositoryService.service.Tools.PrepareExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель для вычисления пользовательских функции с типом RightSideSearch
 */
public class ModelCustomRightSide extends RightSideSearchModel implements ReferenceService, CustomOperation {
    private CustomFunction cFunc;

    public ModelCustomRightSide(CustomFunction cFunc) {
        this.cFunc = cFunc;
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression analizer) {
        List<String> arguments = searchArguments(expression, positionIndex, analizer);
        arguments = arguments.stream().filter(a -> !a.equals(",")).toList();

        List<CustomFunctionVar> inputVars = cFunc.getSteps().stream().filter(v -> v.getType() == TypeVar.INPUT).toList();
        List<CustomFunctionVar> innerVars = cFunc.getSteps().stream().filter(v -> v.getType() == TypeVar.INNER).toList();

        //Заполняем входные переменные аргументами
        for (int i = 0; i < arguments.size(); i++) {
            List<String> temp = PrepareExpression.decompose(arguments.get(i));

            temp = analizer.analise(temp);
            inputVars.get(i).setValue(temp);
        }

        //Заполняем ссылки на внешние переменные в внутрених (ссылки на входные переменныем могут быть на любом шаге).
        for (int i = 0; i < innerVars.size(); i++) {
            List<String> temp1 = new ArrayList<>(innerVars.get(i).getDefaultValue());
            List<String> temp2 = new ArrayList<>(innerVars.get(i).getExpression());

            temp1 = findStepsReferences(inputVars, temp1);
            temp1 = analizer.analise(temp1);
            temp2 = findStepsReferences(inputVars, temp2);

            innerVars.get(i).setValue(temp1);
            innerVars.get(i).setExpression(temp2);
        }

        long count = 1;
        boolean isFind = false;
        String repeatCount = cFunc.getRepeatCount();
        try {
            for (int i = 0; !isFind && i < inputVars.size(); i++) {
                if(inputVars.get(i).getName().equals(repeatCount)){
                    count = Long.parseLong(inputVars.get(i).getValue().get(0));
                    isFind = true;
                }
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e){}

        //Расчитываем функцию по шагам нужное количесво циклов
        int i = 1;
        do {
            for (int j = 0; j < innerVars.size(); j++) {
                //Заполняем ссылки на предыдущие шаги переменные в внутрених.
                //List<String> result = findPreviousStepsReferences(innerVars, innerVars.get(j));
                List<String> result = findStepsReferences(innerVars, innerVars.get(j).getExpression());

                result = analizer.analise(result);
                innerVars.get(j).setValue(result);
            }
            i++;
        } while (i < count);


        expression.remove(positionIndex);

        //Вставляем значение из последней внутреней переменной
        List<String> result = innerVars.get(innerVars.size() - 1).getValue();
        expression.addAll(positionIndex, result);

        return positionIndex + result.size();
    }
}
