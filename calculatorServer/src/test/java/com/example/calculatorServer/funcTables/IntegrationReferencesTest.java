package com.example.calculatorServer.funcTables;

import com.example.calculatorServer.domain.table.funcTable.FuncTable;
import com.example.calculatorServer.domain.table.funcTable.FuncTableCell;
import com.example.calculatorServer.repository.*;
import com.example.calculatorServer.service.ImplService.FuncTableService;
import com.example.calculatorServer.service.MathModels.*;
import com.example.calculatorServer.service.Tools.AnaliseExpression;
import com.example.calculatorServer.service.Tools.PrepareExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class IntegrationReferencesTest {
    @MockBean
    private FuncTableRepository baseRepo;
    @MockBean
    private FuncTableCellRepository baseCellRepo;
    @MockBean
    private CustomFunctionRepository customFuncRepo;
    @MockBean
    private RangeTableRepository tableRepo;
    @MockBean
    private FuncVarRepository funcRepo;

    @MockBean
    private PrepareExpression preparator;

    @Autowired
    private FuncTableService service;

    @Test
    public void ITestRef() {
        //Из-за особенностей содержания Анализатора Выражений установим его таким образом на сервер
        AnaliseExpression analiser = new AnaliseExpression(
                List.of(
                        new ModelFactorial(),
                        new ModelSquare(),
                        new ModelDivitev2(),
                        new ModelMultiplyv2(),
                        new ModelMinusv2(),
                        new ModelPlusv2()
                )
        );
        service.setAnaliser(analiser);
        Assertions.assertEquals(analiser, service.getAnaliser());

        //One Test Record//////////////////
        FuncTableCell cell = new FuncTableCell();
        cell.setId(1L);
        cell.setCellName("Cell_1");
        cell.setCellCount(1);
        cell.setExpression("100");
        service.calculateCellInRecord("Record_1", cell);
        when(baseCellRepo.findById(cell.getId())).thenReturn(Optional.of(cell));
        /////////////////////////////////
        //Two Test Record//////////////////
        FuncTableCell cell2 = new FuncTableCell();
        cell2.setId(2L);
        cell2.setCellName("Cell_2");
        cell2.setCellCount(1);
        cell2.setExpression("259");
        service.calculateCellInRecord("Record_1", cell2);
        when(baseCellRepo.findById(cell2.getId())).thenReturn(Optional.of(cell2));
        /////////////////////////////////
        //Tree Test Record////////////////
        FuncTableCell cell3 = new FuncTableCell();
        cell3.setId(3L);
        cell3.setCellName("Cell_1");
        cell3.setCellCount(1);
        cell3.setExpression("300");
        service.calculateCellInRecord("Record_2", cell3);
        when(baseCellRepo.findById(cell3.getId())).thenReturn(Optional.of(cell3));
        /////////////////////////////////

        //Ref Test//////////////////////
        FuncTableCell result = new FuncTableCell();
        result.setId(4L);
        result.setCellName("Result");
        result.setCellCount(1);
        result.setExpression("ftref(1)+ftref(2)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("359.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("ftref(1..3)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("659.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("ftref(1..3,-):100");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("-4.59", service.findCellById(4L).getBody().getResultString());
        /////////////////////////////////
    }

    @Test
    public void ITestCount() {
        //Из-за особенностей содержания Анализатора Выражений установим его таким образом на сервер
        AnaliseExpression analiser = new AnaliseExpression(
                List.of(
                        new ModelFactorial(),
                        new ModelSquare(),
                        new ModelDivitev2(),
                        new ModelMultiplyv2(),
                        new ModelMinusv2(),
                        new ModelPlusv2()
                )
        );
        service.setAnaliser(analiser);
        Assertions.assertEquals(analiser, service.getAnaliser());

        //One Test Record//////////////////
        FuncTable table = new FuncTable();
        table.setId(1L);
        table.setRecordName("record_1");
        service.addRecord(table);
        when(baseRepo.findById(table.getId())).thenReturn(Optional.of(table));
        /////////////////////////////////
        //Two Test Record//////////////////
        FuncTable table2 = new FuncTable();
        table2.setId(2L);
        table2.setRecordName("record_2");
        service.addRecord(table2);
        when(baseRepo.findById(table2.getId())).thenReturn(Optional.of(table2));
        /////////////////////////////////

        when(baseRepo.findByRecordName("record_1")).thenReturn(
                List.of(
                        table
                )
        );
        when(baseRepo.findByRecordName("record_2")).thenReturn(
                List.of(
                        table2
                )
        );

        //One Test Record Cell//////////////////
        FuncTableCell cell = new FuncTableCell();
        cell.setId(1L);
        cell.setCellName("Cell_1");
        cell.setCellCount(1);
        cell.setExpression("100");
        service.calculateCellInRecord("record_1", cell);
        when(baseCellRepo.findById(cell.getId())).thenReturn(Optional.of(cell));
        /////////////////////////////////
        //Two Test Record Cell//////////////////
        FuncTableCell cell2 = new FuncTableCell();
        cell2.setId(2L);
        cell2.setCellName("Cell_2");
        cell2.setCellCount(1);
        cell2.setExpression("259");
        service.calculateCellInRecord("record_1", cell2);
        when(baseCellRepo.findById(cell2.getId())).thenReturn(Optional.of(cell2));
        /////////////////////////////////
        //Tree Test Record Cell////////////////
        FuncTableCell cell3 = new FuncTableCell();
        cell3.setId(3L);
        cell3.setCellName("Cell_1");
        cell3.setCellCount(1);
        cell3.setExpression("300");
        service.calculateCellInRecord("record_2", cell3);
        when(baseCellRepo.findById(cell3.getId())).thenReturn(Optional.of(cell3));
        /////////////////////////////////

        when(baseCellRepo.findByCellName("Cell_1")).thenReturn(
                List.of(
                        cell,
                        cell3
                )
        );
        when(baseCellRepo.findByCellName("Cell_2")).thenReturn(
                List.of(
                        cell2
                )
        );

        //Ref Test//////////////////////
        FuncTableCell result = new FuncTableCell();
        result.setId(4L);
        result.setCellName("Result");
        result.setCellCount(1);
        result.setExpression("count(record_1, 1)+count(record_2, 1)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("400.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("count(record_1, 1..2)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("359.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("(count(record_1, 1..2, -) - count(record_2, 1)) : 100");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("-4.59", service.findCellById(4L).getBody().getResultString());
        /////////////////////////////////
    }

    @Test
    public void ITestName() {
        //Из-за особенностей содержания Анализатора Выражений установим его таким образом на сервер
        AnaliseExpression analiser = new AnaliseExpression(
                List.of(
                        new ModelFactorial(),
                        new ModelSquare(),
                        new ModelDivitev2(),
                        new ModelMultiplyv2(),
                        new ModelMinusv2(),
                        new ModelPlusv2()
                )
        );
        service.setAnaliser(analiser);
        Assertions.assertEquals(analiser, service.getAnaliser());

        //One Test Record//////////////////
        FuncTable table = new FuncTable();
        table.setId(1L);
        table.setRecordName("record_1");
        service.addRecord(table);
        when(baseRepo.findById(table.getId())).thenReturn(Optional.of(table));
        /////////////////////////////////
        //Two Test Record//////////////////
        FuncTable table2 = new FuncTable();
        table2.setId(2L);
        table2.setRecordName("record_2");
        service.addRecord(table2);
        when(baseRepo.findById(table2.getId())).thenReturn(Optional.of(table2));
        /////////////////////////////////

        when(baseRepo.findByRecordName("record_1")).thenReturn(
                List.of(
                        table
                )
        );
        when(baseRepo.findByRecordName("record_2")).thenReturn(
                List.of(
                        table2
                )
        );

        //One Test Record Cell//////////////////
        FuncTableCell cell = new FuncTableCell();
        cell.setId(1L);
        cell.setCellName("cell_1");
        cell.setCellCount(1);
        cell.setExpression("100");
        service.calculateCellInRecord("record_1", cell);
        when(baseCellRepo.findById(cell.getId())).thenReturn(Optional.of(cell));
        /////////////////////////////////
        //Two Test Record Cell//////////////////
        FuncTableCell cell2 = new FuncTableCell();
        cell2.setId(2L);
        cell2.setCellName("cell_2");
        cell2.setCellCount(1);
        cell2.setExpression("259");
        service.calculateCellInRecord("record_2", cell2);
        when(baseCellRepo.findById(cell2.getId())).thenReturn(Optional.of(cell2));
        /////////////////////////////////
        //Tree Test Record Cell////////////////
        FuncTableCell cell3 = new FuncTableCell();
        cell3.setId(3L);
        cell3.setCellName("cell_1");
        cell3.setCellCount(1);
        cell3.setExpression("300");
        service.calculateCellInRecord("record_1", cell3);
        when(baseCellRepo.findById(cell3.getId())).thenReturn(Optional.of(cell3));
        /////////////////////////////////

        when(baseCellRepo.findByCellName("cell_1")).thenReturn(
                List.of(
                        cell,
                        cell3
                )
        );
        when(baseCellRepo.findByCellName("cell_2")).thenReturn(
                List.of(
                        cell2
                )
        );

        //Ref Test//////////////////////
        FuncTableCell result = new FuncTableCell();
        result.setId(4L);
        result.setCellName("Result");
        result.setCellCount(1);
        result.setExpression("ftname(record_1, cell_1)+50");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("450.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("(ftname(record_1, cell_1, -) + ftname(record_2, cell_2)) : 100");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("0.59", service.findCellById(4L).getBody().getResultString());
        /////////////////////////////////
    }
}
