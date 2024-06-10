package com.example.calculatorService.service.MathModels;

import com.example.calculatorService.service.MathModels.Search.TwoSidesSearchModel;
import com.example.calculatorService.service.Tools.AnaliseExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель сложения
 */
public class ModelPlusv2 extends TwoSidesSearchModel {

    public ModelPlusv2() {
        super("+");
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression anaizer) {
        List<String> arguments = searchArguments(expression, positionIndex);

        if(arguments.size() == 2) {
            try {
                String temp = Double.toString(Double.parseDouble(arguments.get(0)) + Double.parseDouble(arguments.get(1)));

                for (int k = positionIndex - 1; k < positionIndex + 2; k++) {
                    expression.remove(positionIndex - 1);
                }
                expression.add(positionIndex - 1, temp);

                return positionIndex - 1;
            } catch (NumberFormatException e){
                return encapsulateUncertainty(positionIndex, expression);
            }
        } else {
            return positionIndex + arguments.size() + 1;
        }
    }
}
