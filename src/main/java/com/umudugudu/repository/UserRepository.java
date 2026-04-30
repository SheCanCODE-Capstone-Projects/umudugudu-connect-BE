package com.umudugudu.repository;

import com.umudugudu.model.User;
import com.umudugudu.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    Page<User> findByVillageIdAndRole(UUID villageId, UserRole role, Pageable pageable);
    Page<User> findByVillageId(UUID villageId, Pageable pageable);
    Page<User> findByRole(UserRole role, Pageable pageable);
}
