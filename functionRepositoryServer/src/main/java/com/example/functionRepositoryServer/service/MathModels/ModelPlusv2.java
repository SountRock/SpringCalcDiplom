package com.example.functionRepositoryServer.service.MathModels;

import com.example.functionRepositoryServer.service.MathModels.Search.TwoSidesSearchModel;
import com.example.functionRepositoryServer.service.Tools.AnaliseExpression;

import java.util.List;

/**
 * Модель сложения
 */
public class ModelPlusv2 extends TwoSidesSearchModel {

    public ModelPlusv2() {
        super("+");
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression analizer) {
        List<String> leftArguments = searchLeftArguments(expression, positionIndex);
        List<String> rightArguments = searchRightArguments(expression, positionIndex);

        if(leftArguments.size() > 0 || rightArguments.size() > 0){
            List<String> leftResult = analizer.analise(leftArguments);
            List<String> rightResult = analizer.analise(rightArguments);
            try {
                String temp = Double.toString(Double.parseDouble(leftResult.get(0)) + Double.parseDouble(rightResult.get(0)));
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
        } else {
            return positionIndex + leftArguments.size() + rightArguments.size() + 1;
        }
    }
}
