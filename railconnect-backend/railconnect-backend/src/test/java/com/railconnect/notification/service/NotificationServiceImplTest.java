package com.railconnect.notification.service;

import com.railconnect.auth.repository.UserRepository;
import com.railconnect.entity.Notification;
import com.railconnect.entity.User;
import com.railconnect.notification.NotificationChannel;
import com.railconnect.notification.NotificationDeliveryException;
import com.railconnect.notification.NotificationProvider;
import com.railconnect.notification.NotificationStatus;
import com.railconnect.notification.NotificationType;
import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;
import com.railconnect.notification.mapper.NotificationMapper;
import com.railconnect.notification.repository.NotificationRepository;
import com.railconnect.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationProvider emailProvider;

    @Mock
    private NotificationProvider smsProvider;

    @Mock
    private NotificationProvider pushProvider;

    @Mock
    private NotificationProvider inAppProvider;

    @Mock
    private SecurityUtil securityUtil;

    private NotificationServiceImpl notificationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(
                userRepository,
                notificationRepository,
                notificationMapper,
                emailProvider,
                smsProvider,
                pushProvider,
                inAppProvider,
                securityUtil
        );

        testUser = new User();
        testUser.id = 10L;
        testUser.email = "test@example.com";
        testUser.phone = "9876543210";
        testUser.firstName = "John";
        testUser.lastName = "Doe";
    }

    @Test
    void send_multiChannel_dispatchesAllAndSavesNotifications() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(notificationMapper.toResponse(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            return new NotificationResponse(
                    n.getId(),
                    10L,
                    n.getTitle(),
                    n.getMessage(),
                    n.getType() != null ? n.getType().name() : null,
                    n.getChannel() != null ? n.getChannel().name() : null,
                    n.getStatus() != null ? n.getStatus().name() : null,
                    n.isRead(),
                    n.getSentAt()
            );
        });

        NotificationSendRequest request = new NotificationSendRequest(
                10L,
                NotificationType.BOOKING_CONFIRMED,
                List.of(NotificationChannel.EMAIL, NotificationChannel.SMS, NotificationChannel.IN_APP),
                "PNR: 1234567890",
                null,
                null
        );

        List<NotificationResponse> responses = notificationService.send(request);

        assertEquals(3, responses.size());
        verify(emailProvider, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
        verify(smsProvider, times(1)).sendSms(eq("9876543210"), anyString());
        verify(inAppProvider, times(1)).sendInApp(eq(10L), anyString(), anyString());
        verify(notificationRepository, times(3)).save(any(Notification.class));
    }

    @Test
    void send_channelDeliveryFailure_marksStatusFailedAndContinuesRemaining() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        doThrow(new NotificationDeliveryException("SMS gateway error")).when(smsProvider).sendSms(anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(notificationMapper.toResponse(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            return new NotificationResponse(
                    n.getId(),
                    10L,
                    n.getTitle(),
                    n.getMessage(),
                    n.getType() != null ? n.getType().name() : null,
                    n.getChannel() != null ? n.getChannel().name() : null,
                    n.getStatus() != null ? n.getStatus().name() : null,
                    n.isRead(),
                    n.getSentAt()
            );
        });

        NotificationSendRequest request = new NotificationSendRequest(
                10L,
                NotificationType.TRAIN_DELAYED,
                List.of(NotificationChannel.SMS, NotificationChannel.EMAIL),
                "delay 45 mins",
                null,
                null
        );

        List<NotificationResponse> responses = notificationService.send(request);

        assertEquals(2, responses.size());
        assertEquals("FAILED", responses.get(0).getStatus());
        assertEquals("SENT", responses.get(1).getStatus());
    }

    @Test
    void markAsRead_setsReadToTrue() {
        Notification notification = new Notification();
        notification.setRead(false);
        when(notificationRepository.findById(99L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toResponse(notification)).thenReturn(
                new NotificationResponse(99L, 10L, "Title", "Msg", "BOOKING_CONFIRMED",
                        "IN_APP", "SENT", true, null)
        );

        NotificationResponse response = notificationService.markAsRead(99L);

        assertTrue(response.isRead());
        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }
}
