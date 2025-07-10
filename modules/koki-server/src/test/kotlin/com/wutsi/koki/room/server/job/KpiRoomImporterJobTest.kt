package com.wutsi.koki.room.server.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.room.server.service.KpiRoomImporter
import org.mockito.Mockito.mock
import java.time.LocalDate
import kotlin.test.Test

class KpiRoomImporterJobTest {
    private val service = mock<KpiRoomImporter>()
    private val job = KpiRoomImporterJob(service)

    @Test
    fun daily() {
        job.daily()
        verify(service).import(LocalDate.now())
    }

    @Test
    fun monthly() {
        job.monthly()
        verify(service).import(LocalDate.now().minusMonths(1))
    }
}
