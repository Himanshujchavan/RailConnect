package com.railconnect.entity;

import com.railconnect.common.enums.Gender;
import jakarta.persistence.*;

@Entity
@Table(name = "passengers")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String firstName;
    public String lastName;
    public Integer age;

    @Enumerated(EnumType.STRING)
    public Gender gender;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    public Passenger() {}
}
