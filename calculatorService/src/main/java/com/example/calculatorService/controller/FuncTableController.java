package com.example.calculatorService.controller;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.funcTable.FuncTableCell;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
import com.example.calculatorService.repository.FuncTableRepository;
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

    @DeleteMapping("deleteCellByCount/{recordId}/{cellCount}")
    public ResponseEntity deleteCell(@PathVariable("recordId") long recordId, @PathVariable("cellCount") long cellCount){
        return service.deleteCellInRecord(recordId, cellCount);
    }

    @DeleteMapping("deleteCellsByName/{recordId}/{cellName}")
    public ResponseEntity deleteCells(@PathVariable("recordId") long recordId, @PathVariable("cellName") String cellName){
        return service.deleteCellInRecord(recordId, cellName);
    }

    @DeleteMapping("deleteCellByCountWithRecordName/{recordName}/{cellCount}")
    public ResponseEntity deleteCell(@PathVariable("recordName") String recordName, @PathVariable("cellId") long cellCount){
        return service.deleteCellInRecord(recordName, cellCount);
    }

    @DeleteMapping("deleteCellsByNameWithRecordName/{recordName}/{cellName}")
    public ResponseEntity deleteCell(@PathVariable("recordName") String recordName, @PathVariable("recordName") String cellName){
        return service.deleteCellInRecord(recordName, cellName);
    }

    @PostMapping("updateCell")
    public ResponseEntity updateCell(@RequestBody FuncTableCell cell){
        return service.calculateCell(cell);
    }

    @DeleteMapping("deleteCellById/{idRecord}/{idCell}")
    public ResponseEntity deleteCellById(@PathVariable("idRecord") long idRecord, @PathVariable("idCell") long idCell){
        return service.deleteCellInRecordById(idRecord, idCell);
    }

    @GetMapping("getRecordByName/{recordName}")
    public ResponseEntity<FuncTable> findRecordByName(@PathVariable("recordName") String recordName){
        return service.findRecordByName(recordName);
    }

    @GetMapping("getCellById/{id}")
    public ResponseEntity<FuncTableCell> findRCellById(@PathVariable("id") long id){
        return service.findCellById(id);
    }

    @PostMapping("saveTable/{directory}/{fileName}")
    public ResponseEntity saveTable(@PathVariable("directory") String directory, @PathVariable("fileName") String fileName){
        try {
            boolean isSave = saveDocument("calculatorService/" + directory, fileName, "FT", service.getAll().getBody());
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

    public FuncTableRepository getRepo(){
        return service.getFtRepo();
    }
}
