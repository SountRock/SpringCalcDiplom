package com.example.calculatorService.funcList;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.example.calculatorService.repository.CustomFunctionRepository;
import com.example.calculatorService.repository.FuncVarRepository;
import com.example.calculatorService.repository.RangeTableRepository;
import com.example.calculatorService.service.ImplService.FuncVarService;
import com.example.calculatorService.service.MathModels.*;
import com.example.calculatorService.service.Tools.AnaliseExpression;
import com.example.calculatorService.service.Tools.PrepareExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class IntegrationReferencesTest {
    @MockBean
    private FuncVarRepository baseRepo;
    @MockBean
    private CustomFunctionRepository customFuncRepo;
    @MockBean
    private RangeTableRepository tableRepo;


    @MockBean
    private PrepareExpression preparator;

    @Autowired
    private FuncVarService service;

    @Test
    public void UTestRef(){
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
        FuncVar function = new FuncVar();
        function.setId(1L);
        function.setName("test");
        function.setExpression("100");
        service.calculateFunction(function);
        when(baseRepo.findById(function.getId())).thenReturn(Optional.of(function));
        /////////////////////////////////
        //Two Test Record//////////////////
        FuncVar function2 = new FuncVar();
        function2.setId(2L);
        function2.setName("test");
        function2.setExpression("55");
        service.calculateFunction(function2);
        when(baseRepo.findById(function2.getId())).thenReturn(Optional.of(function2));
        /////////////////////////////////
        //Tree Test Record////////////////
        FuncVar function3 = new FuncVar();
        function3.setId(3L);
        function3.setName("test");
        function3.setExpression("45.5");
        service.calculateFunction(function3);
        when(baseRepo.findById(function3.getId())).thenReturn(Optional.of(function3));
        /////////////////////////////////

        //Ref Test//////////////////////
        FuncVar result = new FuncVar();
        result.setId(4L);
        result.setName("result");
        result.setExpression("ref(1)+ref(2)");
        service.calculateFunction(result);
        when(baseRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("155.0", baseRepo.findById(4L).get().getResultString());

        result.setExpression("ref(1..3)");
        service.calculateFunction(result);
        when(baseRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("200.5", baseRepo.findById(4L).get().getResultString());

        result.setExpression("(ref(1..2,*):10)+99");
        service.calculateFunction(result);
        when(baseRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("649.0", baseRepo.findById(4L).get().getResultString());
        /////////////////////////////////
    }

    /*
    @Test
    public void UTestRefForName(){
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

        //One Test Record////////////////
        FuncVar function = new FuncVar();
        function.setId(1L);
        function.setName("test");
        function.setExpression("100");
        service.calculateFunction(function);
        when(baseRepo.findById(function.getId())).thenReturn(Optional.of(function));
        /////////////////////////////////
        //Two Test Record////////////////
        FuncVar function2 = new FuncVar();
        function2.setId(2L);
        function2.setName("test");
        function2.setExpression("55");
        service.calculateFunction(function2);
        when(baseRepo.findById(function2.getId())).thenReturn(Optional.of(function2));
        /////////////////////////////////
        //Tree Test Record///////////////
        FuncVar function3 = new FuncVar();
        function3.setId(3L);
        function3.setName("test");
        function3.setExpression("45.5");
        service.calculateFunction(function3);
        when(baseRepo.findById(function3.getId())).thenReturn(Optional.of(function3));
        /////////////////////////////////

        service.addFunc(baseRepo.findById(1L).get());
        service.addFunc(baseRepo.findById(2L).get());
        service.addFunc(baseRepo.findById(3L).get());

        //Ref Name Test//////////////////
        FuncVar result = new FuncVar();
        result.setId(4L);
        result.setName("result");
        result.setExpression("name(test)");
        service.calculateFunction(result);
        when(baseRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("200.5", baseRepo.findById(4L).get().getResultString());

        result.setExpression("name(test, *)");
        service.calculateFunction(result);
        when(baseRepo.findById(result.getId())).thenReturn(Optional.of(result));

        Assertions.assertEquals("250250.0", baseRepo.findById(4L).get().getResultString());
        /////////////////////////////////
    }
     */
}
