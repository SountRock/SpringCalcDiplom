package com.example.calculatorService.domain.funcvar;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name ="FuncVar")
public class FuncVar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;

    @Column(name="`name`")
    private String name;

    @Column(name="`createDate`")
    private LocalDateTime createDate;
    ////////////////////////////////////////////////////////////////////
    @Nonnull
    @Column(name="`expression`")
    private String expression;

    @Column(name="`result`")
    private List<String> result;
    ////////////////////////////////////////////////////////////////////

    public FuncVar(@Nonnull String expression) {
        this.expression = expression;
    }

    public FuncVar() {}
}
