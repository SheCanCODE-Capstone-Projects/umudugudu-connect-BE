package com.umudugudu.repository;

import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByVillageAndIsiboIsNull(Village village);
    List<User> findByIdInAndVillage(List<Long> ids, Village village);
    Optional<User> findByIdAndVillageAndRole(Long id, Village village, Role role);
}