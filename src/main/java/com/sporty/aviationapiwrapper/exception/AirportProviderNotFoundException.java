package com.sporty.aviationapiwrapper.exception;

public class AirportProviderNotFoundException extends IllegalArgumentException {
    public AirportProviderNotFoundException(String message) {
        super(message);
    }
}
