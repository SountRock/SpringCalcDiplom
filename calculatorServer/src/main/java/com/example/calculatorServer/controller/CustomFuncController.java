package com.example.calculatorServer.controller;

import com.example.calculatorServer.service.CustomFuncRepositoryConnectServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("loader")
public class CustomFuncController {
    @Autowired
    private CustomFuncRepositoryConnectServer connect;

    @PostMapping("/message")
    public ResponseEntity postMessage(@RequestBody String message){
        if(message.equals("ADD_NEW_C_FUNC")){
            ResponseEntity response = connect.loadLastFunc();
            if(response.getStatusCode() == HttpStatus.OK){
                return new ResponseEntity<>("Successful added", HttpStatus.ACCEPTED);
            } else {
                return new ResponseEntity<>("Failed added", HttpStatus.CONFLICT);
            }
        }

        return new ResponseEntity<>("Successful receipt", HttpStatus.OK);
    }

    @PostMapping("/loadLib")
    public ResponseEntity loadLib(){
        return connect.loadCustomFunc();
    }
}
