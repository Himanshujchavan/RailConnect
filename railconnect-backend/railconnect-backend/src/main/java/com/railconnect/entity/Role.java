package com.railconnect.entity;

import com.railconnect.common.enums.RoleType;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    public RoleType name;

    public Role() {}
}
