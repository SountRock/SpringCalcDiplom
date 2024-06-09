package com.example.calculatorService.service.MathModels;

import com.example.calculatorService.service.MathModels.Search.TwoSidesSearchModel;
import com.example.calculatorService.service.Tools.AnaliseExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель деления
 */
public class ModelMultiplyv2 extends TwoSidesSearchModel {

    public ModelMultiplyv2() {
        super("*");
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression anaizer) {
        List<String> arguments = searchArguments(expression, positionIndex);

        if(arguments.size() < 3) {
            try {
                String temp = Double.toString(Double.parseDouble(arguments.get(0)) * Double.parseDouble(arguments.get(1)));

                for (int k = positionIndex - 1; k < positionIndex + 2; k++) {
                    expression.remove(positionIndex - 1);
                }
                expression.add(positionIndex - 1, temp);

                return positionIndex - 1;
            } catch (NumberFormatException e){
                //Обораичвание части с незвестной переменной
                List<String> insertExpressionWithWrapper = new ArrayList<>();
                insertExpressionWithWrapper.add("[");
                insertExpressionWithWrapper.add(expression.get(positionIndex - 1));
                insertExpressionWithWrapper.add("*");
                insertExpressionWithWrapper.add(expression.get(positionIndex + 1));
                insertExpressionWithWrapper.add("]");
                for (int k = positionIndex - 1; k < positionIndex + 2; k++) {
                    expression.remove(positionIndex - 1);
                }
                expression.addAll(positionIndex - 1, insertExpressionWithWrapper);

                return positionIndex + arguments.size() + 3;
            }
        } else {
            return positionIndex + arguments.size() + 1;
        }
    }
}
