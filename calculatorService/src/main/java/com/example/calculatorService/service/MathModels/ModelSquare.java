package com.example.calculatorService.service.MathModels;

import com.example.calculatorService.service.MathModels.Search.RightSideSearchModel;
import com.example.calculatorService.service.Tools.AnaliseExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель вычисления квабратного корня
 */
public class ModelSquare extends RightSideSearchModel {
    public ModelSquare() {
        super("sqrt");
    }

    @Override
    public int operation(List<String> expression, int positionIndex, AnaliseExpression analizer) {
        List<String> arguments = searchArguments(expression, positionIndex, analizer);

        if(arguments.size() == 1) {
            try {
                String result = Double.toString(Math.sqrt(Double.parseDouble(arguments.get(0))));

                for (int k = positionIndex; k < positionIndex + 2; k++) {
                    expression.remove(positionIndex);
                }

                expression.add(positionIndex, result);
                return positionIndex + 1;
            } catch (NumberFormatException e){
                List<String> temp = new ArrayList<>();
                temp.add("{");
                temp.addAll(arguments);
                expression.remove(positionIndex + 1);
                temp.add("}");
                expression.addAll(positionIndex + 1, temp);

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
