package ru.spring.sensor.exceptions;

public class MeasurementNotCreatedException extends RuntimeException {
    public MeasurementNotCreatedException(String message) {
        super(message);
    }
}