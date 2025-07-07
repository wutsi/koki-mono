package com.wutsi.koki.tracking.server.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.tracking.server.service.filter.PersisterFilter
import org.mockito.Mockito.mock
import kotlin.test.Test

class PersisterJobTest {
    private val filter = mock<PersisterFilter>()
    private val job = PersisterJob(filter)

    @Test
    fun run() {
        job.run()
        verify(filter).flush()
    }

    @Test
    fun destroy() {
        job.destroy()
        verify(filter).flush()
    }
}
