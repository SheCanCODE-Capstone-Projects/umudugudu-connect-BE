package com.umudugudu.service;

import com.umudugudu.dto.request.CreateActivityRequest;
import com.umudugudu.entity.Activity;

import java.util.UUID;

public interface ActivityService {


    Activity createActivity(CreateActivityRequest request, UUID leaderId);
}
