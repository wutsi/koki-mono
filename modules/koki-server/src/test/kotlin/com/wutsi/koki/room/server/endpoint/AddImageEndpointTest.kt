package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.room.dto.AddImageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/AddImageEndpoint.sql"])
class AddImageEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var ds: DataSource

    private fun getFileIds(roomId: Long): List<Long> {
        val result = mutableListOf<Long>()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT file_fk FROM T_ROOM_IMAGE where room_fk=$roomId")
                rs.use {
                    while (rs.next()) {
                        result.add(rs.getLong(1))
                    }
                }
            }
        }
        return result.sorted()
    }

    @Test
    fun add() {
        val request = AddImageRequest(
            fileIds = listOf(33, 44)
        )
        val response = rest.postForEntity("/v1/rooms/111/images", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileIds = getFileIds(111L)
        assertEquals(4, fileIds.size)
        assertEquals(listOf(11L, 22L, 33L, 44L), fileIds)
    }

    @Test
    fun `add again`() {
        val request = AddImageRequest(
            fileIds = listOf(11L)
        )
        val response = rest.postForEntity("/v1/rooms/112/images", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileIds = getFileIds(112L)
        assertEquals(2, fileIds.size)
        assertEquals(listOf(11L, 22L), fileIds)
    }

    @Test
    fun `add image only`() {
        val request = AddImageRequest(
            fileIds = listOf(55L)
        )
        val response = rest.postForEntity("/v1/rooms/111/images", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileIds = getFileIds(111L)
        assertEquals(false, fileIds.contains(55L))
    }
}
