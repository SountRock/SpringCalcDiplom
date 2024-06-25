package com.example.functionRepositoryService.service.ImplService;

import com.example.functionRepositoryService.domain.CustomFunction;
import com.example.functionRepositoryService.domain.CustomFunctionVar;
import com.example.functionRepositoryService.domain.TypeSearch;
import com.example.functionRepositoryService.domain.TypeVar;
import com.example.functionRepositoryService.repository.CustomFunctionRepository;
import com.example.functionRepositoryService.service.ReferenceService;
import com.example.functionRepositoryService.service.Tools.AnaliseExpression;
import com.example.functionRepositoryService.service.Tools.PrepareExpression;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class CustomFuncService implements ReferenceService {
    @Autowired
    private CustomFunctionRepository customRepo;

    @Autowired
    private AnaliseExpression analiser;
    @Autowired
    private PrepareExpression preparator;

    @Value("${links.calculatorServer}")
    private String calculatorServerLink;
    @Autowired
    private RestTemplate template;
    @Autowired
    private HttpHeaders headers;
    private long lastAddedFuncId = 0;


    /**
     * Метод создания своей функции
     * Описание шагов: name_step1=step_expression1<default_value&name_step2=step_expression2<default_value:count_repeat
     * Описание шапки функции: func_name(input_var1,input_var2,input_var3):type
     * @param head
     * @param steps
     * @return
     */
    public ResponseEntity<String> createCustomFunc(String head, String steps, String description) {
        CustomFunction newFunc = new CustomFunction();
        List<CustomFunctionVar> vars = new ArrayList<>();

        String[] headAndType = head.split(":");
        try {
            switch (headAndType[1]){
                case "TWO_SIDES":
                    newFunc.setTypeSearch(TypeSearch.TWO_SIDES);
                    break;
                case "RIGHT_SIDE":
                    newFunc.setTypeSearch(TypeSearch.RIGHT_SIDE);
                    break;
                default:
                    newFunc.setTypeSearch(TypeSearch.RIGHT_SIDE);
                    break;
            }

            String[] headSplit = headAndType[0].split("\\(");
            newFunc.setName(headSplit[0]);

            //Добавление входных переменных
            try {
                String[] inputVars = headSplit[1].replaceAll("\\)", "").split(",");

                for (String input : inputVars) {
                    CustomFunctionVar temp = new CustomFunctionVar();
                    temp.setName(input.toLowerCase());
                    temp.setType(TypeVar.INPUT);
                    temp.setDefaultValue(List.of("0"));
                    temp.setValue(List.of("0"));

                    vars.add(temp);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return new ResponseEntity<>("Set input vars error", HttpStatus.CONFLICT);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return new ResponseEntity<>("Set name error", HttpStatus.CONFLICT);
        }
        //Добавляем информацию о количесве входных переменых
        newFunc.setCountInputVars(vars.size());

        //Добавление внутрених переменных (шагов)
        String[] stepsNCountSplit = steps.replaceAll("\n", "&").split(":");
        //Exception ee = new Exception(Arrays.toString(stepsNCountSplit));
        //ee.printStackTrace();
        try {
            try {
                String count = stepsNCountSplit[1];
                newFunc.setRepeatCount(count);
                /*
                boolean isFind = false;
                for (int j = 0; !isFind && j < vars.size(); j++) {
                    if(count.equals(vars.get(j).getName())){
                        newFunc.setRepeatCount(count);
                        isFind = true;
                    }
                }
                 */
            } catch (ArrayIndexOutOfBoundsException e){
                newFunc.setRepeatCount("1");
            }

            String[] stepsSplit = stepsNCountSplit[0].split("&");
            for (String step : stepsSplit) {
                String[] stepParam = step.split("=");
                CustomFunctionVar temp = new CustomFunctionVar();
                temp.setName(stepParam[0].toLowerCase());
                String[] stepAndDefaultValue = stepParam[1].split("<");
                temp.setExpression(PrepareExpression.decompose(stepAndDefaultValue[0]));
                try {
                    temp.setDefaultValue(PrepareExpression.decompose(stepAndDefaultValue[1]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    temp.setDefaultValue(List.of("0"));
                }

                temp.setType(TypeVar.INNER);

                vars.add(temp);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return new ResponseEntity<>("Set steps error", HttpStatus.CONFLICT);
        }

        newFunc.setSteps(vars);

        if(description.length() < 255) {
            newFunc.setDescription(description);
        } else {
            newFunc.setDescription(description.substring(0, 254));
        }

        try {
            customRepo.save(newFunc);
            lastAddedFuncId = newFunc.getId();

            ResponseEntity response = pushMessageONCalculatorServer("ADD_NEW_C_FUNC");
            if(response.getStatusCode() == HttpStatus.ACCEPTED){
                return new ResponseEntity<>(newFunc.toString(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(newFunc + "[NOT PUSH ON CALCULATE SERVER]",
                        response.getStatusCode());
            }
        } catch (DataIntegrityViolationException e){
            return new ResponseEntity<>("This Custom Function name already exists", HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity pushMessageONCalculatorServer(String message){
        try{
            HttpEntity<String> entity = new HttpEntity<>(new String(message));
            ResponseEntity<String> response = template.exchange(calculatorServerLink + "/message",HttpMethod.POST, entity, String.class);

            return response;
        } catch (ResourceAccessException e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<String>> findNCalculateCustomFuncOnService(List<String> expression) {
        List<String> result = findNCalculateCustomFunc(expression, customRepo, analiser);
        if(result != null){
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<List<CustomFunction>> findAll(){
        return new ResponseEntity<>(customRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<CustomFunction> getLast(){
        return new ResponseEntity<>(customRepo.findById(lastAddedFuncId).get(), HttpStatus.OK);
    }

    /**
     * Удалить функцию по id
     * @param id
     * @return
     */
    public ResponseEntity deleteById(long id){
        try {
            customRepo.deleteCustomFunction(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить функцию по имени
     * @param name
     * @return
     */
    public ResponseEntity deleteByName(String name){
        try {
            CustomFunction func = customRepo.findByName(name).get(0);
            customRepo.deleteCustomFunction(func.getId());

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Сохранить функции
     * @param directory
     * @param entities
     * @return
     */
    public boolean saveDocument(String directory, String fileName, List<CustomFunction> entities) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(directory);
        file.mkdirs();
        try {
            file = new File(directory,
                    fileName + ".CustomFunction.json");

            mapper.writeValue(file, entities);

            return true;
        } catch (IOException | IndexOutOfBoundsException e){
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Загрузить функции из файла
     * @param directory
     * @param file
     * @return
     */
    public List<CustomFunction> loadDocument(String directory, String file) {
        File loadFile = new File(directory, file);
        ObjectMapper mapper = new ObjectMapper();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loadFile));
            String json = reader.readLine();

            return List.of(mapper.readValue(json, CustomFunction[].class));
        } catch (IOException e){
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Получить имена файлов в директории
     * @param directory
     * @return
     */
    public List<String> showFiles(String directory){
        File dir = new File(directory);
        List<String> list = new ArrayList<>();
        for (File file : dir.listFiles() ){
            list.add(file.getName());
        }

        return list;
    }

    public void loadFuncs(List<CustomFunction> funcs){
        customRepo.saveAll(funcs);
    }

    public CustomFunctionRepository getCustomRepo(){
        return customRepo;
    }
}
