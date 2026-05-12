package com.umudugudu.repository;

import com.umudugudu.entity.ProfileChangeRequest;
import com.umudugudu.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfileChangeRequestRepository extends JpaRepository<ProfileChangeRequest, UUID> {
    List<ProfileChangeRequest> findByRequesterId(UUID requesterId);
    List<ProfileChangeRequest> findByStatus(RequestStatus status);
    boolean existsByRequesterIdAndStatus(UUID requesterId, RequestStatus status);
}
