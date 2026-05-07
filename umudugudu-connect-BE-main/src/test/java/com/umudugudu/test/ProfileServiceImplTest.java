package com.umudugudu.test;

import com.umudugudu.dto.request.ProfileChangeRequestDTO;
import com.umudugudu.dto.request.ReviewChangeRequestDTO;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
import com.umudugudu.entity.*;
import com.umudugudu.exception.BusinessException;
import com.umudugudu.repository.ProfileChangeRequestRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.impl.EmailService;
import com.umudugudu.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ProfileChangeRequestRepository changeRequestRepository;
    @Mock private EmailService emailService;
    @InjectMocks private ProfileServiceImpl profileService;

    private User citizen;
    private User villageLeader;

    @BeforeEach
    void setUp() {
        citizen = new User();
        citizen.setId(1L); citizen.setFirstName("Jean"); citizen.setLastName("Uwimana");
        citizen.setEmail("jean@example.com"); citizen.setPhoneNumber("0788000001");
        citizen.setVillage("Kacyiru"); citizen.setIsibo("Isibo A");
        citizen.setRole(Role.CITIZEN); citizen.setEnabled(true); citizen.setVerified(true);

        villageLeader = new User();
        villageLeader.setId(2L); villageLeader.setFirstName("Marie");
        villageLeader.setEmail("marie@example.com");
        villageLeader.setRole(Role.VILLAGE_LEADER); villageLeader.setEnabled(true);
    }

    @Test
    @DisplayName("GET /me — returns all profile fields")
    void getMyProfile_returnsAllFields() {
        when(userRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(citizen));
        ProfileResponse p = profileService.getMyProfile("jean@example.com");
        assertThat(p.getFirstName()).isEqualTo("Jean");
        assertThat(p.getVillage()).isEqualTo("Kacyiru");
        assertThat(p.getIsibo()).isEqualTo("Isibo A");
        assertThat(p.getRole()).isEqualTo(Role.CITIZEN);
    }

    @Test
    @DisplayName("Submit change request — saved as PENDING and village leader emailed")
    void submitChangeRequest_notifiesLeader() {
        when(userRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(citizen));
        when(changeRequestRepository.existsByUserAndStatus(citizen, ChangeRequestStatus.PENDING)).thenReturn(false);
        when(userRepository.findAll()).thenReturn(List.of(villageLeader));

        ProfileChangeRequest saved = new ProfileChangeRequest();
        saved.setId(10L); saved.setUser(citizen);
        saved.setStatus(ChangeRequestStatus.PENDING); saved.setRequestedVillage("Kimisagara");
        when(changeRequestRepository.save(any())).thenReturn(saved);

        ProfileChangeRequestDTO dto = new ProfileChangeRequestDTO();
        dto.setVillage("Kimisagara");

        ChangeRequestResponse res = profileService.submitChangeRequest("jean@example.com", dto);
        assertThat(res.getStatus()).isEqualTo(ChangeRequestStatus.PENDING);
        verify(emailService).sendGenericEmail(eq("marie@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Duplicate pending request — throws BusinessException")
    void submitChangeRequest_duplicatePending_throws() {
        when(userRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(citizen));
        when(changeRequestRepository.existsByUserAndStatus(citizen, ChangeRequestStatus.PENDING)).thenReturn(true);
        ProfileChangeRequestDTO dto = new ProfileChangeRequestDTO();
        dto.setVillage("X");
        assertThatThrownBy(() -> profileService.submitChangeRequest("jean@example.com", dto))
                .isInstanceOf(BusinessException.class).hasMessageContaining("pending");
    }

    @Test
    @DisplayName("Empty change request — throws BusinessException")
    void submitChangeRequest_allNull_throws() {
        when(userRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(citizen));
        when(changeRequestRepository.existsByUserAndStatus(citizen, ChangeRequestStatus.PENDING)).thenReturn(false);
        assertThatThrownBy(() -> profileService.submitChangeRequest("jean@example.com", new ProfileChangeRequestDTO()))
                .isInstanceOf(BusinessException.class).hasMessageContaining("At least one");
    }

    @Test
    @DisplayName("Approve change request — profile updated, user notified")
    void reviewChangeRequest_approve() {
        ProfileChangeRequest req = new ProfileChangeRequest();
        req.setId(10L); req.setUser(citizen);
        req.setStatus(ChangeRequestStatus.PENDING); req.setRequestedVillage("Kimisagara");

        when(userRepository.findByEmail("marie@example.com")).thenReturn(Optional.of(villageLeader));
        when(changeRequestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(changeRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ReviewChangeRequestDTO dto = new ReviewChangeRequestDTO();
        dto.setApproved(true);

        ChangeRequestResponse res = profileService.reviewChangeRequest("marie@example.com", 10L, dto);
        assertThat(res.getStatus()).isEqualTo(ChangeRequestStatus.APPROVED);
        assertThat(citizen.getVillage()).isEqualTo("Kimisagara");
        verify(emailService).sendGenericEmail(eq("jean@example.com"), contains("Approved"), anyString());
    }

    @Test
    @DisplayName("Reject without reason — throws BusinessException")
    void reviewChangeRequest_rejectNoReason_throws() {
        ProfileChangeRequest req = new ProfileChangeRequest();
        req.setId(11L); req.setUser(citizen); req.setStatus(ChangeRequestStatus.PENDING);

        when(userRepository.findByEmail("marie@example.com")).thenReturn(Optional.of(villageLeader));
        when(changeRequestRepository.findById(11L)).thenReturn(Optional.of(req));

        ReviewChangeRequestDTO dto = new ReviewChangeRequestDTO();
        dto.setApproved(false); dto.setRejectionReason("");
        assertThatThrownBy(() -> profileService.reviewChangeRequest("marie@example.com", 11L, dto))
                .isInstanceOf(BusinessException.class).hasMessageContaining("rejection reason");
    }

    @Test
    @DisplayName("Non-leader trying to review — throws BusinessException")
    void reviewChangeRequest_notLeader_throws() {
        when(userRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(citizen));
        assertThatThrownBy(() -> profileService.reviewChangeRequest("jean@example.com", 1L, new ReviewChangeRequestDTO()))
                .isInstanceOf(BusinessException.class).hasMessageContaining("Village Leader");
    }
}