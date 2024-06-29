package com.example.functionRepositoryServer;

import com.example.functionRepositoryServer.repository.CustomFunctionRepository;
import com.example.functionRepositoryServer.service.ImplService.CustomFuncService;
import com.example.functionRepositoryServer.service.MathModels.*;
import com.example.functionRepositoryServer.service.Tools.AnaliseExpression;
import com.example.functionRepositoryServer.service.Tools.PrepareExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UnitCreateTest {
    @Mock
    private CustomFunctionRepository customRepo;
    @Mock
    private AnaliseExpression analiser;
    @Mock
    private PrepareExpression preparator;
    @Mock
    private RestTemplate template;

    @InjectMocks
    private CustomFuncService service;

    @Test
    public void UTestCalc1() {
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

        //Описание шагов: name_step1=step_expression1<default_value&name_step2=step_expression2<default_value:count_repeat
        //Описание шапки функции: func_name(input_var1,input_var2,input_var3):type
        service.createCustomFunc("pow(number,degree):TWO_SIDES",
                "res=res*number<number:degree",
                "power");

        List<String> res = service.findNCalculateCustomFuncOnService(PrepareExpression.decompose("3pow4+100")).getBody();
        Assertions.assertEquals("181.0", res.get(0));

        res = service.findNCalculateCustomFuncOnService(PrepareExpression.decompose("(2*2)pow(3+1)+100")).getBody();
        Assertions.assertEquals("356", res.get(0));
    }
}
