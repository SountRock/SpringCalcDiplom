package com.example.functionRepositoryServer.controller;

import com.example.functionRepositoryServer.domain.CustomFunction;
import com.example.functionRepositoryServer.repository.CustomFunctionRepository;
import com.example.functionRepositoryServer.service.ImplService.CustomFuncService;
import com.example.functionRepositoryServer.service.Tools.PrepareExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("СRepo")
public class CreateCustomFunctionController {
    @Autowired
    private CustomFuncService service;

    /**
     * Метод создания своей функции
     * Описание шагов: name_step1=step_expression1<default_value&name_step2=step_expression2<default_value:count_repeat
     * Описание шапки функции: func_name(input_var1,input_var2,input_var3):type
     * @param head
     * @param steps
     * @return
     */
    @PostMapping("create/{head}/{steps}/{description}")
    public ResponseEntity addCustomFunc(@PathVariable("head") String head, @PathVariable("steps") String steps, @PathVariable("description") String description){
        try {
            return service.createCustomFunc(head, steps, description);
        } catch (UnexpectedRollbackException e){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Метод для тестирования своей функции
     * @param function
     * @return
     */
    @PutMapping("testCF/{function}")
    public ResponseEntity testCF(@PathVariable("function") String function){
        List<String> prepareExpression = PrepareExpression.decompose(function);
        return service.findNCalculateCustomFuncOnService(prepareExpression);
    }

    @GetMapping("list")
    public ResponseEntity<List<CustomFunction>> getAll(){
        return service.findAll();
    }

    @DeleteMapping("deleteById/{id}")
    public ResponseEntity deleteById(@PathVariable("id") Long id){
        return service.deleteById(id);
    }

    @DeleteMapping("deleteByName/{name}")
    public ResponseEntity deleteById(@PathVariable("name") String name){
        return service.deleteByName(name);
    }

    @PostMapping("saveLib/{directory}/{fileName}")
    public ResponseEntity saveList(@PathVariable("directory") String directory, @PathVariable("fileName") String fileName) {
        try {
            boolean isSave = service.saveDocument(directory, fileName, service.findAll().getBody());
            if (isSave) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("loadLib/{directory}/{fileName}")
    public ResponseEntity loadTable(@PathVariable("directory") String directory, @PathVariable("fileName") String fileName){
        List<CustomFunction> loadList = service.loadDocument(directory, fileName);

        if(loadList != null){
            service.loadFuncs(loadList);
            return new ResponseEntity<>(loadList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("showFiles/{directory}")
    public ResponseEntity<List<String>> showFilesInDirectory(@PathVariable("directory") String directory){
        List<String> files = service.showFiles("functionRepositoryService/" + directory);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    public ResponseEntity pushMessageONCalculatorServer(){
        return service.pushMessageONCalculatorServer();
    }

    public CustomFunctionRepository getRepo(){
        return service.getCustomRepo();
    }
}
