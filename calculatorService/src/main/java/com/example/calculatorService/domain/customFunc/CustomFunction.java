package com.example.calculatorService.domain.customFunc;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name ="CustomFunction")
public class CustomFunction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;
    @Column(name="`name`", unique = true)
    private String name;

    @Column(name="`countInputVars`")
    private int countInputVars;
    @Column(name="`steps`")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "customFunction_id")
    private List<CustomFunctionVar> steps;

    @Column(name="`repeatCount`")
    private String repeatCount;
    @Column(name="`typeSearch`")
    @Enumerated
    private TypeSearch typeSearch;

    @Column(name="`description`")
    private String description;
}
