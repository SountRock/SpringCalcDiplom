package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.rangeTable.Param;
import com.example.calculatorService.domain.table.rangeTable.Range;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
import com.example.calculatorService.domain.table.rangeTable.ResultWithParams;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class RangeTableService implements ReferenceService {
    @Autowired
    private RangeTableRepository tableRepo;
    private FuncVarRepository funcRepo;
    private CustomFunctionRepository customRepo;

    @Autowired
    private AnaliseExpression analiser;
    @Autowired
    private PrepareExpression preparator;

    @Autowired
    public void setTableRepo(@Lazy FuncVarRepository funcRepo) {
        this.funcRepo = funcRepo;
    }

    @Autowired
    public void setCustomRepo(@Lazy CustomFunctionRepository customRepo) {
        this.customRepo = customRepo;
    }

    /**
     * Добавить функцию
     * @param table
     */
    public void addTable(RangeTable table){
        table.setCreateDate(LocalDateTime.now());
        tableRepo.save(table);
    }

    /**
     * Получить все таблицы
     */
    public List<RangeTable> getAllTables() {
        return tableRepo.findAll();
    }

    /**
     * Найти таблицу по id
     * @param id
     * @return
     */
    public RangeTable findById(Long id){
        return tableRepo.findById(id).get();
    }

    /**
     * Получить все таблицы по имени
     */
    public List<RangeTable> getTableByName(String name){
        return tableRepo.findByName(name);
    }


    /**
     * Очистить все таблицы
     */
    public void deleteTables(){
        tableRepo.deleteAll();
    }

    /**
     * Удалить таблицу по id
     * @param id
     * @return
     */
    public ResponseEntity deleteById(long id){
        try {
            tableRepo.deleteTable(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить таблицу по имени
     * @param name
     * @return
     */
    public ResponseEntity deleteByName(String name){
        try {
            RangeTable table = tableRepo.findByName(name).get(0);
            tableRepo.deleteTable(table.getId());

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Расчитать таблицу
     */
    public ResponseEntity<String> calculateTable(RangeTable table, List<Range> ranges){
        try {
            if(table != null){
                //Подготовительный этап
                double[] currentValues = new double[ranges.size()];
                for (int i = 0; i < currentValues.length; i++) {
                    currentValues[i] = ranges.get(i).getStart() - ranges.get(i).getStep();
                }
                List<String> prepareExpression = preparator.decompose(table.getExpression());

                //Проверям наличие ссылок с FuncVar
                prepareExpression = findFuncVarReferencesById(prepareExpression, funcRepo);
                prepareExpression = findFuncVarReferencesByName(prepareExpression, funcRepo);

                //Проверям наличие ссылок с RangeTable
                prepareExpression = findRangeTableReferencesById(prepareExpression, tableRepo);
                prepareExpression = findRangeTableReferencesByName(prepareExpression, tableRepo);
                prepareExpression = calculateRangeTableReferences(prepareExpression, tableRepo, customRepo, analiser);

                //Расчитываем значения
                List<ResultWithParams> results = new ArrayList<>();
                results = calculateIteration(prepareExpression, ranges, currentValues, results);

                String resultsString = "";
                if(results != null){
                    tableRepo.save(table);
                    for(ResultWithParams r : results) {
                        List<Param> temp = r.getParams();
                        for (Param p : temp) {
                            resultsString += p.getName() + "=" + p.getValue() +"; ";
                        }
                        resultsString += " | " +
                                r.getResult().toString()
                                .replaceAll("\\[", "")
                                .replaceAll("\\]", "")
                                .replaceAll(",", "") +
                                ",\n";
                    }

                    table.setRanges(ranges);
                    table.setResults(results);
                    tableRepo.save(table);
                } else {
                    throw new NullPointerException();
                }

                return new ResponseEntity<>(resultsString, HttpStatus.OK);
            }

            return new ResponseEntity<>("Table is Null", HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        } catch (ReferenceResultIsEmpty e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (TableReferenceErrorException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e){
            return new ResponseEntity<>("This name already exists", HttpStatus.CONFLICT);
        }
    }

    /**
     * Итерация расчета таблицы
     * @param expression
     * @param ranges
     * @param currentValues
     * @return
     */
    public  List<ResultWithParams> calculateIteration(List<String> expression, List<Range> ranges, double[] currentValues, List<ResultWithParams> results){
        if(expression != null){
            boolean isEnd = true;
            for (int i = 0; i < currentValues.length; i++) {
                double valueWithStep = currentValues[i] + ranges.get(i).getStep();
                if(valueWithStep < ranges.get(i).getEnd()){
                    currentValues[i] = valueWithStep;
                    isEnd = false;
                } else {
                    currentValues[i] = ranges.get(i).getEnd();
                }
            }

            List<String> expressionTemp = new ArrayList<>(expression);
            boolean isAllPasted = false;
            for (int j = 0; j < expressionTemp.size(); j++) {
                for (int i = 0; i < currentValues.length; i++) {
                    if(expressionTemp.get(j).equals(ranges.get(i).getName())){
                        expressionTemp.set(j, "" + currentValues[i]);
                        isAllPasted = true;
                    }
                }
            }
            //На случай если переменные с диапазона обновились и их нужно вытащить из "обертки" в которую они попали ранее
            if(isAllPasted){
                for (int i = 0; i < expressionTemp.size(); i++) {
                    if(!expressionTemp.get(i).equals("[") && expressionTemp.get(i).equals("]")){
                        expressionTemp.remove(i);
                        i--;
                    }
                }
            }

            //Проверям наличие ссылок на Custom Function
            List<String> result = findNCalculateCustomFunc(expressionTemp, customRepo, analiser);
            result = analiser.analise(expressionTemp);
            List<Param> params = new ArrayList<>();
            for (int i = 0; i < currentValues.length; i++) {
                params.add(new Param(ranges.get(i).getName(), Double.toString(currentValues[i])));
            }

            results.add(new ResultWithParams(params, result));


            if(!isEnd){
                return calculateIteration(expression, ranges, currentValues, results);
            } else {
                return results;
            }
        }

        return null;
    }

    /**
     * Расчитать таблицу равномерно для всех диапазонов
     */
    public ResponseEntity<String> calculateTable(RangeTable table, List<Range> ranges, int maxCountIteration) {
        try {
            if (table != null) {
                //Подготовительный этап
                double[] currentValues = new double[ranges.size()];
                for (int i = 0; i < currentValues.length; i++) {
                    currentValues[i] = ranges.get(i).getStart() - ranges.get(i).getStep();
                }
                List<String> prepareExpression = preparator.decompose(table.getExpression());

                //Проверям наличие ссылок с FuncVar
                prepareExpression = findFuncVarReferencesById(prepareExpression, funcRepo);
                prepareExpression = findFuncVarReferencesByName(prepareExpression, funcRepo);

                //Проверям наличие ссылок с RangeTable
                prepareExpression = findRangeTableReferencesById(prepareExpression, tableRepo);
                prepareExpression = findRangeTableReferencesByName(prepareExpression, tableRepo);
                prepareExpression = calculateRangeTableReferences(prepareExpression, tableRepo, customRepo, analiser);

                //Расчитываем значения
                List<ResultWithParams> results = new ArrayList<>();
                results = calculateIterationWithCount(prepareExpression, ranges, currentValues, results, 1, maxCountIteration);

                String resultsString = "";
                if (results != null) {
                    tableRepo.save(table);
                    for (ResultWithParams r : results) {
                        List<Param> temp = r.getParams();
                        for (Param p : temp) {
                            resultsString += p.getName() + "=" + p.getValue() +"; ";
                        }
                        resultsString += " | " +
                                r.getResult().toString()
                                        .replaceAll("\\[", "")
                                        .replaceAll("\\]", "")
                                        .replaceAll(",", "") +
                                ",\n";
                    }

                    table.setRanges(ranges);
                    table.setResults(results);
                    tableRepo.save(table);
                } else {
                    throw new NullPointerException();
                }

                return new ResponseEntity<>(resultsString, HttpStatus.OK);
            }

            return new ResponseEntity<>("Table is Null", HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        } catch (ReferenceResultIsEmpty e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (TableReferenceErrorException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e){
            return new ResponseEntity<>("This table name already exists", HttpStatus.CONFLICT);
        }
    }

    /**
     * Итерация расчета таблицы
     * @param expression
     * @param ranges
     * @param currentValues
     * @return
     */
    public List<ResultWithParams> calculateIterationWithCount(List<String> expression, List<Range> ranges, double[] currentValues, List<ResultWithParams> results, int currentCountIteration, int maxCountIteration){
        if(expression != null){
            boolean isEnd = true;
            for (int i = 0; i < currentValues.length; i++) {
                double valueWithStep = currentValues[i] + ranges.get(i).getStep();
                if(currentCountIteration < maxCountIteration){
                    currentValues[i] = valueWithStep;
                    isEnd = false;
                } else {
                    currentValues[i] = ranges.get(i).getEnd();
                }
            }

            List<String> expressionTemp = new ArrayList<>(expression);
            boolean isAllPasted = false;
            for (int j = 0; j < expressionTemp.size(); j++) {
                for (int i = 0; i < currentValues.length; i++) {
                    if(expressionTemp.get(j).equals(ranges.get(i).getName())){
                        expressionTemp.set(j, "" + currentValues[i]);
                        isAllPasted = true;
                    }
                }
            }
            //На случай если переменные с диапазона обновились и их нужно вытащить из "обертки" в которую они попали ранее

            if(isAllPasted){
                for (int i = 0; i < expressionTemp.size(); i++) {
                    if(!expressionTemp.get(i).equals("[") && expressionTemp.get(i).equals("]")){
                        expressionTemp.remove(i);
                        i--;
                    }
                }
            }

            //Проверям наличие ссылок на Custom Function
            List<String> result = findNCalculateCustomFunc(expressionTemp, customRepo, analiser);
            result = analiser.analise(expressionTemp);
            List<Param> params = new ArrayList<>();
            for (int i = 0; i < currentValues.length; i++) {
                params.add(new Param(ranges.get(i).getName(), Double.toString(currentValues[i])));
            }

            results.add(new ResultWithParams(params, result));
            currentCountIteration++;

            if(!isEnd){
                return calculateIterationWithCount(expression, ranges, currentValues, results, currentCountIteration, maxCountIteration);
            } else {
                return results;
            }
        }

        return null;
    }

    public void loadTables(List<RangeTable> tables){
        tableRepo.saveAll(tables);
    }

    /**
     * Загрузить Таблицу из файла
     * @param directory
     * @param file
     * @return
     */
    public List<RangeTable> loadDocument(String directory, String file) {
        File loadFile = new File(directory, file);
        ObjectMapper mapper = new ObjectMapper();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loadFile));
            String json = reader.readLine();

            return List.of(mapper.readValue(json, RangeTable[].class));
        } catch (IOException e){
            e.printStackTrace();

            return null;
        }
    }
}
