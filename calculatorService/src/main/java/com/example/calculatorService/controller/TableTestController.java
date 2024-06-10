package com.example.calculatorService.controller;

import com.example.calculatorService.domain.table.rangeTable.Range;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
import com.example.calculatorService.service.ImplService.RangeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("tTable")
public class TableTestController {
    @Autowired
    private RangeTableService service;

    @PostMapping("add")
    public ResponseEntity addFunc(@RequestBody RangeTable table){
        service.addTable(table);
        return new ResponseEntity<>(HttpStatus.OK);
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
        }
    }


}
