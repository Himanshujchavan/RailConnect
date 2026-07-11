package com.railconnect.notification.controller;

import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;
import com.railconnect.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Phase 5 — Notification Module.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<List<NotificationResponse>> send(@Valid @RequestBody NotificationSendRequest request) {
        return new ResponseEntity<>(notificationService.send(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> history(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getHistory(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
