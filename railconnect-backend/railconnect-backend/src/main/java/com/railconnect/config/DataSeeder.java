package com.railconnect.config;

import com.railconnect.auth.repository.RoleRepository;
import com.railconnect.common.enums.RoleType;
import com.railconnect.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Ensures every RoleType has a corresponding row in the roles table on startup.
 * Registration and CustomUserDetailsService both depend on these rows existing —
 * without this seeder, the very first registration attempt against a fresh
 * database fails.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByName(roleType).orElseGet(() -> {
                Role role = new Role();
                role.name = roleType;
                logger.info("Seeding missing role: {}", roleType);
                return roleRepository.save(role);
            });
        }
    }
}
