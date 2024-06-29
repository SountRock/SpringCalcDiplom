package com.example.calculatorServer.exceptions;

public class TableReferenceErrorException extends RuntimeException {
    public TableReferenceErrorException() {
        super("Table Column Reference Error");
    }
}
