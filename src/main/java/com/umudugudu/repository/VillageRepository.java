package com.umudugudu.repository;

import com.umudugudu.model.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VillageRepository extends JpaRepository<Village, UUID> {
}
