package com.umudugudu.repository;
import com.umudugudu.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VillageRepository extends JpaRepository<Village, UUID> {
    Optional<Village> findByVillageLeaderId(UUID villageLeaderId);
    Optional<Village> findByName(String name);

    boolean existsByName(String name);
}