package com.example.calculatorServer.funcTables;

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
    public void UTestRef() {
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
        result.setExpression("ref(1)+ref(2)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("359.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("ref(1..3)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("659.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("ref(1..3,-):100");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("-4.59", service.findCellById(4L).getBody().getResultString());
        /////////////////////////////////
    }

    /*
    @Test
    public void UTestCount() {
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
        result.setExpression("count(Record_1, 1)+count(Record_2, 1)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("400.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("count(Record_1, 1..2)");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("359.0", service.findCellById(4L).getBody().getResultString());

        result.setExpression("(count(Record_1, 1..2, -) - count(Record_2, 1)) : 100");
        service.calculateCellInRecord("Result", result);
        when(baseCellRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("-4.59", service.findCellById(4L).getBody().getResultString());
        /////////////////////////////////
    }
     */
}
