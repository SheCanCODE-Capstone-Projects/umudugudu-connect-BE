package com.umudugudu.service;

import com.umudugudu.entity.User;

import java.util.List;
import java.util.UUID;

public interface RecipientService  {
    List<User> getRecipients(UUID villageId, List<UUID> isibIds);
}
