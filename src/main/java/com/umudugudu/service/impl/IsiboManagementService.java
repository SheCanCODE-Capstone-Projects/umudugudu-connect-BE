package com.umudugudu.service;

import com.umudugudu.dto.request.AssignMembersRequest;
import com.umudugudu.dto.request.CreateIsiboRequest;
import com.umudugudu.dto.response.IsiboResponse;
import com.umudugudu.entity.Isibo;
import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.entity.Village;
import com.umudugudu.repository.IsiboRepository;
import com.umudugudu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IsiboManagementService {

    private final IsiboRepository isiboRepository;
    private final UserRepository userRepository;

    /** Resolve the currently authenticated village leader and validate their village. */
    private User getAuthenticatedVillageLeader() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User leader = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        if (leader.getRole() != Role.VILLAGE_LEADER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only village leaders can manage isibos");
        }
        if (leader.getVillage() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You are not assigned to any village");
        }
        return leader;
    }

    /** Ensure the isibo belongs to the leader's village. */
    private Isibo resolveIsibo(Long isiboId, Village village) {
        return isiboRepository.findByIdAndVillage(isiboId, village)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Isibo not found or does not belong to your village"));
    }

    private IsiboResponse toResponse(Isibo isibo) {
        IsiboResponse.LeaderInfo leaderInfo = null;
        if (isibo.getIsiboLeader() != null) {
            User l = isibo.getIsiboLeader();
            leaderInfo = IsiboResponse.LeaderInfo.builder()
                    .id(l.getId())
                    .fullName(l.getFirstName() + " " + l.getLastName())
                    .email(l.getEmail())
                    .phoneNumber(l.getPhoneNumber())
                    .build();
        }

        List<IsiboResponse.MemberInfo> members = isibo.getCitizens() == null
                ? List.of()
                : isibo.getCitizens().stream()
                .map(u -> IsiboResponse.MemberInfo.builder()
                        .id(u.getId())
                        .fullName(u.getFirstName() + " " + u.getLastName())
                        .email(u.getEmail())
                        .phoneNumber(u.getPhoneNumber())
                        .role(u.getRole().name())
                        .build())
                .collect(Collectors.toList());

        return IsiboResponse.builder()
                .id(isibo.getId())
                .name(isibo.getName())
                .villageId(isibo.getVillage().getId())
                .villageName(isibo.getVillage().getName())
                .leader(leaderInfo)
                .members(members)
                .memberCount(members.size())
                .build();
    }


    @Transactional
    public IsiboResponse createIsibo(CreateIsiboRequest request) {
        User villageLeader = getAuthenticatedVillageLeader();
        Village village = villageLeader.getVillage();

        // Prevent duplicate name within the same village
        if (isiboRepository.existsByNameAndVillage(request.getName(), village)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An isibo named '" + request.getName() + "' already exists in your village");
        }

        Isibo isibo = new Isibo();
        isibo.setName(request.getName());
        isibo.setVillage(village);

        if (request.getIsiboLeaderId() != null) {
            User isiboLeader = userRepository
                    .findByIdAndVillageAndRole(request.getIsiboLeaderId(), village, Role.ISIBO_LEADER)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Isibo leader not found or does not belong to your village"));

            isibo.setIsiboLeader(isiboLeader);
            // Also set the leader's isibo reference
            isiboLeader.setIsibo(isibo);
            userRepository.save(isiboLeader);
        }

        Isibo saved = isiboRepository.save(isibo);
        return toResponse(saved);
    }


    @Transactional
    public IsiboResponse assignMembers(Long isiboId, AssignMembersRequest request) {
        User villageLeader = getAuthenticatedVillageLeader();
        Village village = villageLeader.getVillage();

        Isibo isibo = resolveIsibo(isiboId, village);

        // Fetch only members that belong to this village
        List<User> members = userRepository.findByIdInAndVillage(request.getMemberIds(), village);

        if (members.size() != request.getMemberIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Some member IDs are invalid or don't belong to your village");
        }

        members.forEach(member -> member.setIsibo(isibo));
        userRepository.saveAll(members);

        // Reload to get fresh citizens list
        Isibo refreshed = isiboRepository.findById(isiboId).orElseThrow();
        return toResponse(refreshed);
    }


    @Transactional
    public void removeMember(Long isiboId, Long memberId) {
        User villageLeader = getAuthenticatedVillageLeader();
        Village village = villageLeader.getVillage();

        resolveIsibo(isiboId, village); // validate ownership

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Member not found"));

        if (member.getIsibo() == null || !member.getIsibo().getId().equals(isiboId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Member is not assigned to this isibo");
        }

        member.setIsibo(null);
        userRepository.save(member);
    }


    public List<IsiboResponse> listMyIsibos() {
        User villageLeader = getAuthenticatedVillageLeader();
        return isiboRepository.findByVillage(villageLeader.getVillage())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    public IsiboResponse getIsibo(Long isiboId) {
        User villageLeader = getAuthenticatedVillageLeader();
        Isibo isibo = resolveIsibo(isiboId, villageLeader.getVillage());
        return toResponse(isibo);
    }


    public List<IsiboResponse.MemberInfo> getUnassignedMembers() {
        User villageLeader = getAuthenticatedVillageLeader();
        return userRepository.findByVillageAndIsiboIsNull(villageLeader.getVillage())
                .stream()
                .map(u -> IsiboResponse.MemberInfo.builder()
                        .id(u.getId())
                        .fullName(u.getFirstName() + " " + u.getLastName())
                        .email(u.getEmail())
                        .phoneNumber(u.getPhoneNumber())
                        .role(u.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional
    public IsiboResponse assignIsiboLeader(Long isiboId, Long userId) {
        User villageLeader = getAuthenticatedVillageLeader();
        Village village = villageLeader.getVillage();

        Isibo isibo = resolveIsibo(isiboId, village);

        User newLeader = userRepository.findByIdAndVillageAndRole(userId, village, Role.ISIBO_LEADER)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User not found, not in your village, or not an ISIBO_LEADER"));

        if (isibo.getIsiboLeader() != null) {
            User oldLeader = isibo.getIsiboLeader();
            oldLeader.setIsibo(null);
            userRepository.save(oldLeader);
        }

        isibo.setIsiboLeader(newLeader);
        newLeader.setIsibo(isibo);

        userRepository.save(newLeader);
        isiboRepository.save(isibo);

        return toResponse(isibo);
    }
}