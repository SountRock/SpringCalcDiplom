package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.table.rangeTable.Range;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
import com.example.calculatorService.domain.table.rangeTable.ResultWithParams;
import com.example.calculatorService.exceptions.ReferenceResultIsEmpty;
import com.example.calculatorService.exceptions.TableExistException;
import com.example.calculatorService.exceptions.TableReferenceErrorException;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.ReferenceService;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RangeTableService implements ReferenceService {
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
     * Удалить таблицу
     * @param id
     */
    public void deleteFunction(Long id){
        tableRepo.deleteTable(id);
    }

    /**
     * Расчитать функцию
     */
    public String calculateTable(RangeTable table, List<Range> ranges){
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

                //Расчитываем значения
                List<ResultWithParams> results = new ArrayList<>();
                results = calculateIteration(prepareExpression, ranges, currentValues, results);

                String resultsString = "";
                if(results != null){
                    tableRepo.save(table);
                    for(ResultWithParams r : results) {
                        resultsString += r.getHead() + " | " +
                                r.getResult().toString()
                                .replaceAll("\\[", "")
                                .replaceAll("\\]", "")
                                .replaceAll(",", "") +
                                ",\n";
                        r.setRangeTable(table);
                    }
                    for (Range r : ranges) {
                        r.setRangeTable(table);
                    }

                    table.setRanges(ranges);
                    table.setResults(results);
                    tableRepo.save(table);
                } else {
                    throw new NullPointerException();
                }

                return resultsString;
            }

            return "Table is Null";
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return "Not Found";
        } catch (NullPointerException e){
            e.printStackTrace();
            return "One of Reference Result is empty OR Table Result is empty";
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

            List<String> result = analiser.analise(expressionTemp);
            String head = "";
            for (int i = 0; i < currentValues.length; i++) {
                head += ranges.get(i).getName() + "=" + currentValues[i] + ";";
            }

            results.add(new ResultWithParams(head, result));

            if(!isEnd){
                return calculateIteration(expression, ranges, currentValues, results);
            } else {
                return results;
            }
        }

        return null;
    }

    /**
     * Расчитать функцию
     */
    public String calculateTable(RangeTable table, List<Range> ranges, int maxCountIteration) {
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
                prepareExpression = calculateRangeTableReferences(prepareExpression, tableRepo, analiser);

                //Расчитываем значения
                List<ResultWithParams> results = new ArrayList<>();
                results = calculateIterationWithCount(prepareExpression, ranges, currentValues, results, 1, maxCountIteration);

                String resultsString = "";
                if (results != null) {
                    tableRepo.save(table);
                    for (ResultWithParams r : results) {
                        resultsString += r.getHead() + " | " +
                                r.getResult().toString()
                                        .replaceAll("\\[", "")
                                        .replaceAll("\\]", "")
                                        .replaceAll(",", "") +
                                ",\n";
                        r.setRangeTable(table);
                    }
                    for (Range r : ranges) {
                        r.setRangeTable(table);
                    }

                    table.setRanges(ranges);
                    table.setResults(results);
                    tableRepo.save(table);
                } else {
                    throw new NullPointerException();
                }

                return resultsString;
            }

            return "Table is Null";
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return "Not Found";
        } catch (ReferenceResultIsEmpty e){
            e.printStackTrace();
            return e.getMessage();
        } catch (TableReferenceErrorException e){
            e.printStackTrace();
            return e.getMessage();
        } catch (TableExistException e){
            e.printStackTrace();
            return e.getMessage();
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

            List<String> result = analiser.analise(expressionTemp);
            String head = "";
            for (int i = 0; i < currentValues.length; i++) {
                head += ranges.get(i).getName() + "=" + currentValues[i] + ";";
            }

            results.add(new ResultWithParams(head, result));
            currentCountIteration++;

            if(!isEnd){
                return calculateIterationWithCount(expression, ranges, currentValues, results, currentCountIteration, maxCountIteration);
            } else {
                return results;
            }
        }

        return null;
    }
}
