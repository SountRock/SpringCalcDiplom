package com.example.functionRepositoryServer.service.MathModels;

import com.example.functionRepositoryServer.domain.CustomFunctionVar;

import java.util.ArrayList;
import java.util.List;

public interface CustomOperation {

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
