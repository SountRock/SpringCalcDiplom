package com.example.calculatorService.domain.customFunc;

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

    @Column(name="`result`")
    private List<String> result;

    @Column(name="`type`")
    private TypeVar type;

    @ManyToOne
    @JoinColumn(name = "customFunction_id")
    @JsonIgnore
    private CustomFunction customFunction;
}
