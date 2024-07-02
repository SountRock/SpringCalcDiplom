package com.example.calculatorServer.exceptions;

/**
 * Исключение вызываеное при отсутсвии результата в при поптки ссылки на запись
 */
public class ReferenceResultIsEmpty extends RuntimeException{
    public ReferenceResultIsEmpty() {
        super("One of Reference Result is empty");
    }
}
