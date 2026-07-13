package com.railconnect.common.cache;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Phase 10 — Caching / Redis.
 * <p>
 * Cache names below get their own TTL; anything not listed here falls back to the 10-minute
 * default configured in {@code application.yml} ({@code spring.cache.redis.time-to-live}).
 * <p>
 * Values are serialized as JSON ({@link GenericJackson2JsonRedisSerializer}) rather than Java's
 * default JDK serialization - most response DTOs in this codebase are records, which don't
 * implement {@code Serializable}, so JDK serialization would fail on them at cache-write time.
 * <p>
 * What's deliberately NOT cached: seat availability, train status, and anything under
 * Phase-9 reporting/live search - those need to be current on every read, not stale-for-10-minutes.
 * What IS cached: reference/lookup data that changes rarely (stations, fare/pricing rules) -
 * see {@code @Cacheable} on {@code StationServiceImpl}, {@code FareManagementServiceImpl}, and
 * {@code PricingAdminServiceImpl}, each paired with a matching {@code @CacheEvict} on their
 * create/update/delete methods so a cached list never goes stale after an admin edit.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String STATIONS_CACHE = "stations";
    public static final String FARE_RULES_CACHE = "fareRules";
    public static final String FESTIVAL_PRICING_RULES_CACHE = "festivalPricingRules";
    public static final String PEAK_SEASON_PRICING_RULES_CACHE = "peakSeasonPricingRules";

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return builder -> builder
                // Station list barely ever changes - safe to cache longer than the default.
                .withCacheConfiguration(STATIONS_CACHE, base.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration(FARE_RULES_CACHE, base.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(FESTIVAL_PRICING_RULES_CACHE, base.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(PEAK_SEASON_PRICING_RULES_CACHE, base.entryTtl(Duration.ofMinutes(30)));
    }
}
