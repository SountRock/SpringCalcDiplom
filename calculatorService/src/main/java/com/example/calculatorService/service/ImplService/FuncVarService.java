package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.exceptions.ReferenceResultIsEmpty;
import com.example.calculatorService.exceptions.TableReferenceErrorException;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.ReferenceService;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Autowired
    private AnaliseExpression analiser;
    @Autowired
    private PrepareExpression preparator;

    @Autowired
    public void setTableRepo(@Lazy RangeTableRepository tableRepo) {
        this.tableRepo = tableRepo;
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
    public String calculateFunction(FuncVar function){
        try {
            if(function != null){
                List<String> prepareExpression = preparator.decompose(function.getExpression());
                //Проверям наличие ссылок с FuncVar
                prepareExpression = findFuncVarReferencesById(prepareExpression, funcRepo);
                prepareExpression = findFuncVarReferencesByName(prepareExpression, funcRepo);

                //Проверям наличие ссылок с RangeTable
                prepareExpression = findRangeTableReferencesById(prepareExpression, tableRepo);
                prepareExpression = findRangeTableReferencesByName(prepareExpression, tableRepo);
                prepareExpression = calculateRangeTableReferences(prepareExpression, tableRepo, analiser);

                if(prepareExpression != null){
                    List<String> result = analiser.analise(prepareExpression);
                    function.setResult(result);

                    funcRepo.save(function);

                    return result.toString()
                            .replaceAll("\\[", "")
                            .replaceAll("\\]", "")
                            .replaceAll(",", "");
                }
            }

            return "Function is Null";
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return "Not Found";
        } catch (ReferenceResultIsEmpty e){
            e.printStackTrace();
            return e.getMessage();
        } catch (TableReferenceErrorException e){
            e.printStackTrace();
            return e.getMessage();
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
