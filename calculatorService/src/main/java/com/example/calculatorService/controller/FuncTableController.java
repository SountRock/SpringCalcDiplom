package com.example.calculatorService.controller;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.funcTable.FuncTableCell;
import com.example.calculatorService.service.CustomFuncRepositoryConnectServer;
import com.example.calculatorService.service.ImplService.FuncTableService;
import com.example.calculatorService.service.ImplService.FuncVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("FTable")
public class FuncTableController {
    @Autowired
    private FuncTableService service;
    @Autowired
    private CustomFuncRepositoryConnectServer connect;

    @PostMapping("add/{recordName}")
    public ResponseEntity addFunc(@PathVariable("recordName") String recordName){
        return service.addNewRecord(recordName);
    }

    @GetMapping("tables")
    public ResponseEntity<List<FuncTable>> getAll() {
        return service.getAll();
    }

    @PutMapping("calculate/{recordName}/{cellName}/{value}")
    public ResponseEntity calculateCellInRecord(@PathVariable("recordName") String recordName, @PathVariable("cellName") String cellName, @PathVariable("value") String value){
        FuncTableCell newCell = new FuncTableCell();
        newCell.setCellName(cellName);
        newCell.setExpression(value);

        Exception e = new Exception(newCell.toString());
        e.printStackTrace();

        return service.calculateCellInRecord(recordName, newCell);
    }

    @DeleteMapping("deleteRecord/{id}")
    public ResponseEntity deleteById(Long id){
         return service.deleteRecordById(id);
    }

    @DeleteMapping("deleteRecordByName/{name}")
    public ResponseEntity deleteByName(String name){
        return service.deleteRecordByName(name);
    }


}
