package com.example.calculatorServer.rangeTable;

import com.example.calculatorServer.domain.table.rangeTable.Range;
import com.example.calculatorServer.domain.table.rangeTable.RangeTable;
import com.example.calculatorServer.repository.*;
import com.example.calculatorServer.service.ImplService.RangeTableService;
import com.example.calculatorServer.service.MathModels.*;
import com.example.calculatorServer.service.Tools.AnaliseExpression;
import com.example.calculatorServer.service.Tools.PrepareExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UnitCalculateTest {
    @Mock
    private RangeTableRepository baseRepo;
    @Mock
    private FuncTableCellRepository ftRepo;
    @Mock
    private CustomFunctionRepository customFuncRepo;
    @Mock
    private FuncVarRepository funcRepo;

    @Mock
    private PrepareExpression preparator;

    @InjectMocks
    private RangeTableService service;

    @Test
    public void UTestCalc1(){
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
        given(baseRepo.findById(table.getId())).willReturn(Optional.of(table));

        String res1 = service.findById(1L).getResults().get(0).getResultString();
        Assertions.assertEquals("2.0", res1);

        String res2 = service.findById(1L).getResults().get(2).getResultString();
        Assertions.assertEquals("8.0", res2);

        String res3 = service.findById(1L).getResults().get(4).getResultString();
        Assertions.assertEquals("12.0", res3);
    }

    @Test
    public void UTestCalc2(){
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

        RangeTable table = new RangeTable();
        table.setId(1L);
        table.setName("table_1");
        table.setExpression("x*y+y");
        ////////////////////
        Range x = new Range();
        x.setName("x");
        x.setStart(1.0);
        x.setEnd(4.0);
        x.setStep(1.0);

        Range y = new Range();
        y.setName("y");
        y.setStart(0.0);
        y.setEnd(14.0);
        y.setStep(3.5);

        List<Range> ranges = new ArrayList<>();
        ranges.add(x);
        ranges.add(y);
        ////////////////////
        service.calculateTable(table, ranges);
        given(baseRepo.findById(table.getId())).willReturn(Optional.of(table));

        String res1 = service.findById(1L).getResults().get(0).getResultString();
        Assertions.assertEquals("0.0", res1);

        String res2 = service.findById(1L).getResults().get(2).getResultString();
        Assertions.assertEquals("28.0", res2);

        String res3 = service.findById(1L).getResults().get(4).getResultString();
        Assertions.assertEquals("70.0", res3);
    }
}
