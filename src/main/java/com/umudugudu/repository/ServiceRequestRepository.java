package com.umudugudu.repository;

import com.umudugudu.entity.ServiceRequest;
import com.umudugudu.entity.ServiceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {

    // Citizen: all my requests, newest first
    List<ServiceRequest> findByCitizenIdOrderByCreatedAtDesc(UUID citizenId);

    // Leader: all pending requests for a specific village
    List<ServiceRequest> findByVillageIdAndStatusOrderByCreatedAtAsc(UUID villageId,
                                                                      ServiceRequestStatus status);

    // Leader: all requests for a village (any status)
    List<ServiceRequest> findByVillageIdOrderByCreatedAtDesc(UUID villageId);

    // Leader: all pending requests for a specific isibo
    List<ServiceRequest> findByIsiboIdAndStatusOrderByCreatedAtAsc(UUID isiboId,
                                                                    ServiceRequestStatus status);

    // Admin: all requests with a given status
    List<ServiceRequest> findByStatusOrderByCreatedAtAsc(ServiceRequestStatus status);
}
