package com.railconnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for notification settings.
 */
@Configuration
@ConfigurationProperties(prefix = "railconnect.notification")
public class NotificationProperties {

    /**
     * Email address used as the "from" address for outgoing emails.
     */
    private String emailFrom = "no-reply@railconnect.com";

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }
}
