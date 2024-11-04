package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.service.StartExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.lang.IllegalStateException
import kotlin.jvm.java
import kotlin.test.assertEquals

class ActivityExecutorProviderTest {
    private val start = Mockito.mock(StartExecutor::class.java)
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
