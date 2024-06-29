package com.example.calculatorServer.exceptions;

public class ReferenceResultIsEmpty extends RuntimeException{
    public ReferenceResultIsEmpty() {
        super("One of Reference Result is empty");
    }
}
