package com.wutsi.koki.platform.cache.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.cache.AbstractCacheConfiguration
import com.wutsi.koki.platform.cache.redis.RedisCache
import com.wutsi.koki.platform.cache.redis.RedisHealthIndicator
import io.lettuce.core.RedisClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@ConditionalOnProperty(
    value = ["wutsi.platform.cache.type"],
    havingValue = "redis",
)
open class RedisCacheConfiguration(
    private val objectMapper: ObjectMapper,
    @Value("\${wutsi.platform.cache.name}") name: String,
    @Value(value = "\${wutsi.platform.cache.ttl:86400}") private val ttl: Int,
    @Value(value = "\${wutsi.platform.cache.redis.url}") private val url: String,
) : AbstractCacheConfiguration(name) {
    @Bean
    override fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                RedisCache(name, ttl, redisClient(), objectMapper),
            ),
        )
        return cacheManager
    }

    @Bean
    open fun redisClient(): RedisClient {
        return RedisClient.create(url)
    }

    @Bean
    open fun redisHealthCheck(): HealthIndicator {
        return RedisHealthIndicator(cache())
    }
}
