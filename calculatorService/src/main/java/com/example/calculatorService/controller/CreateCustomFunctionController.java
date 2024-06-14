package com.example.calculatorService.controller;

import com.example.calculatorService.domain.customFunc.CustomFunction;
import com.example.calculatorService.service.ImplService.CustomFuncService;
import com.example.calculatorService.service.Tools.PrepareExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("custom")
public class CreateCustomFunctionController {
    @Autowired
    private CustomFuncService service;

    //описание шагов: name_step1=step_expression1&name_step2=step_expression2:count_repeat
    //описание шапки функции: func_name(input_var1,input_var2,input_var3):type
    @PostMapping("/create/{head}/{steps}")
    public ResponseEntity addCustomFunc(@PathVariable("head") String head, @PathVariable("steps") String steps){
        return service.createCustomFunc(head, steps);
    }

    @PutMapping("/testCF/{expression}")
    public ResponseEntity testCF(@PathVariable("expression") String expression){
        List<String> prepareExpression = PrepareExpression.decompose(expression);
        return service.findNCalculateCustomFuncOnService(prepareExpression);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CustomFunction>> getAll(){
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }
}
