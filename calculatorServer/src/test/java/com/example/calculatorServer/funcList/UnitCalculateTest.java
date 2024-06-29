package com.example.calculatorServer.funcList;

import com.example.calculatorServer.domain.funcvar.FuncVar;
import com.example.calculatorServer.repository.CustomFunctionRepository;
import com.example.calculatorServer.repository.FuncTableRepository;
import com.example.calculatorServer.repository.FuncVarRepository;
import com.example.calculatorServer.repository.RangeTableRepository;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UnitCalculateTest {
    @Mock
    private FuncVarRepository baseRepo;
    @Mock
    private CustomFunctionRepository customFuncRepo;
    @Mock
    private RangeTableRepository tableRepo;
    @Mock
    private FuncTableRepository ftRepo;

    @Mock
    private PrepareExpression preparator;

    @InjectMocks
    private FuncVarService service;

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

        FuncVar function = new FuncVar();
        function.setId(1L);
        function.setName("Test_1");
        function.setExpression("11*2*sqrt(12*2.7 + 0.4)*fact(12+4*sqrt(20))-2.0E34:(1+1)");
        service.calculateFunction(function);

        given(baseRepo.findById(function.getId())).willReturn(Optional.of(function));

        Assertions.assertEquals("Test_1", baseRepo.findById(1L).get().getName());
        Assertions.assertEquals("1.2838714875654028E34", baseRepo.findById(1L).get().getResultString());
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

        FuncVar function = new FuncVar();
        function.setId(2L);
        function.setName("Test_2");
        function.setExpression("sqrt(33 * fact(5))-100:7*8");
        service.calculateFunction(function);

        given(baseRepo.findById(function.getId())).willReturn(Optional.of(function));

        Assertions.assertEquals("Test_2", baseRepo.findById(2L).get().getName());
        Assertions.assertEquals("-51.35718339550527", baseRepo.findById(2L).get().getResultString());
    }
}
