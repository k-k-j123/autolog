package com.Kaushik.Autolog.Repository;

import com.Kaushik.Autolog.Entity.Users;
import com.Kaushik.Autolog.Entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser(Users user);
}
