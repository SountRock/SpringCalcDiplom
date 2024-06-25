package com.example.calculatorService.controller.html;

import com.example.calculatorService.controller.CustomFuncController;
import com.example.calculatorService.repository.FuncTableRepository;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("main")
public class HtmlMainController {
    @Autowired
    private CustomFuncController cfController;

    @GetMapping
    public String showMainPage() {
        return "redirect:main/greeting";
    }

    @GetMapping("/greeting")
    public String greeting(Model model) {
        return "index";
    }

    @GetMapping("/loadFromCFServer")
    public String loadFromCFServer(Model model){
        ResponseEntity response = cfController.loadLib();
        if(response.getStatusCode() == HttpStatus.OK){
            model.addAttribute("responseLoad", "Successfully Load Custom Func's");
        } else {
            model.addAttribute("responseLoad", "Failed Load Custom Func's");
        }
        Exception e = new Exception(response.getStatusCode().toString());
        e.printStackTrace();

        return "/index";
    }


}
