package com.example.calculatorServer.exceptions;

/**
 * Исключение вызываеное при неврерном ссылании на Диапазонную Таблицу
 */
public class TableReferenceErrorException extends RuntimeException {
    public TableReferenceErrorException() {
        super("Table Column Reference Error");
    }
}
