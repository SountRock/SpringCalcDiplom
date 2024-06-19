package com.example.calculatorService.domain.customFunc;

import com.example.calculatorService.domain.table.funcTable.FuncTable;
import com.example.calculatorService.domain.table.rangeTable.RangeTable;
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
    @Column(name="`value`")
    private List<String> value;
    @Column(name="`defaultValue`")
    private List<String> defaultValue;

    @Column(name="`type`")
    @Enumerated
    private TypeVar type;
}
