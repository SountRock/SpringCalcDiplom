package com.example.calculatorService.repository;

import com.example.calculatorService.domain.funcvar.FuncVar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncVarRepository extends JpaRepository<FuncVar, Long> {
    /**
     * Такой метод удаления был выбраз в связи с возниковвением частой ошибки "row was updated or deleted by another transaction"
     * @param id
     */
    @Modifying
    @Query("DELETE FROM FuncVar f WHERE f.id=:id")
    void deleteFuncVar(Long id);

    List<FuncVar> findByName(String name);
}
