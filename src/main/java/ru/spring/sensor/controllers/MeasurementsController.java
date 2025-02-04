package ru.spring.sensor.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.spring.sensor.dto.MeasurementDTO;
import ru.spring.sensor.exceptions.ErrorResponse;
import ru.spring.sensor.exceptions.MeasurementNotCreatedException;
import ru.spring.sensor.exceptions.SensorNotFoundException;
import ru.spring.sensor.pojos.Measurement;
import ru.spring.sensor.services.MeasurementsService;
import ru.spring.sensor.services.SensorsService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/measurements")
public class MeasurementsController {
    private final MeasurementsService measurementsService;
    private final SensorsService sensorsService;
    private final ModelMapper modelMapper;

    @Autowired
    public MeasurementsController(MeasurementsService measurementsService, SensorsService sensorsService, ModelMapper modelMapper) {
        this.measurementsService = measurementsService;
        this.sensorsService = sensorsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<MeasurementDTO> getMeasurements() {
        return measurementsService.findAll()
                .stream()
                .map(this::convertToMeasurementDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/rainyDaysCount")
    public Long showRainyDays() {
        return measurementsService.countRainyDays();
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addMeasurement(@RequestBody @Valid MeasurementDTO measurementDTO,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new MeasurementNotCreatedException(errorMessage.toString());
        }
        Measurement measurement = convertToMeasurement(measurementDTO);
        measurement.setSensor(sensorsService.findOneByNameOrElseThrowException(measurement.getSensor().getName()));
        measurementsService.save(measurement);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(MeasurementNotCreatedException exception) {
        ErrorResponse response = new ErrorResponse(
                exception.getMessage(), System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SensorNotFoundException.class)
    private ResponseEntity<ErrorResponse> handleException() {
        ErrorResponse response = new ErrorResponse(
                "Такой сенсор не зарегистрирован!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        return modelMapper.map(measurementDTO, Measurement.class);
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }
}