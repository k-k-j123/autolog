package com.Kaushik.Autolog.Repository;

import com.Kaushik.Autolog.Entity.FuelLogs;
import com.Kaushik.Autolog.Entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelRepo extends JpaRepository<FuelLogs, Long> {
    // Return the latest fuel log for a vehicle ordered by odometer reading (highest first)
    Optional<FuelLogs> findTopByVehicleOrderByOdometerReadingDesc(Vehicle vehicle);

    // kept for backward compatibility (if used elsewhere)
    List<FuelLogs> findByVehicle(Vehicle vehicle);
}
