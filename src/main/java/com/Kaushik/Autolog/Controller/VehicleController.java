package com.Kaushik.Autolog.Controller;

import com.Kaushik.Autolog.Entity.Vehicle;
import com.Kaushik.Autolog.Service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/user/{userid}")
    public ResponseEntity<List<Vehicle>> getVehiclesforUser(@PathVariable Long userid) {
        List<Vehicle> vehicles = vehicleService.getAllVehiclesByUser(userid);
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping("/user/{userid}")
    public ResponseEntity<Vehicle> createVehicleForUser(@PathVariable Long userid, @RequestBody Vehicle vehicle) {
        Vehicle createdVehicle = vehicleService.createVehicle(userid, vehicle);
        return ResponseEntity.ok(createdVehicle);
    }

    @DeleteMapping("/user/{userId}/vehicle/{vehicleId}")
    public ResponseEntity<String> deleteVehicleForUser(@PathVariable Long userId, @PathVariable Long vehicleId) {
        boolean isDeleted = vehicleService.deleteVehicle(vehicleId, userId);
        if (isDeleted) {
            return ResponseEntity.ok("Vehicle deleted successfully");
        } else {
            return ResponseEntity.status(403).body("Unauthorized to delete this vehicle");
        }
    }
}
