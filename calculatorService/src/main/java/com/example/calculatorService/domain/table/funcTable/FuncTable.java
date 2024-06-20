package com.example.calculatorService.domain.table.funcTable;

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
    private List<FuncTableCell> cells;
}
