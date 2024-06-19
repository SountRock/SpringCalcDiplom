package com.example.calculatorService.service.ImplService;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.funcTable.FuncTableCell;
import com.example.calculatorService.exceptions.ReferenceResultIsEmpty;
import com.example.calculatorService.exceptions.TableReferenceErrorException;
import com.example.calculatorService.repository.CustomFunctionRepository;
import com.example.calculatorService.repository.FuncTableRepository;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.ReferenceService;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@Transactional
public class FuncTableService implements ReferenceService {
    @Autowired
    private FuncTableRepository ftRepo;
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
     * Расчитать функцию
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
                prepareExpression = findFuncTableReferencesById(prepareExpression, ftRepo);
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
    public ResponseEntity deleteCellInRecord(long idRecord, long idCell){
        try {
            FuncTable table = ftRepo.findById(idRecord).get();
            List<FuncTableCell> cells = table.
                    getCells().
                    stream().
                    filter(c -> c.getId() != idCell).
                    collect(Collectors.toList());
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
            FuncTable table = ftRepo.findById(idRecord).get();
            List<FuncTableCell> cells = table.
                    getCells().
                    stream().
                    filter(c -> !c.getCellName().equals(cellName)).
                    collect(Collectors.toList());
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
     * @param idCell
     * @return
     */
    public ResponseEntity deleteCellInRecord(String recordName, long idCell){
        try {
            FuncTable table = ftRepo.findByRecordName(recordName).get(0);
            List<FuncTableCell> cells = table.
                    getCells().
                    stream().
                    filter(c -> c.getId() != idCell).
                    collect(Collectors.toList());
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
            FuncTable table = ftRepo.findByRecordName(recordName).get(0);
            List<FuncTableCell> cells = table.
                    getCells().
                    stream().
                    filter(c -> !c.getCellName().equals(cellName)).
                    collect(Collectors.toList());
            table.setCells(cells);
            ftRepo.save(table);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
