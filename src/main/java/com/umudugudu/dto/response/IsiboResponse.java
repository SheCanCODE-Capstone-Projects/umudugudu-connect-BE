package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class IsiboResponse {
    private UUID id;
    private String name;
    private String villageName;
    private UUID villageId;
    private LeaderInfo leader;
    private List<MemberInfo> members;
    private int memberCount;

    @Data
    @Builder
    public static class LeaderInfo {
        private UUID id;
        private String fullName;
        private String email;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class MemberInfo {
        private UUID id;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String role;
    }
}