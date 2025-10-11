package com.kaushik.autolog.Model;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Vehicles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    private LocalDateTime purchase_date;
    private double tankSize;

    @OneToMany(mappedBy = "vehicles", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FuelLogs> fuelLogs;

    @OneToMany(mappedBy = "vehicles", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceLogs> serviceLogs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public LocalDateTime getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(LocalDateTime purchase_date) {
        this.purchase_date = purchase_date;
    }

    public double getTankSize() {
        return tankSize;
    }

    public void setTankSize(double tankSize) {
        this.tankSize = tankSize;
    }
}
