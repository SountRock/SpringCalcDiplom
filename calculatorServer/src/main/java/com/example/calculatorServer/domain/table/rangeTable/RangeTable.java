package com.example.calculatorServer.domain.table.rangeTable;

import com.example.calculatorServer.domain.table.funcTable.FuncTableCell;
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
    @Lob
    private String expression;

    @Column(name="`ranges`")
    //@OneToMany(mappedBy = "rangeTable", cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Range> ranges;
    @Column(name="`rangesFormula`")
    @Lob
    private String rangesFormula;

    @Column(name="`results`")
    //@OneToMany(mappedBy = "rangeTable", cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ResultWithParams> results;
    ////////////////////////////////////////////////////////////////////

    public RangeTable(@Nonnull String expression) {
        this.expression = expression;
    }

    public RangeTable() {}

    public void removeRanges() {
        while (!ranges.isEmpty()){
            Range temp = ranges.get(0);
            temp.setRangeTable(null);
            ranges.remove(0);
        }
    }

    public void removeResults() {
        while (!results.isEmpty()){
            ResultWithParams temp = results.get(0);
            temp.setRangeTable(null);
            results.remove(0);
        }
    }
}
