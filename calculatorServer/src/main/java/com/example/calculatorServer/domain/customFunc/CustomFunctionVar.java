package com.example.calculatorServer.domain.customFunc;

import com.example.calculatorServer.domain.table.funcTable.FuncTable;
import com.example.calculatorServer.domain.table.rangeTable.RangeTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name ="CustomFunctionVar")
public class CustomFunctionVar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;

    @Column(name="`name`")
    private String name;

    @Column(name="`expression`")
    private List<String> expression;
    @Column(name="`expressionString`")
    private String expressionString;
    @Column(name="`value`")
    private List<String> value;
    @Column(name="`workExpression`")
    private List<String> workExpression;
    @Column(name="`defaultValue`")
    private List<String> defaultValue;
    @Column(name="`defaultValueString`")
    private String defaultValueString;

    @Column(name="`type`")
    @Enumerated
    private TypeVar type;

    /*
    @ManyToOne
    @JoinColumn(name = "customFunction_id")
    @JsonIgnore
    private CustomFunction customFunction;
     */
}
