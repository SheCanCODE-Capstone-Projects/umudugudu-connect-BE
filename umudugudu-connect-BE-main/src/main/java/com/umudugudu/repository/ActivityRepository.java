package com.umudugudu.repository;

import com.umudugudu.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByVillageId(UUID villageId);

    List<Activity> findByCreatedBy(UUID createdBy);
}