package com.umudugudu.repository;

import com.umudugudu.entity.Isibo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IsiboRepository extends JpaRepository<Isibo, Long> {
    List<Isibo> findByVillageId(Long villageId);
}