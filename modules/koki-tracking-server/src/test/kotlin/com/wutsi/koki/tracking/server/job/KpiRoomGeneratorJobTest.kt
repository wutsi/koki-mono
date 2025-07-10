package com.wutsi.koki.tracking.server.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.tracking.server.service.KpiRoomService
import org.mockito.Mockito.mock
import java.time.LocalDate
import kotlin.test.Test

class KpiRoomJobTest {
    private val service = mock<KpiRoomService>()
    private val job = KpiRoomJob(service)

    @Test
    fun daily() {
        job.daily()
        verify(service).generate(LocalDate.now())
    }

    @Test
    fun monthly() {
        job.monthly()
        verify(service).generate(LocalDate.now().minusMonths(1))
    }
}
