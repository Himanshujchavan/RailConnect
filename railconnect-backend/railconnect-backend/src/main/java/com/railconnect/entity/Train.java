package com.railconnect.entity;

import com.railconnect.common.enums.TrainType;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "trains")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String number;
    public String name;

    @Enumerated(EnumType.STRING)
    public TrainType type;

    @OneToMany(mappedBy = "train")
    public List<Coach> coaches;

    public Train() {}
}
