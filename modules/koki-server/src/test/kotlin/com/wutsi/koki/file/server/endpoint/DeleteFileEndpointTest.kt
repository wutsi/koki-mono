package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.file.server.dao.FileRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/file/DeleteFileEndpoint.sql"])
class DeleteFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FileRepository

    @Test
    fun delete() {
        rest.delete("/v1/files/100")

        val file = dao.findById(100L).get()
        assertTrue(file.deleted)
        assertNotNull(file.deletedAt)
        assertEquals(USER_ID, file.deletedById)
    }
}
