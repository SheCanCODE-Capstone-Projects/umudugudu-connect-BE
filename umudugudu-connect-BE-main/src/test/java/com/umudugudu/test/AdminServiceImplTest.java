package com.umudugudu.test;

import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void shouldUpdateRoleSuccessfully() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setRole(Role.ISIBO_LEADER);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        String result = adminService.updateRoleByEmail(
                "test@gmail.com",
                Role.VILLAGE_LEADER
        );

        assertEquals("Role updated to VILLAGE_LEADER for user test@gmail.com", result);
        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnUserNotFound() {
        when(userRepository.findByEmail("missing@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminService.updateRoleByEmail("missing@gmail.com", Role.ISIBO_LEADER)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldReturnSameRoleMessage() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setRole(Role.ISIBO_LEADER);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        String result = adminService.updateRoleByEmail(
                "test@gmail.com",
                Role.ISIBO_LEADER
        );

        assertEquals("User already has role: ISIBO_LEADER", result);
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldValidateNullEmail() {
        String result = adminService.updateRoleByEmail(null, Role.ISIBO_LEADER);
        assertEquals("Email is required", result);
    }

    @Test
    void shouldValidateNullRole() {
        String result = adminService.updateRoleByEmail("test@gmail.com", null);
        assertEquals("Role is required", result);
    }
}