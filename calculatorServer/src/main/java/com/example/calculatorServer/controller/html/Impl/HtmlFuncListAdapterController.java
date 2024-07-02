package com.example.calculatorServer.controller.html.Impl;

import com.example.calculatorServer.controller.FuncListController;
import com.example.calculatorServer.controller.html.HtmlDownloadNUploadControllerInterface;
import com.example.calculatorServer.domain.funcvar.FuncVar;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("funcList")
public class HtmlFuncListAdapterController implements HtmlDownloadNUploadControllerInterface<FuncVar> {
    @Autowired
    private FuncListController controller;

    @GetMapping
    public String showMainPage() {
        return "redirect:funcList/list";
    }

    @GetMapping("/list")
    public String showFuncTable(Model model) {
        try {
            model.addAttribute("funcs", controller.getHistory().getBody());
        } catch (NoSuchElementException e){}

        return "list/listView";
    }

    @GetMapping("/calculate-func")
    public String calculateFunc(@RequestParam(name = "name") String name,
                                 @RequestParam(name = "expression") String expression) {
        controller.calculateWithName(expression, name);

        return "redirect:list";
    }

    @GetMapping("/delete-func")
    public String deleteFunc(@RequestParam("id") String id) {
        try {
            long idNum = Long.parseLong(id);
            controller.deleteById(idNum);
        } catch (NumberFormatException e){}

        return "redirect:list";
    }

    @GetMapping("/download")
    public void downloadFuncList(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        downloadFile(fileName, "FL", response);
    }

    @Override
    public List<FuncVar> getEntities() {
        try {
            return controller.getHistory().getBody();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    @Override
    public FuncVar getEntityForTable(FuncVar loadEntity) {
        FuncVar temp = new FuncVar();
        temp.setName(loadEntity.getName());
        temp.setExpression(loadEntity.getExpression());
        temp.setResult(loadEntity.getResult());
        temp.setResultString(loadEntity.getResultString());
        temp.setCreateDate(loadEntity.getCreateDate());

        return temp;
    }

    @PostMapping("/upload")
    public String uploadFileWithEntitiesADD(@RequestParam("file") MultipartFile file, RedirectAttributes attributes){
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:list";
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<FuncVar> list = List.of(mapper.readValue(file.getBytes(), FuncVar[].class));
            for (FuncVar f : list) {
                try {
                    controller.getRepo().save(getEntityForTable(f));
                } catch (DataIntegrityViolationException | IllegalStateException e){}
            }
        } catch (IOException e) {}

        attributes.addFlashAttribute("message", "Successfully uploaded " + file.getName() + '!');

        return "redirect:list";
    }

    @GetMapping("/clear")
    public String clearList(){
        controller.getRepo().deleteAll();

        return "redirect:list";
    }
}
