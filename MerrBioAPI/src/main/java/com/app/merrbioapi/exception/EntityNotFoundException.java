package com.app.merrbioapi.exception;

public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityName, Object identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier));
    }
}