package com.umudugudu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsResponse {
    private boolean success;
    private String message;
    private List<String> pendingActivities;
    private List<String> penalties;
    private String fullResponse;
}
