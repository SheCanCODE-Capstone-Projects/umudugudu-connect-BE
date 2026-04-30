package com.umudugudu.service;

import com.umudugudu.dto.AuthResponseDto;
import com.umudugudu.dto.UserProfileDto;
import com.umudugudu.exception.BusinessException;
import com.umudugudu.exception.ResourceNotFoundException;
import com.umudugudu.model.RefreshToken;
import com.umudugudu.model.User;
import com.umudugudu.model.UserRole;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates the full OTP authentication flow:
 *   1. sendOtp      — generate + deliver OTP
 *   2. verifyOtp    — verify code → issue JWT pair
 *   3. refresh      — exchange refresh token → new access token
 *   4. getProfile   — return authenticated user's profile
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService          otpService;
    private final UserRepository      userRepository;
    private final JwtUtils            jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder     passwordEncoder;

    // ── Step 1: Request OTP ───────────────────────────────────────────────────

    /**
     * Registers a new user with email and password.
     */
    @Transactional
    public AuthResponseDto register(String fullName, String email,
                                    String phoneNumber, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered");
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BusinessException("Phone number already registered");
        }

        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(rawPassword))
                .role(UserRole.CITIZEN)
                .active(true)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    /**
     * Logs in with email and password.
     */
    @Transactional
    public AuthResponseDto login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new BusinessException("Account is deactivated");
        }

        return buildAuthResponse(user);
    }

    /**
     * Sends a 6-digit OTP to the given phone number.
     */
    public void requestOtp(String phoneNumber) {
        // Verify the user exists before sending an OTP
        if (!userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ResourceNotFoundException("User", phoneNumber);
        }
        otpService.sendOtp(phoneNumber);
    }

    // ── Step 2: Verify OTP → issue tokens ────────────────────────────────────

    /**
     * Verifies the OTP, then issues an access + refresh token pair.
     *
     * @throws OtpExpiredException          if the OTP has expired
     * @throws OtpInvalidException          if the code is wrong
     * @throws MaxAttemptsExceededException if too many wrong attempts
     */
    @Transactional
    public AuthResponseDto verifyOtp(String phoneNumber, String code) {
        otpService.verifyOtp(phoneNumber, code);

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User", phoneNumber));

        return buildAuthResponse(user);
    }

    // ── Step 3: Refresh access token ─────────────────────────────────────────

    /**
     * Validates the refresh token and issues a new access token.
     * The refresh token itself is NOT rotated here (single-use rotation
     * can be added later if required).
     *
     * @throws InvalidRefreshTokenException if the token is invalid or expired
     */
    @Transactional
    public AuthResponseDto refresh(String refreshTokenString) {
        User user = refreshTokenService.validate(refreshTokenString);
        // Issue a fresh access token; keep the same refresh token
        String newAccessToken = jwtUtils.generateAccessToken(user);
        return new AuthResponseDto(
                newAccessToken,
                refreshTokenString,
                toProfile(user)
        );
    }

    // ── Step 4: Get profile ───────────────────────────────────────────────────

    public UserProfileDto getProfile(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User", phoneNumber));
        return UserProfileDto.from(user);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AuthResponseDto buildAuthResponse(User user) {
        String accessToken  = jwtUtils.generateAccessToken(user);
        RefreshToken refresh = refreshTokenService.create(user);

        return new AuthResponseDto(
                accessToken,
                refresh.getToken(),
                toProfile(user)
        );
    }

    private AuthResponseDto.UserProfile toProfile(User user) {
        return new AuthResponseDto.UserProfile(
                user.getId(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getVillage() != null ? user.getVillage().getId() : null,
                user.getIsib()    != null ? user.getIsib().getId()    : null
        );
    }
}
