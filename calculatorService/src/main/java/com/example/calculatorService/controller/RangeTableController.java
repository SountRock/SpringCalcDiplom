package com.example.calculatorService.controller;

import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.rangeTable.Range;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
import com.example.calculatorService.service.ImplService.RangeTableService;
import com.example.calculatorService.service.SaveDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("RTable")
public class RangeTableController implements SaveDocument<RangeTable> {
    @Autowired
    private RangeTableService service;

    @PostMapping("add")
    public ResponseEntity addFunc(@RequestBody RangeTable table){
        service.addTable(table);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("deleteById/{id}")
    public ResponseEntity deleteById(@PathVariable("id") Long id){
        return service.deleteById(id);
    }

    @DeleteMapping("deleteByName/{name}")
    public ResponseEntity deleteById(@PathVariable("name") String name){
        return service.deleteByName(name);
    }

    @GetMapping("tables")
    public ResponseEntity<List<RangeTable>> getHistory(){
        return new ResponseEntity<>(service.getAllTables(), HttpStatus.OK);
    }

    //ranges задаются как (пример): x=1..5,0.5&y=0.1..2&x=5..20,1;
    @PutMapping("calculateTable/{name}/{expression}/{ranges}")
    public ResponseEntity<String> calculateTable(@PathVariable("name") String name, @PathVariable("expression") String expression, @PathVariable("ranges")String ranges){
        try {
            RangeTable table = new RangeTable(expression);

            String[] temp = ranges.split("&");
            List<Range> rangesList = new ArrayList<>();
            for (String t : temp) {
                String[] nameNValue = t.split("=");
                Range range = new Range();
                range.setName(nameNValue[0]);
                String[] rangeNStep = nameNValue[1].split(",");
                range.setStep(Double.parseDouble(rangeNStep[1]));
                String[] rangeInterval = rangeNStep[0].split("\\.\\.");
                range.setStart(Double.parseDouble(rangeInterval[0]));
                range.setEnd(Double.parseDouble(rangeInterval[1]));

                rangesList.add(range);
            }

            table.setCreateDate(LocalDateTime.now());
            table.setName(name);

            ResponseEntity<String> result = service.calculateTable(table, rangesList);
            return result;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
            e.printStackTrace();
            return new ResponseEntity<>("Syntax ranges error", HttpStatus.CONFLICT);
        } catch (UnexpectedRollbackException e){
            return new ResponseEntity<>("Create Table Error", HttpStatus.CONFLICT);
        }
    }

    //ranges задаются как (пример): x=1..5&y=0.1..2&x=5..20:100;
    //где 100 - сто точек в каждом диапазоне
    @PutMapping("distributionCalculateTable/{name}/{expression}/{ranges}")
    public ResponseEntity<String> distributionCalculateTable(@PathVariable("name") String name, @PathVariable("expression") String expression, @PathVariable("ranges")String ranges){
        try {
            RangeTable table = new RangeTable(expression);
            String[] rangesNCountSteps = ranges.split(":");
            int countSteps = Integer.parseInt(rangesNCountSteps[1]);
            String[] temp = rangesNCountSteps[0].split("&");
            List<Range> rangesList = new ArrayList<>();
            for (String t : temp) {
                String[] nameNValue = t.split("=");
                Range range = new Range();
                range.setName(nameNValue[0]);
                String[] rangeInterval = nameNValue[1].split("\\.\\.");
                range.setStart(Double.parseDouble(rangeInterval[0]));
                range.setEnd(Double.parseDouble(rangeInterval[1]));

                double step = (range.getEnd() - range.getStart())/(countSteps);
                range.setStep(step);

                rangesList.add(range);
            }
            table.setCreateDate(LocalDateTime.now());
            table.setName(name);

            ResponseEntity<String> result = service.calculateTable(table, rangesList, countSteps);
            return result;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
            e.printStackTrace();
            return new ResponseEntity<>("Syntax ranges error", HttpStatus.CONFLICT);
        } catch (UnexpectedRollbackException e){
            return new ResponseEntity<>("Create Table Error", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("saveTable/{directory}/{fileName}")
    public ResponseEntity saveTable(@PathVariable("directory") String directory, @PathVariable("fileName") String fileName){
        try {
            boolean isSave = saveDocument("calculatorService/" + directory, fileName, service.getAllTables());
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
        List<RangeTable> loadList = service.loadDocument("calculatorService/" + directory, fileName);

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
