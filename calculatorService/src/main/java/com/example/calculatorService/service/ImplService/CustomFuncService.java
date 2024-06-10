package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.repository.CustomFunctionRepository;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class CustomFuncService {
    @Autowired
    private FuncVarRepository funcRepo;
    private RangeTableRepository tableRepo;
    private CustomFunctionRepository customRepo;

    @Autowired
    private AnaliseExpression analiser;
    @Autowired
    private PrepareExpression preparator;

    @Autowired
    public void setTableRepo(@Lazy RangeTableRepository tableRepo) {
        this.tableRepo = tableRepo;
    }

    @Autowired
    public void setTableRepo(@Lazy CustomFunctionRepository customRepo) {
        this.customRepo = customRepo;
    }

    //описание шагов: name_step1=step_expression1&name_step2=step_expression2:count_repeat
    //описание шапки функции: func_name(input_var1,input_var2,input_var3)

}
