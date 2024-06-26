package com.example.functionRepositoryService.domain;

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
    @Column(name="`workExpression`")
    private List<String> workExpression;
    @Column(name="`defaultValue`")
    private List<String> defaultValue;

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
