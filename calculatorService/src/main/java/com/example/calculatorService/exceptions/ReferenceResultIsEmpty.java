package com.example.calculatorService.exceptions;

public class ReferenceResultIsEmpty extends RuntimeException{
    public ReferenceResultIsEmpty() {
        super("One of Reference Result is empty");
    }
}
