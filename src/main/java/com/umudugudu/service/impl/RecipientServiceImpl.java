package com.umudugudu.service.impl;

import com.umudugudu.entity.User;
import com.umudugudu.entity.Village;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.RecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class RecipientServiceImpl implements RecipientService {
    private final UserRepository userRepository;
    @Override
    public List<User> getRecipients(UUID villageId, List<UUID> isibIds) {

        Village village = new Village();
        village.setId(villageId);

        if (isibIds == null || isibIds.isEmpty()) {
            return userRepository.findByVillageAndIsiboIsNull(village);
        }

        return userRepository.findByIsiboIdIn(isibIds);
    }
    }

