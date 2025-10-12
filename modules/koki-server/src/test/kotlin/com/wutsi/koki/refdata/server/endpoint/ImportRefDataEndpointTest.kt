package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.refdata.server.dao.AmenityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql"])
class ImportRefDataEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AmenityRepository

    @Test
    fun import() {
        val response = rest.getForEntity("/v1/refdata/import", Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
