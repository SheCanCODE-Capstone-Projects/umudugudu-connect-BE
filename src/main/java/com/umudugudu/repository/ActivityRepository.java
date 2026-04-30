package com.umudugudu.repository;

import com.umudugudu.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    Page<Activity> findByVillageId(UUID villageId, Pageable pageable);
}
