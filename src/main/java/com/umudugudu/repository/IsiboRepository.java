package com.umudugudu.repository;

import com.umudugudu.entity.Isibo;
import com.umudugudu.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IsiboRepository extends JpaRepository<Isibo, Long> {
    List<Isibo> findByVillage(Village village);
    boolean existsByNameAndVillage(String name, Village village);
    Optional<Isibo> findByIdAndVillage(Long id, Village village);
}