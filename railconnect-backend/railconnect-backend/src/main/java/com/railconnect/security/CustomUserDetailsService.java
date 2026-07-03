package com.railconnect.security;

import com.railconnect.entity.User;
import com.railconnect.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // 1. Fetch user from repository using alternative option flows
        // Note: If UserRepository returns com.railconnect.auth.entity.User, you may need to cast it or align your repository imports
        User user = (User) userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        // 2. Map your public role properties to SimpleGrantedAuthority list
        // Accessing fields directly via public reference (user.role.name) instead of a getter method
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.role.name.name())
        );

        // 3. Return Spring Security User Details wrapper accessing fields directly
        return new org.springframework.security.core.userdetails.User(
                user.username,  // Changed from user.getUsername() to direct public field access
                user.password,  // Changed from user.getPassword() to direct public field access
                authorities
        );
    }
}