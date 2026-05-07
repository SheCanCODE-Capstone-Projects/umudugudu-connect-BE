package com.umudugudu.service;

import com.umudugudu.entity.Role;

public interface AdminService {
    String updateRoleByEmail(String email, Role newRole);
}
