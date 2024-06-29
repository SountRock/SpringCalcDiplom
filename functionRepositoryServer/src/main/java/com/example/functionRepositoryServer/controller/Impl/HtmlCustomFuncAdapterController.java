package com.example.functionRepositoryServer.controller.Impl;

import com.example.functionRepositoryServer.controller.CreateCustomFunctionController;
import com.example.functionRepositoryServer.controller.HtmlDownloadControllerInterface;
import com.example.functionRepositoryServer.domain.CustomFunction;
import com.example.functionRepositoryServer.domain.CustomFunctionVar;
import com.example.functionRepositoryServer.service.Tools.PrepareExpression;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("customFuncRepo")
public class HtmlCustomFuncAdapterController implements HtmlDownloadControllerInterface<CustomFunction> {
    @Autowired
    private CreateCustomFunctionController controller;

    @GetMapping
    public String showMainPage() {
        return "redirect:customFuncRepo/list";
    }

    @GetMapping("/list")
    public String showFuncTable(Model model) {
        try {
            model.addAttribute("funcs", controller.getAll().getBody());
        } catch (NoSuchElementException e){}

        return "customFuncList";
    }

    @GetMapping("/calculate-func")
    public String calculateFunc(@RequestParam(name = "head") String head,
                                @RequestParam(name = "steps") String steps,
                                @RequestParam(name = "repeatCount") String repeatCount,
                                @RequestParam(name = "description") String description) {
        List<String> headTest = PrepareExpression.decompose(head.replaceAll(" ", ""));
        String decodeHead = "";
        try {
            boolean isArg1 = headTest.get(0).equals("(") && headTest.get(2).equals(")");
            boolean isArg2 = headTest.get(4).equals("(") && headTest.get(6).equals(")");
            if(isArg1 && isArg2 && headTest.size() == 7){
                decodeHead = headTest.get(3) + "(" + headTest.get(1) + "," + headTest.get(5) + "):TWO_SIDES";
            } else {
                decodeHead = head + ":RIGHT_SIDE";
            }
        } catch (IndexOutOfBoundsException  e){
            decodeHead = head + ":RIGHT_SIDE";
        }

        //name_step1=step_expression1<default_value&name_step2=step_expression2<default_value:count_repeat
        String decodeSteps = steps.replaceAll(";", "&")
                .replaceAll(" ", "")
                .replaceAll("\n", "")
                .replaceAll("\t", "")
                .replaceAll("\r", "")
                + ":" + repeatCount;

        controller.addCustomFunc(decodeHead, decodeSteps, description);

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

    @GetMapping("/testCF")
    public String testCF(@RequestParam(name = "testCF") String testCF,
                         Model model){
        model.addAttribute("testCF", testCF);
        String result = controller.testCF(testCF).getBody()
                .toString()
                .replaceAll("\\[", "")
                .replaceAll("\\]", "")
                .replaceAll(",", "");
        model.addAttribute("result", result);

        return "testResult";
    }

    @GetMapping("/download")
    public void downloadCFRepo(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        downloadFile(fileName, "CFR", response);
    }

    @Override
    public List<CustomFunction> getEntities() {
        try {
            return controller.getAll().getBody();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    @Override
    public CustomFunction getEntityForTable(CustomFunction loadEntity) {
        CustomFunction temp = new CustomFunction();
        temp.setName(loadEntity.getName());
        temp.setTypeSearch(loadEntity.getTypeSearch());
        temp.setRepeatCount(loadEntity.getRepeatCount());
        temp.setDescription(loadEntity.getDescription());
        temp.setCountInputVars(loadEntity.getCountInputVars());
        List<CustomFunctionVar> steps = new ArrayList<>();
        for (CustomFunctionVar v : loadEntity.getSteps()) {
            CustomFunctionVar tempCF = new CustomFunctionVar();
            tempCF.setName(v.getName());
            tempCF.setType(v.getType());
            tempCF.setExpression(v.getExpression());
            tempCF.setExpressionString(v.getExpressionString());
            tempCF.setDefaultValue(v.getDefaultValue());
            tempCF.setDefaultValueString(v.getDefaultValueString());
            steps.add(tempCF);
        }
        temp.setSteps(steps);

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
            List<CustomFunction> list = List.of(mapper.readValue(file.getBytes(), CustomFunction[].class));
            for (CustomFunction f : list) {
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
