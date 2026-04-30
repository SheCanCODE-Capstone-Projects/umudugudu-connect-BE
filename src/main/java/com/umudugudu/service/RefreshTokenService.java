package com.umudugudu.service;

import com.umudugudu.exception.InvalidRefreshTokenException;
import com.umudugudu.model.RefreshToken;
import com.umudugudu.model.User;
import com.umudugudu.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiry-ms:604800000}")
    private long refreshExpiryMs;

    /** Creates and persists a new refresh token for the given user. */
    @Transactional
    public RefreshToken create(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(OffsetDateTime.now().plusNanos(refreshExpiryMs * 1_000_000L))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(token);
    }

    /**
     * Validates the token string and returns the associated user.
     *
     * @throws InvalidRefreshTokenException if not found, revoked, or expired
     */
    @Transactional(readOnly = true)
    public User validate(String tokenString) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenString)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (token.isRevoked() || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new InvalidRefreshTokenException();
        }
        return token.getUser();
    }

    /** Revokes all refresh tokens for a user (e.g. on logout). */
    @Transactional
    public void revokeAll(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
