package com.wutsi.koki.file.server.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class FileJobsTest {
    private val service = mock<FileService>()
    private val publisher = mock<Publisher>()
    private val tenantService = mock<TenantService>()
    private val jobs = FileJobs(service, publisher, tenantService)

    private val now = Date()
    private val earlier = DateUtils.addHours(now, -2)

    private val tenants = listOf(
        TenantEntity(id = 1L, status = TenantStatus.ACTIVE),
        TenantEntity(id = 2L, status = TenantStatus.ACTIVE),
        TenantEntity(id = 3L, status = TenantStatus.SUSPENDED),
        TenantEntity(id = 4L, status = TenantStatus.NEW),
    )
    private val file1s = listOf(
        FileEntity(
            id = 111L,
            tenantId = 1L,
            ownerId = 444L,
            ownerType = ObjectType.LISTING,
            type = FileType.FILE,
            modifiedAt = earlier
        ),
        FileEntity(
            id = 112L,
            tenantId = 1L,
            ownerId = null,
            ownerType = null,
            type = FileType.IMAGE,
            modifiedAt = earlier
        ),
        FileEntity(id = 112L, tenantId = 1L, ownerId = null, ownerType = null, type = FileType.IMAGE, modifiedAt = now),
    )
    private val file2s = listOf(
        FileEntity(
            id = 211L,
            tenantId = 2L,
            ownerId = null,
            ownerType = ObjectType.UNKNOWN,
            type = FileType.FILE,
            modifiedAt = earlier
        ),
        FileEntity(
            id = 212L,
            tenantId = 2L,
            ownerId = 111L,
            ownerType = null,
            type = FileType.FILE,
            modifiedAt = earlier
        ),
    )
    private val file3s = listOf(
        FileEntity(
            id = 311L,
            tenantId = 3L,
            ownerId = 444L,
            ownerType = ObjectType.LISTING,
            type = FileType.FILE,
            modifiedAt = earlier
        ),
    )
    private val file4s = listOf(
        FileEntity(
            id = 411L,
            tenantId = 4L,
            ownerId = 444L,
            ownerType = ObjectType.LISTING,
            type = FileType.FILE,
            modifiedAt = earlier
        ),
    )

    @BeforeEach
    fun setUp() {
        doReturn(tenants).whenever(tenantService).all()

        doReturn(file1s) // tenantId=1L
            .doReturn(file2s) // tenantId=2L
            .doReturn(file3s) // tenantId=3L
            .doReturn(file4s) // tenantId=4L
            .whenever(service).search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    @Test
    fun underReview() {
        // WHEN
        jobs.underReview()

        // THEN
        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher, times(4)).publish(event.capture())

        assertEquals(file1s[0].id, event.firstValue.fileId)
        assertEquals(file1s[0].tenantId, event.firstValue.tenantId)
        assertEquals(file1s[0].ownerId, event.firstValue.owner?.id)
        assertEquals(file1s[0].ownerType, event.firstValue.owner?.type)

        assertEquals(file1s[1].id, event.secondValue.fileId)
        assertEquals(file1s[1].tenantId, event.secondValue.tenantId)
        assertEquals(null, event.secondValue.owner)

        assertEquals(file2s[0].id, event.thirdValue.fileId)
        assertEquals(file2s[0].tenantId, event.thirdValue.tenantId)
        assertEquals(null, event.thirdValue.owner)

        assertEquals(file2s[1].id, event.allValues[3].fileId)
        assertEquals(file2s[1].tenantId, event.allValues[3].tenantId)
        assertEquals(null, event.allValues[3].owner)
    }

    @Test
    fun `underReview - exception do not stop the process`() {
        // GIVEN
        doAnswer { invocation ->
            {
                val event = invocation.arguments[0] as FileUploadedEvent
                if (event.fileId == file1s[0].id) {
                    throw RuntimeException()
                }
            }
        }
            .whenever(publisher)
            .publish(any())

        // WHEN
        jobs.underReview()

        // THEN
        verify(publisher, times(4)).publish(any())
    }
}
