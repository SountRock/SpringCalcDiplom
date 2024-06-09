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

    @Column(name="`head`")
    private String head;

    @Column(name="`result`")
    private List<String> result;

    @ManyToOne
    @JoinColumn(name = "rangeTable_id")
    @JsonIgnore
    private RangeTable rangeTable;

    public ResultWithParams(String head, List<String> result) {
        this.head = head;
        this.result = result;
    }

    public ResultWithParams() {}
}
