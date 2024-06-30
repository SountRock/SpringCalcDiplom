package com.example.calculatorServer.repository;

import com.example.calculatorServer.domain.funcvar.FuncVar;
import com.example.calculatorServer.domain.table.funcTable.FuncTable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncTableRepository extends JpaRepository<FuncTable, Long> {
    /**
     * Такой метод удаления был выбраз в связи с возниковвением частой ошибки "row was updated or deleted by another transaction"
     * @param id
     */
    @Modifying
    @Query("DELETE FROM FuncTable ft WHERE ft.id=:id")
    void deleteFuncTable(Long id);

    List<FuncTable> findByRecordName(String recordName);
}
