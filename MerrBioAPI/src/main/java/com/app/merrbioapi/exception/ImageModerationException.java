package com.app.merrbioapi.exception;

/**
 * Exception thrown when image moderation fails
 */
public class ImageModerationException extends RuntimeException {
    
    public ImageModerationException(String message) {
        super(message);
    }
    
    public ImageModerationException(String message, Throwable cause) {
        super(message, cause);
    }
}