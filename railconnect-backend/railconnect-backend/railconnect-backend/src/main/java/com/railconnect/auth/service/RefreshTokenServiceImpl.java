package com.railconnect.auth.service;

import com.railconnect.auth.repository.RefreshTokenRepository;
import com.railconnect.common.constants.SecurityConstants;
import com.railconnect.common.exception.InvalidTokenException;
import com.railconnect.common.exception.TokenExpiredException;
import com.railconnect.entity.RefreshToken;
import com.railconnect.entity.User;
import com.railconnect.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.user = user;
        refreshToken.token = jwtService.generateRefreshToken(user.username);
        refreshToken.expiryDate = LocalDateTime.now()
                .plusSeconds(SecurityConstants.REFRESH_TOKEN_EXPIRATION_MS / 1000);
        refreshToken.revoked = false;
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not recognized"));

        if (refreshToken.revoked) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }
        if (refreshToken.expiryDate.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token has expired, please log in again");
        }
        if (!jwtService.validateToken(token) || jwtService.isExpired(token)) {
            throw new TokenExpiredException("Refresh token has expired, please log in again");
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.revoked = true;
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Override
    @Transactional
    public void revokeAllForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
