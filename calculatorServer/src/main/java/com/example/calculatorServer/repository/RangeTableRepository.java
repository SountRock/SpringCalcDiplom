package com.example.calculatorServer.repository;

import com.example.calculatorServer.domain.table.rangeTable.RangeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RangeTableRepository extends JpaRepository<RangeTable, Long> {
    /**
     * Такой метод удаления был выбраз в связи с возниковвением частой ошибки "row was updated or deleted by another transaction"
     * @param id
     */
    @Modifying
    @Query("DELETE FROM RangeTable rt WHERE rt.id=:id")
    void deleteTable(Long id);

    List<RangeTable> findByName(String name);
}

