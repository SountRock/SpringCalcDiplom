package com.example.calculatorService.service;

import com.example.calculatorService.domain.customFunc.CustomFunction;
import com.example.calculatorService.domain.customFunc.CustomFunctionVar;
import com.example.calculatorService.repository.CustomFunctionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
                List<CustomFunction> funcs = List.of(response.getBody());
                for (int i = 0; i < funcs.size(); i++) {
                    funcs.set(i, getEntityForTable(
                            funcs.get(i)
                    ));
                }

                customRepo.saveAll(funcs);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceAccessException e){
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
                customRepo.save(getEntityForTable(response.getBody()));
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceAccessException e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private CustomFunction getEntityForTable(CustomFunction loadEntity) {
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
            tempCF.setDefaultValue(v.getDefaultValue());
            steps.add(tempCF);
        }
        temp.setSteps(steps);

        return temp;
    }
}
