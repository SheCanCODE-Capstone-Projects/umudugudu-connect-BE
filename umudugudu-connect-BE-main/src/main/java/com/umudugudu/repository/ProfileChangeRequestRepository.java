package com.umudugudu.repository;

import com.umudugudu.entity.ChangeRequestStatus;
import com.umudugudu.entity.ProfileChangeRequest;
import com.umudugudu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfileChangeRequestRepository extends JpaRepository<ProfileChangeRequest, Long> {
    List<ProfileChangeRequest> findByUserOrderByCreatedAtDesc(User user);
    List<ProfileChangeRequest> findByStatusOrderByCreatedAtAsc(ChangeRequestStatus status);
    boolean existsByUserAndStatus(User user, ChangeRequestStatus status);
}