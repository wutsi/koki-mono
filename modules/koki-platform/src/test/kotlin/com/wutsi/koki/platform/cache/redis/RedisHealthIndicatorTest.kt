package com.wutsi.koki.platform.cache.redis

import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.boot.health.contributor.Status
import org.springframework.cache.Cache
import kotlin.test.assertEquals

internal class RedisHealthIndicatorTest {
    lateinit var cache: Cache
    lateinit var health: HealthIndicator

    @BeforeEach
    fun setUp() {
        cache = mock()
        health = RedisHealthIndicator(cache)
    }

    @Test
    fun up() {
        assertEquals(Status.UP, health.health()?.status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException()).whenever(cache).get(RedisHealthIndicator.KEY)
        assertEquals(Status.DOWN, health.health()?.status)
    }
}
