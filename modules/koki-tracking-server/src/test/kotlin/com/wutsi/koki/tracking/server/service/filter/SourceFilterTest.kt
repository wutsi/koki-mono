package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.tracking.server.domain.TrackEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class SourceFilterTest {
    private val filter = SourceFilter(DefaultKVLogger())

    @Test
    fun empty() {
        val track = TrackEntity()
        assertEquals(null, filter.filter(track).source)
    }

    @Test
    fun source() {
        val track = TrackEntity(ua = "Twitterbot")
        assertEquals("twitter", filter.filter(track).source)
    }
}
