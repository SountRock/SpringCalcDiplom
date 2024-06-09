package com.example.calculatorService.configuration;

import com.example.calculatorService.service.MathModels.*;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Beans {
    private List<Operation> operations;

    public Beans() {
        operations = new ArrayList<>();
        operations.add(new ModelFactorial());
        operations.add(new ModelSquare());
        operations.add(new ModelDivitev2());
        operations.add(new ModelMultiplyv2());
        operations.add(new ModelPlusv2());
        operations.add(new ModelMinusv2());
    }

    @Bean
    public AnaliseExpression analiseExpression(){
        return new AnaliseExpression(operations);
    }

    @Bean
    public PrepareExpression prepareExpression(){
        return new PrepareExpression();
    }
}
