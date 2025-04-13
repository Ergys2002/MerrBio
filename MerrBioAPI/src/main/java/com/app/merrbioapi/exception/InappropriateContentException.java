package com.app.merrbioapi.exception;

/**
 * Exception thrown when inappropriate content is detected in an image
 */
public class InappropriateContentException extends RuntimeException {
    
    public InappropriateContentException(String message) {
        super(message);
    }
}