package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/RemoveImageEndpoint.sql"])
class RemoveImageEndpointTest : AuthorizationAwareEndpointTest() {
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
    fun delete() {
        rest.delete("/v1/rooms/111/images/11")

        val ids = getFileIds(111L)
        assertEquals(1, ids.size)
        assertEquals(listOf(22L), ids)
    }

    @Test
    fun `delete invalid amenity`() {
        rest.delete("/v1/rooms/112/images/1000000")

        val ids = getFileIds(112L)
        assertEquals(2, ids.size)
        assertEquals(listOf(11L, 22L), ids)
    }
}
