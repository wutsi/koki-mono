package com.wutsi.koki.file.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/file/DeleteFileEndpoint.sql"])
class DeleteFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FileRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun delete() {
        rest.delete("/v1/files/100")

        val file = dao.findById(100L).get()
        assertTrue(file.deleted)
        assertNotNull(file.deletedAt)
        assertEquals(USER_ID, file.deletedById)

        val event = argumentCaptor<FileDeletedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(111L, event.firstValue.owner?.id)
        assertEquals(ObjectType.ACCOUNT, event.firstValue.owner?.type)
    }
}
