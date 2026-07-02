package com.railconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String code;
    public String name;
    public String city;

    public Station() {}
}
