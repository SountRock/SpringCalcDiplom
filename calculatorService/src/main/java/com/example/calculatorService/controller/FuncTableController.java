package com.example.calculatorService.controller;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.funcTable.FuncTableCell;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
import com.example.calculatorService.service.CustomFuncRepositoryConnectServer;
import com.example.calculatorService.service.ImplService.FuncTableService;
import com.example.calculatorService.service.ImplService.FuncVarService;
import com.example.calculatorService.service.SaveDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("FTable")
public class FuncTableController implements SaveDocument<FuncTable> {
    @Autowired
    private FuncTableService service;

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
        try {
            return service.calculateCellInRecord(recordName, newCell);
        } catch (UnexpectedRollbackException e){
            return new ResponseEntity<>("Create Table Error", HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("deleteRecord/{id}")
    public ResponseEntity deleteById(@PathVariable("id") Long id){
         return service.deleteRecordById(id);
    }

    @DeleteMapping("deleteRecordByName/{name}")
    public ResponseEntity deleteByName(@PathVariable("name") String name){
        return service.deleteRecordByName(name);
    }

    @DeleteMapping("deleteCellById/{recordId}/{cellId}")
    public ResponseEntity deleteCell(@PathVariable("recordId") long recordId, @PathVariable("cellId") long cellId){
        return service.deleteCellInRecord(recordId, cellId);
    }

    @DeleteMapping("deleteCellsByName/{recordId}/{cellName}")
    public ResponseEntity deleteCells(@PathVariable("recordId") long recordId, @PathVariable("cellName") String cellName){
        return service.deleteCellInRecord(recordId, cellName);
    }

    @DeleteMapping("deleteCellByIdWithRecordName/{recordName}/{cellId}")
    public ResponseEntity deleteCell(@PathVariable("recordName") String recordName, @PathVariable("cellId") long cellId){
        return service.deleteCellInRecord(recordName, cellId);
    }

    @DeleteMapping("deleteCellsByNameWithRecordName/{recordName}/{cellName}")
    public ResponseEntity deleteCell(@PathVariable("recordName") String recordName, @PathVariable("recordName") String cellName){
        return service.deleteCellInRecord(recordName, cellName);
    }

    @PostMapping("saveTable/{directory}/{fileName}")
    public ResponseEntity saveTable(@PathVariable("directory") String directory, @PathVariable("fileName") String fileName){
        try {
            boolean isSave = saveDocument("calculatorService/" + directory, fileName, service.getAll().getBody());
            if(isSave){
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("loadTable/{directory}/{fileName}")
    public ResponseEntity loadTable(@PathVariable("directory") String directory, @PathVariable("fileName") String fileName){
        List<FuncTable> loadList = service.loadDocument("calculatorService/" + directory, fileName);

        if(loadList != null){
            service.loadTables(loadList);
            return new ResponseEntity<>(loadList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("showFiles/{directory}")
    public ResponseEntity<List<String>> showFilesInDirectory(@PathVariable("directory") String directory){
        List<String> files = showFiles("calculatorService/" + directory);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
