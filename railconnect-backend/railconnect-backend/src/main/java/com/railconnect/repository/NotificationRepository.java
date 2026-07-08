package com.railconnect.repository;

import com.railconnect.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("commonNotificationRepository")
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Additional query methods can be added as needed
}
