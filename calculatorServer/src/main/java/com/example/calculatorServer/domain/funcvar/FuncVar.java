package com.example.calculatorServer.domain.funcvar;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Data;

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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createDate;
    ////////////////////////////////////////////////////////////////////
    @Nonnull
    @Column(name="`expression`")
    private String expression;

    @Column(name="`result`")
    private List<String> result;
    @Column(name="`resultString`")
    private String resultString;
    ////////////////////////////////////////////////////////////////////

    public FuncVar(@Nonnull String expression) {
        this.expression = expression;
    }

    public FuncVar() {}
}
