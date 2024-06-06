package com.example.calculatorService.service;

import com.example.calculatorService.domain.FuncVar;
import com.example.calculatorService.repository.FunctionRepository;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FuncVarService {
    @Autowired
    private FunctionRepository repo;

    @Autowired
    private AnaliseExpression analiser;

    @Autowired
    private PrepareExpression preparator;

    /**
     * Добавить функцию
     * @param function
     */
    public void addFunc(FuncVar function){
        function.setCreateDate(LocalDateTime.now());
        repo.save(function);
    }

    /**
     * Очистить историю
     */
    public void clearHistory(){
        repo.deleteAll();
    }

    /**
     * Удалить записть из истории
     * @param id
     */
    public void deleteFunction(Long id){
        repo.deleteFuncVar(id);
    }

    /**
     * Расчитать функцию
     */
    public String calculateFunction(FuncVar function){
        try {
            if(function != null){
                List<String> prepareExpression = preparator.decompose(function.getExpression());
                prepareExpression = findFuncVarReferencesById(prepareExpression);
                prepareExpression = findFuncVarReferencesByName(prepareExpression);
                if(prepareExpression != null){
                    List<String> result = analiser.analise(prepareExpression);
                    function.setResult(result);

                    repo.save(function);

                    return result.toString()
                            .replaceAll("\\[", "")
                            .replaceAll("\\]", "")
                            .replaceAll(",", "");
                }
            }

            return "Function is Null";
        } catch (NoSuchElementException e){
            return "Not Found";
        } catch (NullPointerException e){
            return "One of Reference Result is empty";
        }
    }

    /**
     * Найти ссылки на другие функции.
     * Синтаксис1: ref(номер_id);
     * Синтаксис2.1: ref(с_id..по_id);
     * Синтаксис2.2: ref(с_id..по_id, операция_объединения) операция_объединения может быть к примеру умножением (*), по умолчанию сложение (2.1);
     * @param expression
     * @return
     */
    private List<String> findFuncVarReferencesById(List<String> expression) throws NoSuchElementException, NullPointerException{
        if(expression == null){
            return null;
        }
        //вставка результата другого выражения по id
        for (int i = 0; i < expression.size(); i++) {
            if(expression.get(i).equals("ref")){
                try {
                    String ids = expression.get(i + 2).replaceAll(" ", "");
                    //Когда мы хотим получить несколько результатов по id и както их объединить
                    if(ids.indexOf("..") > 0) {
                        boolean isSuccess = false;
                        //Операция объединения результатов выражений по умолчанию сложение
                        String operationBetweenResultsById = "+";
                        byte sizeLabelRef = 4; //Размер самой ссылки в выражении, для того, чтобы мы могли ее стереть после вставки значения по ней
                        //Ищем указана ли операция объединения результатов выражений
                        if(expression.get(i + 3).equals(",")){
                            operationBetweenResultsById = expression.get(i + 4);
                            sizeLabelRef = 6;
                        }

                        //Теперь разделяем диапазон иднексов
                        String[] idsArr = ids.split("\\.\\.");
                        try {
                            long start = Long.parseLong(idsArr[0]);
                            long end = Long.parseLong(idsArr[1]);
                            List<String> result = new ArrayList<>();
                            for (long j = start; j < end + 1; j++) {
                                 FuncVar tempFunc = repo.findById(j).get();
                                 result.add("(");
                                 result.addAll(tempFunc.getResult());
                                 result.add(")");
                                 result.add(operationBetweenResultsById);
                            }

                            result.remove(result.size() - 1);
                            expression.addAll(i, result);
                            for (byte j = 0; j < sizeLabelRef; j++) {
                                expression.remove(i + result.size());
                            }
                            i += result.size() + 2;
                        } catch (ArrayIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    } else {
                        long idFunc = Long.parseLong(ids);

                        FuncVar temp = repo.findById(idFunc).get();
                        List<String> resultById = temp.getResult();
                        if(resultById != null){
                            expression.remove(i + 2);
                            expression.remove(i);
                            expression.addAll(i + 1, resultById);

                            i += resultById.size() + 2;
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();

                    return null;
                }
            }
        }

        return expression;
    }

    /**
     * Найти ссылки на другие функции по имени.
     * Синтаксис1: name(имя). Если только одна переменная имеет такое имя, то вернется ее значение.
     * Если несколько то вернеться их объединение.
     * Синтаксис2: name(имя,операция_объединения) операция_объединения может быть к примеру умножением (*), по умолчанию сложение (1);
     * @param expression
     * @return
     */
    private List<String> findFuncVarReferencesByName(List<String> expression) throws NoSuchElementException, NullPointerException{
        if(expression == null){
            return null;
        }

        //вставка результата другого выражения по имени
        for (int i = 0; i < expression.size(); i++) {
            if(expression.get(i).equals("name")){
                //Операция объединения результатов выражений по умолчанию сложение
                String operationBetweenResultsById = "+";
                byte sizeLabelRef = 4; //Размер самой ссылки в выражении, для того, чтобы мы могли ее стереть после вставки значения по ней
                if(expression.get(i + 3).equals(",")){
                    operationBetweenResultsById = expression.get(i + 4);
                    sizeLabelRef = 6;
                }

                String name = expression.get(i + 2).replaceAll(" ", "");

                List<FuncVar> temp = repo.findByName(name);
                //Если ничего не было найдено
                if(temp.size() < 1){
                    throw new NoSuchElementException();
                }

                List<String> result = new ArrayList<>();
                for (FuncVar t : temp) {
                    result.add("(");
                    result.addAll(t.getResult());
                    result.add(")");
                    result.add(operationBetweenResultsById);
                }

                result.remove(result.size() - 1);
                expression.addAll(i, result);
                for (byte j = 0; j < sizeLabelRef; j++) {
                    expression.remove(i + result.size());
                }

                i += result.size() + 2;
            }
        }

        return expression;
    }

    /**
     * Получить все функции по имени
     */
    public List<FuncVar> getFuncByName(String name){
        return repo.findByName(name);
    }

    /**
     * Получить историю вычислений
     */
    public List<FuncVar> getHistory() {
        return repo.findAll();
    }

    /**
     * Найти функцию по id
     * @param id
     * @return
     */
    public FuncVar findById(Long id){
        return repo.findById(id).get();
    }

}
