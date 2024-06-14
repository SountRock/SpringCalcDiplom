package com.example.calculatorService.service.MathModels;

import com.example.calculatorService.domain.customFunc.CustomFunctionVar;

import java.util.ArrayList;
import java.util.List;

public interface CustomOperation {
    /*
    default List<String> findPreviousStepsReferences(List<CustomFunctionVar> allSteps, CustomFunctionVar currentStep){
        List<String> temp = new ArrayList<>(currentStep.getExpression());

        for (int i = 0; i < temp.size(); i++) {
            for (int j = 0; j < allSteps.size(); j++) {
                if (temp.get(i).equals(allSteps.get(j).getName())){
                    List<String> refValue = allSteps.get(j).getValue();
                    temp.remove(i);
                    temp.addAll(i, refValue);
                }
            }
        }
        //currentStep.setValue(temp);

        return temp;
    }
     */

    default List<String> findStepsReferences(List<CustomFunctionVar> steps, List<String> expression){
        List<String> temp = new ArrayList<>(expression);

        for (int i = 0; i < temp.size(); i++) {
            for (int j = 0; j < steps.size(); j++) {
                if (temp.get(i).equals(steps.get(j).getName())){
                    List<String> refValue = steps.get(j).getValue();
                    temp.remove(i);
                    temp.addAll(i, refValue);
                }
            }
        }

        return temp;
    }
}
