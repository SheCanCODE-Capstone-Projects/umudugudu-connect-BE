package com.umudugudu.repository;

import com.umudugudu.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findAllByOrderByActivityDateAsc(Pageable pageable);
}
