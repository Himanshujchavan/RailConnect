package com.railconnect.auth.service;

import com.railconnect.auth.dtorequestresponse.*;
import com.railconnect.auth.mapper.AuthMapper;
import com.railconnect.auth.repository.RoleRepository;
import com.railconnect.auth.repository.UserRepository;
import com.railconnect.common.constants.SecurityConstants;
import com.railconnect.common.enums.RoleType;
import com.railconnect.common.exception.*;
import com.railconnect.entity.RefreshToken;
import com.railconnect.entity.Role;
import com.railconnect.entity.User;
import com.railconnect.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationServiceImpl(UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      AuthMapper authMapper,
                                      PasswordEncoder passwordEncoder,
                                      AuthenticationManager authenticationManager,
                                      JwtService jwtService,
                                      RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new IllegalStateException(
                        "Default USER role is not seeded — check DataSeeder ran on startup"));

        User user = authMapper.toUser(request);
        user.username = request.email();
        user.password = passwordEncoder.encode(request.password());
        user.role = userRole;

        User saved = userRepository.save(user);
        return authMapper.toRegisterResponse(saved);
    }

    @Override
    @Transactional
    public LoginResponse authenticate(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException | DisabledException ex) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        String accessToken = jwtService.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken.token,
                SecurityConstants.JWT_EXPIRATION_MS / 1000,
                authMapper.toUserSummary(user)
        );
    }

    @Override
    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenService.verifyToken(request.refreshToken());
        User user = storedToken.user;

        // Rotate: issue a fresh pair and revoke the one that was just used.
        refreshTokenService.revoke(storedToken.token);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        String newAccessToken = jwtService.generateToken(user.username);

        return new JwtResponse(newAccessToken, newRefreshToken.token, SecurityConstants.JWT_EXPIRATION_MS / 1000);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Deliberately does NOT throw when the email isn't found: the controller always
        // returns the same generic message either way, so callers can't enumerate which
        // emails are registered by watching for a 404 vs a 200 here.
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            user.resetPasswordToken = resetToken;
            user.resetPasswordTokenExpiry = LocalDateTime.now()
                    .plusSeconds(SecurityConstants.PASSWORD_RESET_EXPIRATION_MS / 1000);
            userRepository.save(user);

            // TODO: wire up to the notification module once it exists, to actually email
            // `resetToken` to the user instead of relying on it being visible only in logs.
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or unknown password reset token"));

        if (user.resetPasswordTokenExpiry == null || user.resetPasswordTokenExpiry.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Password reset token has expired, please request a new one");
        }

        user.password = passwordEncoder.encode(request.getNewPassword());
        user.resetPasswordToken = null;
        user.resetPasswordTokenExpiry = null;
        userRepository.save(user);

        // Force re-login everywhere after a password reset.
        refreshTokenService.revokeAllForUser(user);
    }
}
