package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.customFunc.CustomFunction;
import com.example.calculatorService.domain.customFunc.CustomFunctionVar;
import com.example.calculatorService.domain.customFunc.TypeSearch;
import com.example.calculatorService.domain.customFunc.TypeVar;
import com.example.calculatorService.repository.CustomFunctionRepository;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.MathModels.*;
import com.example.calculatorService.service.ReferenceService;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomFuncService implements ReferenceService {
    @Autowired
    private CustomFunctionRepository customRepo;

    private FuncVarRepository funcRepo;
    private RangeTableRepository tableRepo;

    @Autowired
    private AnaliseExpression analiser;
    @Autowired
    private PrepareExpression preparator;


    @Autowired
    public void setCustomRepo(@Lazy FuncVarRepository funcRepo) {
        this.funcRepo = funcRepo;
    }
    @Autowired
    public void setTableRepo(@Lazy RangeTableRepository tableRepo) {
        this.tableRepo = tableRepo;
    }

    //описание шагов: name_step1=step_expression1<default_value&name_step2=step_expression2<default_value:count_repeat
    //описание шапки функции: func_name(input_var1,input_var2,input_var3):type
    public ResponseEntity<String> createCustomFunc(String head, String steps) {
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
        String[] stepsNCountSplit = steps.replaceAll("%0A", "&").split(":");
        //String[] stepsNCountSplit = steps.replaceAll("\n", "&").split(":");
        try {
            try {
                String count = stepsNCountSplit[1];

                boolean isFind = false;
                for (int j = 0; !isFind && j < vars.size(); j++) {
                    if(count.equals(vars.get(j).getName())){
                        newFunc.setRepeatCount(count);
                        isFind = true;
                    }
                }
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

        try {
            customRepo.save(newFunc);
        } catch (DataIntegrityViolationException e){
            return new ResponseEntity<>("This Custom Function name already exists", HttpStatus.CONFLICT);
        }


        return new ResponseEntity<>(newFunc.toString(), HttpStatus.OK);
    }

    public ResponseEntity<List<String>> findNCalculateCustomFuncOnService(List<String> expression) {
        List<String> result = findNCalculateCustomFunc(expression, customRepo, funcRepo, tableRepo, analiser);
        if(result != null){
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    public List<CustomFunction> findAll(){
        return customRepo.findAll();
    }
}
