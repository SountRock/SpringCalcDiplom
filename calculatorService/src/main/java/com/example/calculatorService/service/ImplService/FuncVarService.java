package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.exceptions.ReferenceResultIsEmpty;
import com.example.calculatorService.exceptions.TableReferenceErrorException;
import com.example.calculatorService.repository.CustomFunctionRepository;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.ReferenceService;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class FuncVarService implements ReferenceService {
    @Autowired
    private FuncVarRepository funcRepo;
    private RangeTableRepository tableRepo;
    private CustomFunctionRepository customRepo;

    @Autowired
    private AnaliseExpression analiser;
    @Autowired
    private PrepareExpression preparator;

    @Autowired
    public void setTableRepo(@Lazy RangeTableRepository tableRepo) {
        this.tableRepo = tableRepo;
    }

    @Autowired
    public void setCustomRepo(@Lazy CustomFunctionRepository customRepo) {
        this.customRepo = customRepo;
    }

    /**
     * Добавить функцию
     * @param function
     */
    public void addFunc(FuncVar function){
        function.setCreateDate(LocalDateTime.now());
        funcRepo.save(function);
    }

    /**
     * Очистить историю
     */
    public void clearHistory(){
        funcRepo.deleteAll();
    }

    /**
     * Расчитать функцию
     */
    public ResponseEntity<String> calculateFunction(FuncVar function){
        try {
            if(function != null){
                List<String> prepareExpression = preparator.decompose(function.getExpression());
                //Проверям наличие ссылок с FuncVar
                prepareExpression = findFuncVarReferencesById(prepareExpression, funcRepo);
                prepareExpression = findFuncVarReferencesByName(prepareExpression, funcRepo);

                //Проверям наличие ссылок с RangeTable
                prepareExpression = findRangeTableReferencesById(prepareExpression, tableRepo);
                prepareExpression = findRangeTableReferencesByName(prepareExpression, tableRepo);
                prepareExpression = calculateRangeTableReferences(prepareExpression, tableRepo, customRepo, analiser);

                //Проверям наличие ссылок на Custom Function
                prepareExpression = findNCalculateCustomFunc(prepareExpression, customRepo, analiser);

                if(prepareExpression != null){
                    List<String> result = analiser.analise(prepareExpression);
                    function.setResult(result);
                    String resultString = result.toString()
                            .replaceAll("\\[", "")
                            .replaceAll("\\]", "")
                            .replaceAll(",", "");
                    function.setResultString(resultString);

                    funcRepo.save(function);

                    return new ResponseEntity<>(resultString, HttpStatus.OK);
                }
            }

            return new ResponseEntity<>("Function is Null", HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        } catch (ReferenceResultIsEmpty e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (TableReferenceErrorException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Получить все функции по имени
     */
    public List<FuncVar> getFuncByName(String name){
        return funcRepo.findByName(name);
    }

    /**
     * Получить историю вычислений
     */
    public  ResponseEntity<List<FuncVar>> getHistory() {
        return new ResponseEntity<>(funcRepo.findAll(), HttpStatus.OK);
    }

    /**
     * Найти функцию по id
     * @param id
     * @return
     */
    public FuncVar findById(Long id){
        return funcRepo.findById(id).get();
    }

    /**
     * Удалить функцию по id
     * @param id
     * @return
     */
    public ResponseEntity deleteById(long id){
        try {
            funcRepo.deleteFuncVar(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить функции по имени
     * @param name
     * @return
     */
    public ResponseEntity deleteByName(String name){
        try {
            List<FuncVar> funcs = funcRepo.findByName(name);
            for (FuncVar f : funcs) {
                funcRepo.deleteById(f.getId());
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    public void loadFuncs(List<FuncVar> funcVars){
        funcRepo.saveAll(funcVars);
    }

    /**
     * Загрузить Лист из файла
     * @param directory
     * @param file
     * @return
     */
    public List<FuncVar> loadDocument(String directory, String file) {
        File loadFile = new File(directory, file);
        ObjectMapper mapper = new ObjectMapper();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loadFile));
            String json = reader.readLine();

            return List.of(mapper.readValue(json, FuncVar[].class));
        } catch (IOException e){
            e.printStackTrace();

            return null;
        }
    }

    public FuncVarRepository getFuncRepo(){
        return funcRepo;
    }

    public void setAnaliser(AnaliseExpression analiser) {
        this.analiser = analiser;
    }

    public AnaliseExpression getAnaliser() {
        return analiser;
    }
}
