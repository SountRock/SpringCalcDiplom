package com.example.calculatorServer.domain.table.rangeTable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Param")
public class Param {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;
    @Column(name="`name`")
    private String name;

    @Column(name="`value`")
    private String value;

    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Param() {
    }

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "resultWithParams_id")
    //@JsonIgnore
    //private ResultWithParams result;
}
