package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class IsiboResponse {
    private Long id;
    private String name;
    private String villageName;
    private Long villageId;
    private LeaderInfo leader;
    private List<MemberInfo> members;
    private int memberCount;

    @Data
    @Builder
    public static class LeaderInfo {
        private Long id;
        private String fullName;
        private String email;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class MemberInfo {
        private Long id;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String role;
    }
}