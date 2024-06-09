package com.example.calculatorService.exceptions;

public class TableReferenceErrorException extends RuntimeException {
    public TableReferenceErrorException() {
        super("Table Column Reference Error");
    }
}
