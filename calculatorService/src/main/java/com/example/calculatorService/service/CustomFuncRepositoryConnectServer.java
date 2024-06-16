package com.example.calculatorService.service;

import com.example.calculatorService.domain.customFunc.CustomFunction;
import com.example.calculatorService.repository.CustomFunctionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CustomFuncRepositoryConnectServer {
    @Autowired
    private CustomFunctionRepository customRepo;

    @Value("${links.funcRepository}")
    private String funcRepositoryLink;
    @Autowired
    private RestTemplate template;
    @Autowired
    private HttpHeaders headers;

    @PostConstruct
    public ResponseEntity loadCustomFunc() {
        try{
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CustomFunction[]> response = template.exchange(funcRepositoryLink + "/list", HttpMethod.GET, entity, CustomFunction[].class);

            if(response.getStatusCode() == HttpStatus.OK){
                customRepo.saveAll(List.of(response.getBody()));
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity loadLastFunc() {
        try{
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CustomFunction> response = template.exchange(funcRepositoryLink + "/last", HttpMethod.GET, entity, CustomFunction.class);

            if(response.getStatusCode() == HttpStatus.OK){
                customRepo.save(response.getBody());
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}