package com.railconnect.notification.mapper;

import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    /**
     * Maps your custom Notification entity to the NotificationResponse DTO.
     */
    @Mapping(source = "user.id", target = "userId")

    @Mapping(source = "sentAt", target = "createdAt")
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "read", ignore = true) // Changed from "isRead" to "read" to match JavaBean conventions
    NotificationResponse toResponse(Notification notification);

    /**
     * Maps a list of Notification entities to a list of NotificationResponse DTOs.
     */
    List<NotificationResponse> toResponseList(List<Notification> notifications);
}