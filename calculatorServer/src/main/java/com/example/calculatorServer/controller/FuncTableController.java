package com.example.calculatorServer.controller;

import com.example.calculatorServer.domain.table.funcTable.FuncTable;
import com.example.calculatorServer.domain.table.funcTable.FuncTableCell;
import com.example.calculatorServer.repository.FuncTableRepository;
import com.example.calculatorServer.service.ImplService.FuncTableService;;
import com.example.calculatorServer.service.SaveDocument;
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

    @PostMapping("addRecord/{recordName}")
    public ResponseEntity addFunc(@PathVariable("recordName") String recordName){
        return service.addNewRecord(recordName);
    }

    @GetMapping("tables")
    public ResponseEntity<List<FuncTable>> getAll() {
        return service.getAll();
    }

    @PutMapping("calculate/{recordName}/{cellName}/{value}")
    public synchronized ResponseEntity calculateCellInRecord(@PathVariable("recordName") String recordName, @PathVariable("cellName") String cellName, @PathVariable("value") String value){
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
    public ResponseEntity deleteCell(@PathVariable("recordName") String recordName, @PathVariable("cellCount") long cellCount){
        return service.deleteCellInRecord(recordName, cellCount);
    }

    @DeleteMapping("deleteCellsByNameWithRecordName/{recordName}/{cellName}")
    public ResponseEntity deleteCell(@PathVariable("recordName") String recordName, @PathVariable("cellName") String cellName){
        return service.deleteCellInRecordByName(recordName, cellName);
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
            boolean isSave = saveDocument(directory, fileName, "FT", service.getAll().getBody());
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
        List<FuncTable> loadList = service.loadDocument(directory, fileName);

        if(loadList != null){
            service.loadTables(loadList);
            return new ResponseEntity<>(loadList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("showFiles/{directory}")
    public ResponseEntity<List<String>> showFilesInDirectory(@PathVariable("directory") String directory){
        List<String> files = showFiles(directory);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    public FuncTableRepository getRepo(){
        return service.getFtRepo();
    }
}
