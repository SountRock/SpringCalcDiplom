package com.example.calculatorServer.controller.html.Impl;

import com.example.calculatorServer.controller.FuncTableController;
import com.example.calculatorServer.controller.html.HtmlDownloadNUploadControllerInterface;
import com.example.calculatorServer.domain.table.funcTable.FuncTable;
import com.example.calculatorServer.domain.table.funcTable.FuncTableCell;
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
@RequestMapping("funcTable")
public class HtmlFuncTableAdapterController implements HtmlDownloadNUploadControllerInterface<FuncTable> {
    @Autowired
    private FuncTableController controller;

    @GetMapping
    public String showMainPage() {
        return "redirect:funcTable/table";
    }

    @GetMapping("/table")
    public String showFuncTable(Model model) {
        try {
            model.addAttribute("records", controller.getAll().getBody());
        } catch (NoSuchElementException e){}

        return "funcTable/funcTable";
    }

    @GetMapping("/calculate-cell")
    public String calculateCell(@RequestParam(name = "recordName") String recordName,
                                @RequestParam(name = "cellName") String cellName,
                                @RequestParam(name = "value") String value) {
        controller.calculateCellInRecord(recordName, cellName, value);

        return "redirect:table";
    }

    @GetMapping("/delete-cell")
    public String deleteCell(@RequestParam("idRecord") String idRecord,
                             @RequestParam("idCell") String idCell) {
        try {
            long idRecordNum = Long.parseLong(idRecord);
            long idCellNum = Long.parseLong(idCell);
            controller.deleteCellById(idRecordNum, idCellNum);
        } catch (NumberFormatException e){}

        return "redirect:table";
    }

    @GetMapping("/delete-record")
    public String deleteRecord(@RequestParam(name = "recordId") String recordId) {
        controller.deleteById(Long.parseLong(recordId));

        return "redirect:table";
    }

    @GetMapping("/delete-cells-by-name")
    public String deleteCellsByName(@RequestParam(name = "recordName") String recordName,
                                    @RequestParam(name = "cellsName") String cellsName) {
        controller.deleteCell(recordName, cellsName);

        return "redirect:table";
    }

    @GetMapping("/update-cell/{id}/{recordName}")
    public String updateCell(@PathVariable("id") String id, @PathVariable("recordName") String recordName, Model model){
        try {
            long idNum = Long.parseLong(id);
            FuncTableCell cell = controller.findRCellById(idNum).getBody();

            if(cell != null){
                model.addAttribute("cell", cell);
            } else {
                return "redirect:table";
            }
        } catch (NumberFormatException | NoSuchElementException e){
            return "redirect:table";
        }
        model.addAttribute("recordName", recordName);

        return "funcTable/cell-update";
    }

    @PostMapping("/update-cell")
    public String updateUser(FuncTableCell cell,
                             @RequestParam(name = "recordName") String recordName) {
        try {
            FuncTable findRecord = controller.findRecordByName(recordName).getBody();
            if(findRecord != null){
                cell.setFuncTable(findRecord);
                findRecord.removeCell(cell);
                controller.updateCell(cell);
            } else {
                controller.calculateCellInRecord(recordName, cell.getCellName(), cell.getExpression());
            }

        } catch (NoSuchElementException e){}

        return "redirect:table";
    }

    @GetMapping("/download")
    public void downloadFuncTable(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        downloadFile(fileName, "FT", response);
    }

    @Override
    public List<FuncTable> getEntities() {
        try {
            return controller.getAll().getBody();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    @Override
    public FuncTable getEntityForTable(FuncTable loadEntity) {
        FuncTable temp = new FuncTable();
        temp.setRecordName(loadEntity.getRecordName());
        List<FuncTableCell> cells = new ArrayList<>();
        for (FuncTableCell c : loadEntity.getCells()) {
            FuncTableCell tempCell = new FuncTableCell();
            tempCell.setCellCount(c.getCellCount());
            tempCell.setCellName(c.getCellName());
            tempCell.setExpression(c.getExpression());
            tempCell.setResult(c.getResult());
            tempCell.setResultString(c.getResultString());

            cells.add(tempCell);
        }
        temp.setCells(cells);

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
            List<FuncTable> list = List.of(mapper.readValue(file.getBytes(), FuncTable[].class));
            for (FuncTable t : list) {
                try {
                    List<FuncTable> temp = controller.getRepo().findByRecordName(t.getRecordName());
                    FuncTable prepareTable = getEntityForTable(t);
                    if(temp.isEmpty()){
                        controller.getRepo().save(prepareTable);
                    } else {
                        FuncTable findTable = temp.get(0);

                        List<FuncTableCell> cells = findTable.getCells();
                        cells.addAll(prepareTable.getCells());
                        findTable.setCells(cells);

                        controller.getRepo().save(findTable);
                    }
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
