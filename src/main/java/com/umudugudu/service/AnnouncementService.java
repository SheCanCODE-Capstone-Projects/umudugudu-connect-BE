package com.umudugudu.service;

import com.umudugudu.dto.request.CreateAnnouncementRequest;
import com.umudugudu.dto.response.AnnouncementResponse;

public interface AnnouncementService {
    AnnouncementResponse createAnnouncement(
            CreateAnnouncementRequest request
    );
}
