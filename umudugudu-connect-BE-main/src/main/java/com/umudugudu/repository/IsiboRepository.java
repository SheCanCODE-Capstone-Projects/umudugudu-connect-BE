package com.umudugudu.repository;

import com.umudugudu.entity.Isibo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IsiboRepository extends JpaRepository<Isibo, UUID> {
    List<Isibo> findByVillageId(UUID villageId);
}