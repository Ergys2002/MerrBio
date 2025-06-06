package com.app.merrbioapi.exception;

public class InvalidUserRole extends RuntimeException {
    public InvalidUserRole(String role, String expectedRole) {
        super("Invalid user role: " + role + ". Expected role: " + expectedRole);
    }
}
