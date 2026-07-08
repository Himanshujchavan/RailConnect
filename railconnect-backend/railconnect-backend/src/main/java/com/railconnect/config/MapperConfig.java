package com.railconnect.config;

import com.railconnect.auth.mapper.AuthMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that explicitly registers MapStruct mappers as Spring beans.
 * This guarantees that AuthMapper is available for injection even if annotation
 * processing does not generate the bean automatically.
 */
@Configuration
public class MapperConfig {

    @Bean
    public AuthMapper authMapper() {
        return Mappers.getMapper(AuthMapper.class);
    }
}
