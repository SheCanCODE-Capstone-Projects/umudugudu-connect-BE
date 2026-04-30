package com.umudugudu.repository;

import com.umudugudu.model.Isib;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IsibRepository extends JpaRepository<Isib, UUID> {
    List<Isib> findByVillageId(UUID villageId);
}
