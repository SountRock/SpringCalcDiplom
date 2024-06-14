package com.example.calculatorService.service.MathModels;

import com.example.calculatorService.service.MathModels.Search.TwoSidesSearchModel;
import com.example.calculatorService.service.Tools.AnaliseExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель вычитания
 */
public class ModelMinusv2 extends TwoSidesSearchModel {

    public ModelMinusv2() {
        super("-");
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression analizer) {
        //List<String> arguments = searchArguments(expression, positionIndex, analizer);
        List<String> leftArguments = searchLeftArguments(expression, positionIndex);
        List<String> rightArguments = searchRightArguments(expression, positionIndex);

        //if(arguments.size() == 2) {
        /*
        if(leftArguments.size() == 1 && rightArguments.size() == 1) {
            try {
                //String temp = Double.toString(Double.parseDouble(arguments.get(0)) - Double.parseDouble(arguments.get(1)));
                String temp = Double.toString(Double.parseDouble(leftArguments.get(0)) - Double.parseDouble(rightArguments.get(0)));

                for (int k = positionIndex - 1; k < positionIndex + 2; k++) {
                    expression.remove(positionIndex - 1);
                }
                expression.add(positionIndex - 1, temp);

                return positionIndex - 1;
            } catch (NumberFormatException e){
                return encapsulateUncertainty(positionIndex, expression);
            }
        }
         */
        if(leftArguments.size() > 0 || rightArguments.size() > 0){
            List<String> leftResult = analizer.analise(leftArguments);
            List<String> rightResult = analizer.analise(rightArguments);
            try {
                String temp = Double.toString(Double.parseDouble(leftResult.get(0)) - Double.parseDouble(rightResult.get(0)));

                int startPosition = positionIndex - leftArguments.size();
                int endPosition = positionIndex + rightArguments.size() + 1;
                expression.add(startPosition, temp);
                for (int i = 0; i < endPosition - startPosition; i++) {
                    expression.remove(startPosition + 1);
                }

                return startPosition;
            } catch (IndexOutOfBoundsException e){
                return positionIndex + leftArguments.size() + rightArguments.size() + 1;
            } catch (NumberFormatException e){
                return encapsulateUncertainty(positionIndex, expression);
            }
        }
        else if(leftArguments.size() == 0){
            try {
                String temp = Double.toString(0 - Double.parseDouble(expression.get(positionIndex + 1)));

                for (int k = positionIndex; k < positionIndex + 2; k++) {
                    expression.remove(positionIndex);
                }
                expression.add(positionIndex, temp);

                return positionIndex - 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e){
                return positionIndex + rightArguments.size() + leftArguments.size() + 1;
                //return positionIndex + arguments.size() + 1;
            }
        } else {
            //return positionIndex + arguments.size() + 1;
            return positionIndex + rightArguments.size() + leftArguments.size() + 1;
        }
    }
}
