package com.umudugudu.repository;

import com.umudugudu.entity.ChangeRequestStatus;
import com.umudugudu.entity.ProfileChangeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProfileChangeRequestRepository extends JpaRepository<ProfileChangeRequest, UUID> {
    List<ProfileChangeRequest> findByUserId(UUID userId);
    Page<ProfileChangeRequest> findByStatusAndUser_VillageId(
            ChangeRequestStatus status, UUID villageId, Pageable pageable);
    boolean existsByUserIdAndStatus(UUID userId, ChangeRequestStatus status);
}
