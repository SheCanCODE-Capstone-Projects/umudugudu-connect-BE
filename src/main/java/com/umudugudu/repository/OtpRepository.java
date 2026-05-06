package com.umudugudu.repository;

import com.umudugudu.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByPhoneNumberOrderByIdDesc(String phoneNumber);
    Optional<Otp> findTopByEmailOrderByIdDesc(String email);

    @Transactional
    void deleteByPhoneNumber(String phoneNumber);

    @Transactional
    void deleteByEmail(String email);
}