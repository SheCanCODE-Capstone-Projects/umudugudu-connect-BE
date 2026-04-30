package com.umudugudu.repository;

import com.umudugudu.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByExternalTxId(String externalTxId);
    Page<Payment> findByPayerVillageId(UUID villageId, Pageable pageable);
}
