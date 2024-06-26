package com.example.calculatorService.controller.html.Impl;

import com.example.calculatorService.controller.FuncTableController;
import com.example.calculatorService.controller.RangeTableController;
import com.example.calculatorService.controller.html.HtmlDownloadControllerInterface;
import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.funcTable.FuncTableCell;
import com.example.calculatorService.domain.table.rangeTable.Param;
import com.example.calculatorService.domain.table.rangeTable.Range;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
import com.example.calculatorService.domain.table.rangeTable.ResultWithParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("rangeTable")
public class HtmlRangeTableAdapterController implements HtmlDownloadControllerInterface<RangeTable> {
    @Autowired
    private RangeTableController controller;

    @GetMapping
    public String showMainPage() {
        return "redirect:rangeTable/table";
    }

    @GetMapping("/table")
    public String showFuncTable(Model model) {
        try {
            model.addAttribute("tables", controller.getHistory().getBody());
        } catch (NoSuchElementException e){}

        return "rangeTable/rangeTable";
    }

    @GetMapping("/calculate-table")
    public String calculateTable(@RequestParam(name = "name") String name,
                                @RequestParam(name = "expression") String expression,
                                @RequestParam(name = "ranges") String ranges) {
        String tempRanges = ranges.replaceAll(";", "&")
                .replaceAll(" ", "")
                .replaceAll("\n", "")
                .replaceAll("\t", "");
        if(tempRanges.indexOf(",") > -1){
            controller.calculateTable(name, expression, tempRanges);
        } else {
            controller.distributionCalculateTable(name, expression, tempRanges);
        }

        return "redirect:table";
    }

    @GetMapping("/delete-table")
    public String deleteCell(@RequestParam("idTable") String idTable) {
        try {
            long idTableNum = Long.parseLong(idTable);
            controller.deleteById(idTableNum);
        } catch (NumberFormatException e){}

        return "redirect:table";
    }

    @GetMapping("/update-table")
    public String updateCell(@RequestParam("idTable") String idTable, Model model){
        try {
            long idNum = Long.parseLong(idTable);
            RangeTable table = controller.tableById(idNum).getBody();
            if(table != null){
                model.addAttribute("table", table);
            } else {
                return "redirect:table";
            }
        } catch (NumberFormatException | NoSuchElementException e){
            return "redirect:table";
        }

        return "rangeTable/table-update";
    }

    @PostMapping("/update-table")
    public String updateUser(RangeTable table) {
        String tempRanges = table.getRangesFormula().replaceAll(";", "&")
                .replaceAll(" ", "")
                .replaceAll("\n", "")
                .replaceAll("\t", "");

        //Обновление содержимого в таблице (такой способ выбран для надежности)
        RangeTable tableTemp = controller.tableById(table.getId()).getBody();
        tableTemp.removeRanges();
        tableTemp.removeResults();
        tableTemp.setName(table.getName());
        tableTemp.setExpression(table.getExpression());
        tableTemp.setRangesFormula(tempRanges);
        controller.addFunc(tableTemp);

        controller.updateTable(table);

        return "redirect:table";
    }

    @GetMapping("/download")
    public void downloadRangeTable(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        downloadFile(fileName, "RT", response);
    }

    @Override
    public List<RangeTable> getEntities() {
        try {
            return controller.getHistory().getBody();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    @Override
    public RangeTable getEntityForTable(RangeTable loadEntity) {
        RangeTable temp = new RangeTable();
        temp.setName(loadEntity.getName());
        temp.setCreateDate(loadEntity.getCreateDate());
        temp.setExpression(loadEntity.getExpression());
        temp.setRangesFormula(loadEntity.getRangesFormula());

        List<Range> ranges = new ArrayList<>();
        for (Range r : loadEntity.getRanges()) {
            Range tempRange = new Range();
            tempRange.setName(r.getName());
            tempRange.setStart(r.getStart());
            tempRange.setEnd(r.getEnd());
            tempRange.setStep(r.getStep());

            ranges.add(tempRange);
        }
        temp.setRanges(ranges);

        List<ResultWithParams> results = new ArrayList<>();
        for (ResultWithParams r : loadEntity.getResults()) {
            ResultWithParams tempRes = new ResultWithParams();
            tempRes.setResult(r.getResult());
            tempRes.setResultString(r.getResultString());
            List<Param> params = new ArrayList<>();
            for (Param p : r.getParams()) {
                Param tempParam = new Param();
                tempParam.setName(p.getName());
                tempParam.setValue(p.getValue());

                params.add(tempParam);
            }
            tempRes.setParams(params);

            results.add(tempRes);
        }
        temp.setResults(results);

        return temp;
    }

    @PostMapping("/upload")
    public String uploadFileWithEntitiesADD(@RequestParam("file") MultipartFile file, RedirectAttributes attributes){
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:table";
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<RangeTable> list = List.of(mapper.readValue(file.getBytes(), RangeTable[].class));
            for (RangeTable t : list) {
                try {
                    controller.getRepo().save(getEntityForTable(t));
                } catch (DataIntegrityViolationException e){}
            }
        } catch (IOException e) {}

        attributes.addFlashAttribute("message", "Successfully uploaded " + file.getName() + '!');

        return "redirect:table";
    }

    @GetMapping("/clear")
    public String clearTable(){
        controller.getRepo().deleteAll();

        return "redirect:table";
    }
}

