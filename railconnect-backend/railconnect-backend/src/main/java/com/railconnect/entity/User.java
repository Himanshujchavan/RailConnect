package com.railconnect.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String username;
    public String email;
    public String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    public Role role;

    @OneToMany(mappedBy = "user")
    public List<Passenger> passengers;

    public User() {}
}
