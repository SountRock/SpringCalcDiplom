package com.example.calculatorService.domain.table.rangeTable;

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
@Table(name = "RangeTable")
public class RangeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;

    @Column(name="`name`", unique = true)
    private String name;

    @Column(name="`createDate`")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createDate;
    ////////////////////////////////////////////////////////////////////
    @Nonnull
    @Column(name="`expression`")
    private String expression;

    @Column(name="`ranges`")
    //@OneToMany(mappedBy = "rangeTable", cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Range> ranges;

    @Column(name="`results`")
    //@OneToMany(mappedBy = "rangeTable", cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultWithParams> results;
    ////////////////////////////////////////////////////////////////////

    public RangeTable(@Nonnull String expression) {
        this.expression = expression;
    }

    public RangeTable() {}
}
