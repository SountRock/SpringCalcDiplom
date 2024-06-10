package com.example.calculatorService.repository;

import com.example.calculatorService.domain.customFunc.CustomFunction;
import com.example.calculatorService.domain.funcvar.FuncVar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomFunctionRepository extends JpaRepository<CustomFunction, Long> {
    /**
     * Такой метод удаления был выбраз в связи с возниковвением частой ошибки "row was updated or deleted by another transaction"
     * @param id
     */
    @Modifying
    @Query("DELETE FROM CustomFunction c WHERE c.id=:id")
    void deleteCustomFunction(Long id);

    List<CustomFunction> findByName(String name);
}
