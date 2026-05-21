package com.umudugudu.repository;

import com.umudugudu.entity.Isibo;
import com.umudugudu.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IsiboRepository extends JpaRepository<Isibo, UUID> {
    List<Isibo> findByVillage(Village village);
    boolean existsByNameAndVillage(String name, Village village);
    Optional<Isibo> findByIdAndVillage(UUID id, Village village);
}