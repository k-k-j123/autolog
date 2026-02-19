package com.Kaushik.Autolog.Service;

import com.Kaushik.Autolog.Entity.Users;
import com.Kaushik.Autolog.Entity.Vehicle;
import com.Kaushik.Autolog.Repository.VehicleRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepo vehicleRepo;
    private final UserService userService;

    public VehicleService(VehicleRepo vehicleRepo, UserService userService) {
        this.vehicleRepo = vehicleRepo;
        this.userService = userService;
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepo.findById(id).orElse(null);
    }

    public Vehicle createVehicle(Long userid,Vehicle vehicle) {
        Users user = userService.getUserById(userid);
        if (user != null) {
            vehicle.setUser(user);
            return vehicleRepo.save(vehicle);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<Vehicle> getAllVehiclesByUser(Long userId) {
        Users user = userService.getUserById(userId);
        if (user != null) {
            return vehicleRepo.findByUser(user);
        }
        return List.of();
    }

    public boolean deleteVehicle(Long vehicleId, Long userId) {
        Optional<Vehicle> optVehicle = vehicleRepo.findById(vehicleId);
        if (optVehicle.isPresent()) {
            Vehicle vehicle = optVehicle.get();
            if (vehicle.getUser().getId() == userId) {
                vehicleRepo.delete(vehicle);
                return true;
            }
        }
        return false;
    }

}
