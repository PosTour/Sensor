package ru.spring.sensor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spring.sensor.pojos.Measurement;

@Repository
public interface MeasurementsRepository extends JpaRepository<Measurement, Integer> {}