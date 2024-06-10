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

    @Column(name="`name`")
    private String name;

    @Column(name="`steps`")
    @OneToMany(mappedBy = "customFunction", cascade = CascadeType.ALL)
    private List<CustomFunctionVar> steps;

    @Column(name="`result`")
    private List<String> result;

    @Column(name="`description`")
    private String description;
}
