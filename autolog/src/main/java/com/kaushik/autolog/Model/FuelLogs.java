package com.kaushik.autolog.Model;

import jakarta.persistence.*;

@Entity
public class FuelLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="vid",nullable = false)
    private Vehicles vehicles;


}
