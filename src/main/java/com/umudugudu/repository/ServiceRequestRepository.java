package com.umudugudu.repository;

import com.umudugudu.model.ServiceRequest;
import com.umudugudu.model.ServiceRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {
    List<ServiceRequest> findByCitizenId(UUID citizenId);
    Page<ServiceRequest> findByStatus(ServiceRequestStatus status, Pageable pageable);
    Page<ServiceRequest> findByReviewedById(UUID reviewerId, Pageable pageable);
}
