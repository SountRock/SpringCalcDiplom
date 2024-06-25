package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.funcTable.FuncTableCell;
import com.example.calculatorService.exceptions.ReferenceResultIsEmpty;
import com.example.calculatorService.exceptions.TableReferenceErrorException;
import com.example.calculatorService.repository.*;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Service
@Transactional
public class FuncTableService implements ReferenceService {
    @Autowired
    private FuncTableRepository ftRepo;
    @Autowired
    private FuncTableCellRepository ftcRepo;

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

    public ResponseEntity<FuncTable> addNewRecord(String recordName){
        FuncTable record = new FuncTable();
        try{
            record.setRecordName(recordName);

            ftRepo.save(record);
            return new ResponseEntity<>(record, HttpStatus.OK);
        } catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(record, HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity addNewCellInRecord(String recordName, String cellName, String value){
        try {
            FuncTable record = ftRepo.findByRecordName(recordName).get(0);

            FuncTableCell newCell = new FuncTableCell();
            newCell.setCellName(cellName);
            newCell.setExpression(value);

            List<FuncTableCell> cells = record.getCells();
            long sizeRecord = cells.size();
            newCell.setCellCount(sizeRecord + 1);

            cells.add(newCell);
            record.setCells(cells);

            ftRepo.save(record);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Расчитать ячейку
     */
    public ResponseEntity<String> calculateCell(FuncTableCell cell){
        try {
            if(cell != null){
                List<String> prepareExpression = preparator.decompose(cell.getExpression());

                //Проверям наличие ссылок с FuncTable
                prepareExpression = findFuncTableReferencesById(prepareExpression, ftcRepo);
                prepareExpression = findFuncTableReferencesByCount(prepareExpression, ftRepo);
                prepareExpression = findFuncTableReferencesByName(prepareExpression, ftRepo);

                //Проверям наличие ссылок с RangeTable
                prepareExpression = findRangeTableReferencesById(prepareExpression, tableRepo);
                prepareExpression = findRangeTableReferencesByName(prepareExpression, tableRepo);
                prepareExpression = calculateRangeTableReferences(prepareExpression, tableRepo, customRepo, analiser);

                //Проверям наличие ссылок на Custom Function
                prepareExpression = findNCalculateCustomFunc(prepareExpression, customRepo, analiser);

                if(prepareExpression != null){
                    List<String> result = analiser.analise(prepareExpression);
                    cell.setResult(result);
                    String stringResult = result.toString()
                            .replaceAll("\\[", "")
                            .replaceAll("\\]", "")
                            .replaceAll(",", "");
                    cell.setResultString(stringResult);

                    ftcRepo.save(cell);

                    return new ResponseEntity<>(stringResult, HttpStatus.OK);
                }
            }

            return new ResponseEntity<>("Cell is Null", HttpStatus.NOT_FOUND);
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
     * Расчитать выражение в ячейке
     */
    public ResponseEntity<String> calculateCellInRecord(String recordName, FuncTableCell cell){
        try {
            if(cell != null){
                FuncTable record;
                try {
                    record = ftRepo.findByRecordName(recordName).get(0);
                } catch (NoSuchElementException | IndexOutOfBoundsException e){
                    record = new FuncTable();
                    record.setRecordName(recordName);
                }

                List<String> prepareExpression = preparator.decompose(cell.getExpression());

                //Проверям наличие ссылок с FuncTable
                prepareExpression = findFuncTableReferencesById(prepareExpression, ftcRepo);
                prepareExpression = findFuncTableReferencesByCount(prepareExpression, ftRepo);
                prepareExpression = findFuncTableReferencesByName(prepareExpression, ftRepo);

                //Проверям наличие ссылок с RangeTable
                prepareExpression = findRangeTableReferencesById(prepareExpression, tableRepo);
                prepareExpression = findRangeTableReferencesByName(prepareExpression, tableRepo);
                prepareExpression = calculateRangeTableReferences(prepareExpression, tableRepo, customRepo, analiser);

                //Проверям наличие ссылок на Custom Function
                prepareExpression = findNCalculateCustomFunc(prepareExpression, customRepo, analiser);

                if(prepareExpression != null){
                    List<String> result = analiser.analise(prepareExpression);
                    cell.setResult(result);
                    String stringResult = result.toString()
                            .replaceAll("\\[", "")
                            .replaceAll("\\]", "")
                            .replaceAll(",", "");
                    cell.setResultString(stringResult);

                    List<FuncTableCell> cells = record.getCells();
                    if(cells != null){
                        cell.setCellCount(cells.size() + 1);
                        cells.add(cell);
                    } else {
                        cells = new ArrayList<>();
                        cell.setCellCount(1);
                        cells.add(cell);
                    }

                    record.setCells(cells);
                    ftRepo.save(record);
                    return new ResponseEntity<>(stringResult, HttpStatus.OK);
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

    public ResponseEntity<FuncTable> findRecordByName(String name){
        try {
            return new ResponseEntity<>(ftRepo.findByRecordName(name).get(0), HttpStatus.OK);
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    public ResponseEntity<FuncTableCell> findCellById(long id){
        try {
            return new ResponseEntity<>(ftcRepo.findById(id).get(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<FuncTable>> getAll(){
        return new ResponseEntity<>(ftRepo.findAll(), HttpStatus.OK);
    }


    /**
     * Удалить запись по имени
     * @param name
     * @return
     */
    public ResponseEntity deleteRecordByName(String name){
        try {
            long id = ftRepo.
                    findByRecordName(name).
                        get(0).
                            getId();
            ftRepo.deleteFuncTable(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить запись по id
     * @param id
     * @return
     */
    public ResponseEntity deleteRecordById(long id){
        try {
            ftRepo.deleteFuncTable(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить ячеку по id
     * @param idRecord
     * @param idCell
     * @return
     */
    public ResponseEntity deleteCellInRecordById(long idRecord, long idCell){
        try {
            FuncTable temp = ftRepo.findById(idRecord).get();
            FuncTable table = new FuncTable();
            table.setRecordName(temp.getRecordName());
            table.setId(temp.getId());

            List<FuncTableCell> cells = temp.
                    getCells().
                    stream().
                    filter(c -> c.getId() != idCell).
                    collect(Collectors.toList());

            //Для того, чтобы не сбивался count ячеек
            AtomicLong count = new AtomicLong(1);
            cells.stream().forEach(c -> {
                c.setCellCount(count.get());
                count.getAndIncrement();
            });
            for (FuncTableCell c : cells) {
                temp.removeCell(c);
            }

            table.setCells(cells);
            ftRepo.save(table);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить ячеку по count
     * @param idRecord
     * @param countCell
     * @return
     */
    public ResponseEntity deleteCellInRecord(long idRecord, long countCell){
        try {
            FuncTable temp = ftRepo.findById(idRecord).get();
            FuncTable table = new FuncTable();
            table.setRecordName(temp.getRecordName());
            table.setId(temp.getId());

            List<FuncTableCell> cells = temp.
                    getCells().
                    stream().
                    filter(c -> c.getCellCount() != countCell).
                    collect(Collectors.toList());

            //Для того, чтобы не сбивался count ячеек
            AtomicLong count = new AtomicLong(1);
            cells.stream().forEach(c -> {
                c.setCellCount(count.get());
                count.getAndIncrement();
            });
            for (FuncTableCell c : cells) {
                temp.removeCell(c);
            }

            table.setCells(cells);
            ftRepo.save(table);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить ячеки по имени
     * @param idRecord
     * @param cellName
     * @return
     */
    public ResponseEntity deleteCellInRecord(long idRecord, String cellName){
        try {
            FuncTable temp = ftRepo.findById(idRecord).get();
            FuncTable table = new FuncTable();
            table.setRecordName(temp.getRecordName());
            table.setId(temp.getId());

            List<FuncTableCell> cells = temp.
                    getCells().
                    stream().
                    filter(c -> !c.getCellName().equals(cellName)).
                    collect(Collectors.toList());

            //Для того, чтобы не сбивался count ячеек
            AtomicLong count = new AtomicLong(1);
            cells.stream().forEach(c -> {
                c.setCellCount(count.get());
                count.getAndIncrement();
            });
            for (FuncTableCell c : cells) {
                temp.removeCell(c);
            }

            table.setCells(cells);
            ftRepo.save(table);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить ячеку по id, с указанием имени записи
     * @param recordName
     * @param countCell
     * @return
     */
    public ResponseEntity deleteCellInRecord(String recordName, long countCell){
        try {
            FuncTable temp = ftRepo.findByRecordName(recordName).get(0);
            FuncTable table = new FuncTable();
            table.setRecordName(temp.getRecordName());
            table.setId(temp.getId());

            List<FuncTableCell> cells = temp.
                    getCells().
                    stream().
                    filter(c -> c.getCellCount() != countCell).
                    collect(Collectors.toList());

            //Для того, чтобы не сбивался count ячеек
            AtomicLong count = new AtomicLong(1);
            cells.stream().forEach(c -> {
                c.setCellCount(count.get());
                count.getAndIncrement();
            });
            for (FuncTableCell c : cells) {
                temp.removeCell(c);
            }

            table.setCells(cells);
            ftRepo.save(table);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Удалить ячеки по имени, с указанием имени записи
     * @param recordName
     * @param cellName
     * @return
     */
    public ResponseEntity deleteCellInRecord(String recordName, String cellName){
        try {
            FuncTable temp = ftRepo.findByRecordName(recordName).get(0);
            FuncTable table = new FuncTable();
            table.setRecordName(temp.getRecordName());
            table.setId(temp.getId());

            List<FuncTableCell> cells = temp.
                    getCells().
                    stream().
                    filter(c -> !c.getCellName().equals(cellName)).
                    collect(Collectors.toList());
            table.setCells(cells);

            //Для того, чтобы не сбивался count ячеек
            AtomicLong count = new AtomicLong(1);
            cells.stream().forEach(c -> {
                c.setCellCount(count.get());
                count.getAndIncrement();
            });
            for (FuncTableCell c : cells) {
                temp.removeCell(c);
            }

            table.setCells(cells);
            ftRepo.save(table);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IndexOutOfBoundsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    public void loadTables(List<FuncTable> tables){
        ftRepo.saveAll(tables);
    }

    /**
     * Загрузить Таблицу из файла
     * @param directory
     * @param file
     * @return
     */
    public List<FuncTable> loadDocument(String directory, String file) {
        File loadFile = new File(directory, file);
        ObjectMapper mapper = new ObjectMapper();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loadFile));
            String json = reader.readLine();

            return List.of(mapper.readValue(json, FuncTable[].class));
        } catch (IOException e){
            e.printStackTrace();

            return null;
        }
    }

    public FuncTableRepository getFtRepo(){
        return ftRepo;
    }
}
