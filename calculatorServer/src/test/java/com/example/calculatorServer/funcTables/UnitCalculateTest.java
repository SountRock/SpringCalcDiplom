package com.example.calculatorServer.funcTables;


import com.example.calculatorServer.domain.funcvar.FuncVar;
import com.example.calculatorServer.domain.table.funcTable.FuncTableCell;
import com.example.calculatorServer.repository.*;
import com.example.calculatorServer.service.ImplService.FuncTableService;
import com.example.calculatorServer.service.ImplService.FuncVarService;
import com.example.calculatorServer.service.MathModels.*;
import com.example.calculatorServer.service.Tools.AnaliseExpression;
import com.example.calculatorServer.service.Tools.PrepareExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UnitCalculateTest {
    @Mock
    private FuncTableRepository baseRepo;
    @Mock
    private FuncTableCellRepository baseCellRepo;
    @Mock
    private CustomFunctionRepository customFuncRepo;
    @Mock
    private RangeTableRepository tableRepo;

    @Mock
    private PrepareExpression preparator;

    @InjectMocks
    private FuncTableService service;

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

        FuncTableCell cell = new FuncTableCell();
        cell.setId(1L);
        cell.setCellName("Cell_1");
        cell.setCellCount(1);
        cell.setExpression("11*2*sqrt(12*2.7 + 0.4)*fact(12+4*sqrt(20))-2.0E34:(1+1)");
        service.calculateCellInRecord("Record_1", cell);

        given(baseCellRepo.findById(cell.getId())).willReturn(Optional.of(cell));

        Assertions.assertEquals("1.2838714875654028E34", service.findCellById(1L).getBody().getResultString());
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

        FuncTableCell cell = new FuncTableCell();
        cell.setId(1L);
        cell.setCellName("Cell_1");
        cell.setCellCount(1);
        cell.setExpression("sqrt(33 * fact(5))-100:7*8");
        service.calculateCellInRecord("Record_1", cell);

        given(baseCellRepo.findById(cell.getId())).willReturn(Optional.of(cell));

        Assertions.assertEquals("-51.35718339550527", service.findCellById(1L).getBody().getResultString());
    }
}
