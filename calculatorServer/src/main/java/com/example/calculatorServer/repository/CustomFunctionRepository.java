package com.example.calculatorServer.repository;

import com.example.calculatorServer.domain.customFunc.CustomFunction;
import com.example.calculatorServer.domain.funcvar.FuncVar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomFunctionRepository extends JpaRepository<CustomFunction, Long> {
    List<CustomFunction> findByName(String name);
}
