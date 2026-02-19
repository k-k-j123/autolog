package com.Kaushik.Autolog.Controller;

import com.Kaushik.Autolog.Entity.FuelLogs;
import com.Kaushik.Autolog.Service.FuelLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fuelLogs")
public class FuelLogController {

    private final FuelLogService fuelLogService;

    public FuelLogController(FuelLogService fuelLogService) {
        this.fuelLogService = fuelLogService;
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<FuelLogs>> getFuelLogsByVehicleId(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                fuelLogService.getFuelLogByVehicleId(vehicleId)
        );
    }


    @PostMapping("/vehicle/{vehicleId}")
    public ResponseEntity<FuelLogs> createFuelLog(@PathVariable Long vehicleId, @RequestBody FuelLogs fuelLog) {
        try {
            FuelLogs createdLog = fuelLogService.createFuelLog(vehicleId, fuelLog);
            return ResponseEntity.ok(createdLog);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/vehicle/{vehicleId}/fuel/{fuelId}")
    public ResponseEntity<Void> deleteFuelLog(@PathVariable Long vehicleId, @PathVariable Long fuelId) {
        boolean deleted = fuelLogService.deleteFuelLogForVehicle(vehicleId, fuelId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
