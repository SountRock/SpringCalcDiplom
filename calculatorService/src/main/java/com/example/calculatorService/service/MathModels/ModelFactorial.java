package com.example.calculatorService.service.MathModels;

import com.example.calculatorService.service.MathModels.MathMethod.GammaFunc;
import com.example.calculatorService.service.MathModels.Search.RightSideSearchModel;
import com.example.calculatorService.service.Tools.AnaliseExpression;

import java.util.List;

/**
 * Модель факториала
 */
public class ModelFactorial extends RightSideSearchModel{
    public ModelFactorial() {
        super("fact");
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression analizer) {
        List<String> arguments = searchArguments(expression, positionIndex, analizer);

        if(arguments.size() < 2) {
            try {
                String result = Double.toString(GammaFunc.gamma(Double.parseDouble(arguments.get(0)) + 1.0));

                for (int k = positionIndex; k < positionIndex + 1; k++) {
                    expression.remove(positionIndex);
                }

                expression.add(positionIndex, result);
                return positionIndex + 1;
            } catch (NumberFormatException e){
                return positionIndex + arguments.size() + 1;
            }
        } else {
            arguments.add(0, "(");
            arguments.add(")");
            expression.addAll(positionIndex + 1, arguments);
            return positionIndex + arguments.size() + 1;
        }
    }
}

