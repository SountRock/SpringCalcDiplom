package com.example.calculatorServer.controller;

import com.example.calculatorServer.service.CustomFuncRepositoryConnectServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("loader")
public class CustomFuncController {
    @Autowired
    private CustomFuncRepositoryConnectServer connect;

    @GetMapping("/loadLib")
    public ResponseEntity loadLib(){
        return connect.loadCustomFunc();
    }
}
