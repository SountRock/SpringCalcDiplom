package com.example.calculatorService.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class TableExistException extends DataIntegrityViolationException {
    public TableExistException() {
        super("This name already exists");
    }
}
