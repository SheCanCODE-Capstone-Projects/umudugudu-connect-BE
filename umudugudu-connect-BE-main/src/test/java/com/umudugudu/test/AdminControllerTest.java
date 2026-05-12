package com.umudugudu.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umudugudu.controller.AdminController;
import com.umudugudu.dto.request.UpdateRoleRequest;
import com.umudugudu.entity.Role;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.security.JwtUtils;
import com.umudugudu.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AdminService adminService;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @WithMockUser(roles = "ADMIN")
    @Test
    void shouldUpdateRoleViaController() throws Exception {

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setEmail("test@gmail.com");
        request.setRole(Role.VILLAGE_LEADER);

        when(adminService.updateRoleByEmail(
                "test@gmail.com",
                Role.VILLAGE_LEADER
        )).thenReturn("Role updated successfully");

        mockMvc.perform(put("/api/v1/admin/users/role-by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Role updated successfully"));
    }
}