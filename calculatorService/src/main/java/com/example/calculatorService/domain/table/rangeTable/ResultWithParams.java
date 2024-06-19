package com.example.calculatorService.domain.table.rangeTable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "ResultWithParams")
public class ResultWithParams {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`id`")
    private Long id;

    @Column(name="`params`")
    //@OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Param> params;

    @Column(name="`result`")
    private List<String> result;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "rangeTable_id")
    //@JsonIgnore
    //private RangeTable rangeTable;

    public ResultWithParams(List<Param> params, List<String> result) {
        this.params = params;
        this.result = result;
    }

    public ResultWithParams() {}
}
