package com.umudugudu.dto.request;

import lombok.Data;

@Data
public class ProfileChangeRequestDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String village;
    private String isibo;
}