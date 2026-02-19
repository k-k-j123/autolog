package com.Kaushik.Autolog.Service;

import com.Kaushik.Autolog.Entity.FuelLogs;
import com.Kaushik.Autolog.Entity.Vehicle;
import com.Kaushik.Autolog.Repository.FuelRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuelLogService {

    private final VehicleService vehicleService;
    private final FuelRepo fuelRepo;


    public FuelLogService(VehicleService vehicleService, FuelRepo fuelRepo) {
        this.vehicleService = vehicleService;
        this.fuelRepo = fuelRepo;
    }



    public FuelLogs createFuelLog(Long vehicleId, FuelLogs fuelLog) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle != null){
            // calculate and set mileage before saving
            calculateAndSetMileage(vehicle, fuelLog);
            fuelLog.setVehicle(vehicle);
            return fuelRepo.save(fuelLog);
        }else{
            throw new RuntimeException("Vehicle not found");
        }
    }

    public List<FuelLogs> getFuelLogByVehicleId(Long vehicleId) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle != null) {
            return fuelRepo.findByVehicle(vehicle);
        }
        return List.of();
    }

    public boolean deleteFuelLogForVehicle(Long vehicleId,Long fuelId) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if(vehicle != null){
            Optional<FuelLogs> fuelLogOpt = fuelRepo.findById(fuelId);
            if(fuelLogOpt.isPresent() && fuelLogOpt.get().getVehicle().getId() == vehicleId){
                fuelRepo.delete(fuelLogOpt.get());
                return true;
            }
        }
        return false;
    }
    /**
     * Calculate mileage using the latest fuel log for the vehicle.
     * Logic: find latest fuel log ordered by odometer reading. kilometers_driven = newOdometer - lastOdometer
     * if fuelUsed > 0, mileage = kilometers_driven / fuelUsed; otherwise mileage = 0.
     */
    private void calculateAndSetMileage(Vehicle vehicle, FuelLogs newLog) {
        if (newLog == null || vehicle == null) return;

        Optional<FuelLogs> lastLogOpt = fuelRepo.findTopByVehicleOrderByOdometerReadingDesc(vehicle);
        long lastOdo = 0;
        if (lastLogOpt.isPresent()) {
            lastOdo = lastLogOpt.get().getOdometerReading();
        }

        long kilometersDriven = newLog.getOdometerReading() - lastOdo;
        if (kilometersDriven < 0) {
            // Prevent negative distance; treat as 0 to avoid negative mileage
            kilometersDriven = 0;
        }

        double fuelUsed = newLog.getFuelAmount();
        double mileage = 0.0;
        if (fuelUsed > 0) {
            mileage = (double) kilometersDriven / fuelUsed;
        }

        newLog.setMileage(mileage);
    }


}
