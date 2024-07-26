package ru.spring.sensor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.spring.sensor.pojos.Sensor;
import ru.spring.sensor.services.SensorsService;

@SuppressWarnings("NullableProblems")
@Component
public class UniqueSensorValidator implements Validator {
    private final SensorsService sensorsService;

    @Autowired
    public UniqueSensorValidator(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Sensor.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Sensor sensor = (Sensor) object;
        if (sensorsService.findOneByName(sensor.getName()).isPresent()) {
            errors.rejectValue("name", "", "Такой сенсор уже зарегистрирован!");
        }
    }
}