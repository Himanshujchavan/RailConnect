package com.railconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 🔥 Crucial for automatic timestamps
public class RailConnectApplication {
    public static void main(String[] args) {
        SpringApplication.run(RailConnectApplication.class, args);
    }
}
