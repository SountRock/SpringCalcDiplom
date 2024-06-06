package com.example.calculatorService.domain;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "function")
public class FuncVar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;

    @Nonnull
    @Column(name="`expression`")
    private String expression;

    public FuncVar(@Nonnull String expression) {
        this.expression = expression;
    }

    public FuncVar() {}

    @Column(name="`name`")
    private String name;

    @Column(name="`result`")
    //private List<List<String>> result;
    private List<String> result;

    @Column(name="`createDate`")
    private LocalDateTime createDate;

}
