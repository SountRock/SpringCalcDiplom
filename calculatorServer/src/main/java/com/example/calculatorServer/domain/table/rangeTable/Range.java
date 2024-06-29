package com.example.calculatorServer.domain.table.rangeTable;

import com.example.calculatorServer.domain.table.funcTable.FuncTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Range")
public class Range {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;
    @Column(name="`name`")
    private String name;

    @Column(name="`start`")
    private double start;
    @Column(name="`end`")
    private double end;
    @Column(name="`step`")
    private double step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rangeTable_id")
    @JsonIgnore
    private RangeTable rangeTable;
}
