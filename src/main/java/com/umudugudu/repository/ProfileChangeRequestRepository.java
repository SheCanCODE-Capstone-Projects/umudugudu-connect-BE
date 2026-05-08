package com.umudugudu.repository;

import com.umudugudu.entity.ProfileChangeRequest;
import com.umudugudu.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileChangeRequestRepository extends JpaRepository<ProfileChangeRequest, Long> {
    List<ProfileChangeRequest> findByRequesterId(Long requesterId);
    List<ProfileChangeRequest> findByStatus(RequestStatus status);
    boolean existsByRequesterIdAndStatus(Long requesterId, RequestStatus status);
}
