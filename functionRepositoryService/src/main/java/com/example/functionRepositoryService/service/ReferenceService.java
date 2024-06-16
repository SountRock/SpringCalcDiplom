package com.example.functionRepositoryService.service;


import com.example.functionRepositoryService.domain.CustomFunction;
import com.example.functionRepositoryService.repository.CustomFunctionRepository;
import com.example.functionRepositoryService.service.MathModels.ModelCustomRightSide;
import com.example.functionRepositoryService.service.MathModels.ModelCustomTwoSides;
import com.example.functionRepositoryService.service.Tools.AnaliseExpression;

import java.util.*;

public interface ReferenceService {

    default List<String> findNCalculateCustomFunc(List<String> expression, CustomFunctionRepository customRepo, AnaliseExpression analiser) {
        try {
            List<CustomFunction> funcs = customRepo.findAll();

            for (CustomFunction c : funcs) {
                for (int i = 0; i < expression.size(); i++) {
                    if (expression.get(i).equals(c.getName())){
                        switch (c.getTypeSearch()){
                            case TWO_SIDES:
                                ModelCustomTwoSides model1 = new ModelCustomTwoSides(c);
                                i = model1.operation(expression, i, analiser);
                                break;
                            case RIGHT_SIDE:
                                ModelCustomRightSide model2 = new ModelCustomRightSide(c);
                                i = model2.operation(expression, i, analiser);
                                break;
                        }
                    }
                }
            }

            return expression;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}