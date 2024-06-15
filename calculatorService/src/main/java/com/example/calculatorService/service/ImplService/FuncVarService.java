package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.exceptions.ReferenceResultIsEmpty;
import com.example.calculatorService.exceptions.TableReferenceErrorException;
import com.example.calculatorService.repository.CustomFunctionRepository;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.CustomFuncRepositoryConnectServer;
import com.example.calculatorService.service.ReferenceService;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
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
     * Удалить записть из истории
     * @param id
     */
    public void deleteFunction(Long id){
        funcRepo.deleteFuncVar(id);
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

                    funcRepo.save(function);

                    return new ResponseEntity<>(
                            result.toString()
                                    .replaceAll("\\[", "")
                                    .replaceAll("\\]", "")
                                    .replaceAll(",", "")
                    , HttpStatus.OK);
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
    public List<FuncVar> getHistory() {
        return funcRepo.findAll();
    }

    /**
     * Найти функцию по id
     * @param id
     * @return
     */
    public FuncVar findById(Long id){
        return funcRepo.findById(id).get();
    }

}
