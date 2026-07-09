package com.railconnect.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /** Mirrors email at registration time; kept so security lookups (findByUsername) keep working. */
    public String username;

    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String password;

    // -- Password reset support --
    public String resetPasswordToken;
    public LocalDateTime resetPasswordTokenExpiry;

    @ManyToOne
    @JoinColumn(name = "role_id")
    public Role role;

    @OneToMany(mappedBy = "user")
    public List<Passenger> passengers;

        public String fullName() {
        if (firstName == null && lastName == null) {
            return username;
        }
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
