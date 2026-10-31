package com.railconnect.entity;

import com.railconnect.common.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
