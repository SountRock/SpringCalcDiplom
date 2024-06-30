package com.example.calculatorServer.rangeTable;

import com.example.calculatorServer.domain.funcvar.FuncVar;
import com.example.calculatorServer.domain.table.funcTable.FuncTableCell;
import com.example.calculatorServer.domain.table.rangeTable.Range;
import com.example.calculatorServer.domain.table.rangeTable.RangeTable;
import com.example.calculatorServer.repository.*;
import com.example.calculatorServer.service.ImplService.FuncTableService;
import com.example.calculatorServer.service.ImplService.FuncVarService;
import com.example.calculatorServer.service.ImplService.RangeTableService;
import com.example.calculatorServer.service.MathModels.*;
import com.example.calculatorServer.service.Tools.AnaliseExpression;
import com.example.calculatorServer.service.Tools.PrepareExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class IntegrationReferencesTest {
    @MockBean
    private RangeTableRepository baseRepo;
    @MockBean
    private FuncTableRepository ftRepo;
    @MockBean
    private FuncTableCellRepository ftcRepo;
    @MockBean
    private CustomFunctionRepository customFuncRepo;
    @MockBean
    private FuncVarRepository funcRepo;

    @MockBean
    private PrepareExpression preparator;

    @Autowired
    private RangeTableService service;

    @Autowired
    private FuncVarService funcService;

    @Autowired
    private FuncTableService funcTableService;

    @Test
    public void UTestRefForId(){
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

        /////////////////////////////////////////////////////////
        RangeTable table = new RangeTable();
        table.setId(1L);
        table.setName("table_1");
        table.setExpression("x+y");
        ////////////////////
        Range x = new Range();
        x.setName("x");
        x.setStart(1.0);
        x.setEnd(4.0);
        x.setStep(1.0);

        Range y = new Range();
        y.setName("y");
        y.setStart(1.0);
        y.setEnd(8.0);
        y.setStep(2.0);

        List<Range> ranges = new ArrayList<>();
        ranges.add(x);
        ranges.add(y);
        ////////////////////
        service.calculateTable(table, ranges);
        when(baseRepo.findById(table.getId())).thenReturn(Optional.of(table));
        /////////////////////////////////////////////////////////
        FuncVar function = new FuncVar();
        function.setId(1L);
        function.setName("res");
        function.setExpression("tref(1, 3) + 100");
        funcService.calculateFunction(function);
        when(funcRepo.findById(function.getId())).thenReturn(Optional.of(function));

        Assertions.assertEquals("108.0", funcRepo.findById(1L).get().getResultString());

        function.setExpression("tref(1, 5) + 100");
        funcService.calculateFunction(function);
        when(funcRepo.findById(function.getId())).thenReturn(Optional.of(function));

        Assertions.assertEquals("112.0", funcRepo.findById(1L).get().getResultString());

        function.setExpression("tcalc(1, x=22&y=50) + 100");
        funcService.calculateFunction(function);
        when(funcRepo.findById(function.getId())).thenReturn(Optional.of(function));

        Assertions.assertEquals("172.0", funcRepo.findById(1L).get().getResultString());
    }

    @Test
    public void UTestRefForNameForFuncList(){
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

        /////////////////////////////////////////////////////////
        RangeTable table = new RangeTable();
        table.setId(1L);
        table.setName("table_1");
        table.setExpression("x+y");
        ////////////////////
        Range x = new Range();
        x.setName("x");
        x.setStart(1.0);
        x.setEnd(4.0);
        x.setStep(1.0);

        Range y = new Range();
        y.setName("y");
        y.setStart(1.0);
        y.setEnd(8.0);
        y.setStep(2.0);

        List<Range> ranges = new ArrayList<>();
        ranges.add(x);
        ranges.add(y);
        ////////////////////
        service.calculateTable(table, ranges);
        when(baseRepo.findById(table.getId())).thenReturn(Optional.of(table));
        /////////////////////////////////////////////////////////

        when(baseRepo.findByName("table_1")).thenReturn(
                List.of(
                        table
                )
        );

        FuncVar function = new FuncVar();
        function.setId(1L);
        function.setName("res");
        function.setExpression("tname(table_1, 3) + 100");
        funcService.calculateFunction(function);
        when(funcRepo.findById(function.getId())).thenReturn(Optional.of(function));

        Assertions.assertEquals("108.0", funcRepo.findById(1L).get().getResultString());

        function.setExpression("tname(table_1, 5) + 100");
        funcService.calculateFunction(function);
        when(funcRepo.findById(function.getId())).thenReturn(Optional.of(function));

        Assertions.assertEquals("112.0", funcRepo.findById(1L).get().getResultString());

        function.setExpression("tcalc(table_1, x=22&y=50) + 100");
        funcService.calculateFunction(function);
        when(funcRepo.findById(function.getId())).thenReturn(Optional.of(function));

        Assertions.assertEquals("172.0", funcRepo.findById(1L).get().getResultString());
    }

    @Test
    public void UTestRefForNameForFuncTable(){
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

        /////////////////////////////////////////////////////////
        RangeTable table = new RangeTable();
        table.setId(1L);
        table.setName("table_1");
        table.setExpression("x+y");
        ////////////////////
        Range x = new Range();
        x.setName("x");
        x.setStart(1.0);
        x.setEnd(4.0);
        x.setStep(1.0);

        Range y = new Range();
        y.setName("y");
        y.setStart(1.0);
        y.setEnd(8.0);
        y.setStep(2.0);

        List<Range> ranges = new ArrayList<>();
        ranges.add(x);
        ranges.add(y);
        ////////////////////
        service.calculateTable(table, ranges);
        when(baseRepo.findById(table.getId())).thenReturn(Optional.of(table));
        /////////////////////////////////////////////////////////

        when(baseRepo.findByName("table_1")).thenReturn(
                List.of(
                        table
                )
        );

        FuncTableCell result = new FuncTableCell();
        result.setId(4L);
        result.setCellName("Result");
        result.setCellCount(1);
        result.setExpression("tname(table_1, 3) + 100");
        funcTableService.calculateCellInRecord("Result", result);
        when(ftcRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("108.0", funcTableService.findCellById(4L).getBody().getResultString());

        result.setExpression("tname(table_1, 5) + 100");
        funcTableService.calculateCellInRecord("Result", result);
        when(ftcRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("112.0", funcTableService.findCellById(4L).getBody().getResultString());

        result.setExpression("tcalc(table_1, x=22&y=50) + 100");
        funcTableService.calculateCellInRecord("Result", result);
        when(ftcRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("172.0", funcTableService.findCellById(4L).getBody().getResultString());
    }
}
