package com.railconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String type;
    public String message;
    public LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    public Notification() {}
}
