package com.example.projetOO.exeptions;

public class RessourceNotFoundExeption extends RuntimeException {
    public RessourceNotFoundExeption(String message) {
        super(message);
    }
}
