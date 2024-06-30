package com.example.calculatorServer.repository;

import com.example.calculatorServer.domain.table.funcTable.FuncTable;
import com.example.calculatorServer.domain.table.funcTable.FuncTableCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncTableCellRepository extends JpaRepository<FuncTableCell, Long> {
    /**
     * Такой метод удаления был выбраз в связи с возниковвением частой ошибки "row was updated or deleted by another transaction"
     * @param id
     */
    @Modifying
    @Query("DELETE FROM FuncTableCell ftc WHERE ftc.id=:id")
    void deleteFuncTableCell(Long id);

    List<FuncTableCell> findByCellName(String cellName);
}
