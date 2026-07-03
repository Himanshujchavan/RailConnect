package com.railconnect.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, nullable = false, length = 512)
    public String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    public LocalDateTime expiryDate;

    public boolean revoked = false;

    public RefreshToken() {}
}
