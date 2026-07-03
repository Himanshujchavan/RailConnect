package com.railconnect.auth.service;

import com.railconnect.auth.dtorequestresponse.*;

public interface AuthenticationService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse authenticate(LoginRequest request);

    JwtResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
