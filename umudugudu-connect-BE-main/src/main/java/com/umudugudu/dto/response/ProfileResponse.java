package com.umudugudu.dto.response;

import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import lombok.Getter;

@Getter
public class ProfileResponse {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final String village;
    private final String isibo;
    private final Role role;
    private final boolean verified;

    public ProfileResponse(User user) {
        this.id          = user.getId();
        this.firstName   = user.getFirstName();
        this.lastName    = user.getLastName();
        this.email       = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.village     = user.getVillage();
        this.isibo       = user.getIsibo();
        this.role        = user.getRole();
        this.verified    = user.isVerified();
    }
}