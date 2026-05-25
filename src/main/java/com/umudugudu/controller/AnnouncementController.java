package com.umudugudu.controller;

import com.umudugudu.dto.request.CreateAnnouncementRequest;
import com.umudugudu.dto.response.AnnouncementResponse;
import com.umudugudu.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnnouncementResponse createAnnouncement(
            @RequestBody CreateAnnouncementRequest request
    ) {

        return announcementService.createAnnouncement(request);
    }
}
