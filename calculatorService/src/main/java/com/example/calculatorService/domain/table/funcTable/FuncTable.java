package com.example.calculatorService.domain.table.funcTable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name ="FuncTable")
public class FuncTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;

    @Column(name="`recordName`", unique = true)
    private String recordName;

    @Column(name="`cells`")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "funcTable_id")
    private List<FuncTableCell> cells;
}