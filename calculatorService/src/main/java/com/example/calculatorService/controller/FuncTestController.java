package com.example.calculatorService.controller;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.service.ImplService.FuncVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("test")
public class FuncTestController {
    @Autowired
    private FuncVarService service;

    @PostMapping("add")
    public ResponseEntity addFunc(@RequestBody FuncVar funcVar){
        service.addFunc(funcVar);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("form")
    public ResponseEntity<FuncVar> getForm(){
        FuncVar temp = new FuncVar("1+1");
        temp.setResult(new ArrayList<>(List.of("12", "13")));
        temp.setCreateDate(LocalDateTime.now());
        temp.setName("one");

        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

    /*
    @PutMapping("calculate/{expression}")
    public ResponseEntity<String> calculate(@PathVariable("expression") String expression){
        FuncVar temp = new FuncVar(expression);
        temp.setCreateDate(LocalDateTime.now());

        String result = service.calculateFunction(temp);
        if(!result.equals("Not found references")){
            service.addFunc(temp);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }
     */

    @PutMapping("calculateWithName/{name}/{expression}")
    public ResponseEntity<String> calculateWithName(@PathVariable("expression") String expression, @PathVariable("name") String name){
        FuncVar temp = new FuncVar(expression);
        temp.setCreateDate(LocalDateTime.now());
        temp.setName(name);

        String result = service.calculateFunction(temp);
        if(!result.equals("One of Reference Result is empty") &&
                !result.equals("Not Found") &&
                !result.equals("Table is Null")){
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("calculateById/{id}")
    public ResponseEntity<String> calculate(@PathVariable("id") Long id){
        FuncVar temp = service.findById(id);
        String result = service.calculateFunction(temp);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("history")
    public ResponseEntity<List<FuncVar>> getHistory(){
        return new ResponseEntity<>(service.getHistory(), HttpStatus.OK);
    }

    @GetMapping("findByName/{name}")
    public ResponseEntity<List<FuncVar>> findByName(@PathVariable("name") String name){
        return new ResponseEntity<>(service.getFuncByName(name), HttpStatus.OK);
    }

}
