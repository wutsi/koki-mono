package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.dto.ActivityType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.lang.IllegalStateException
import kotlin.test.assertEquals

class ActivityExecutorProviderTest {
    private val start = mock(StartExecutor::class.java)
    private val provider = ActivityExecutorProvider(start)

    @Test
    fun getStart() {
        assertEquals(start, provider.get(ActivityType.START))
    }

    @Test
    fun unknown() {
        assertThrows<IllegalStateException> {
            provider.get(ActivityType.UNKNOWN)
        }
    }
}
