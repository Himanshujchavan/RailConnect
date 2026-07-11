package com.railconnect.notification.dtorequestresponse;

import com.railconnect.notification.NotificationChannel;
import com.railconnect.notification.NotificationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NotificationSendRequest(
        @NotNull Long userId,

        @NotNull NotificationType type,

        @NotEmpty List<NotificationChannel> channels,

        // Free-text context spliced into the default template body, e.g. "PNR 1234567890".
        String details,

        // Optional overrides - if omitted, NotificationTemplates supplies the default copy.
        String titleOverride,
        String messageOverride
) {
}
