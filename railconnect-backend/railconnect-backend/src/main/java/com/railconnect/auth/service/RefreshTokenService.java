package com.railconnect.auth.service;

import com.railconnect.entity.RefreshToken;
import com.railconnect.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    /** Returns the token if it's present, not revoked and not expired; throws otherwise. */
    RefreshToken verifyToken(String token);

    void revoke(String token);

    void revokeAllForUser(User user);
}
