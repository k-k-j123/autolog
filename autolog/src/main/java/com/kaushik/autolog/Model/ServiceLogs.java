package com.kaushik.autolog.Model;

import jakarta.persistence.*;

@Entity
public class ServiceLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="vid",nullable = false)
    private Vehicles vehicles;
}
