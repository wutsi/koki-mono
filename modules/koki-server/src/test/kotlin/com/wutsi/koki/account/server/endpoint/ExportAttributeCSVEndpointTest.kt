package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/ExportAttributeCSVEndpoint.sql"])
class ExportAttributeCSVEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun export() {
        val response = rest.getForEntity("/v1/attributes/csv", String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals("text/csv", response.headers.contentType?.toString())
        assertEquals("tenant_1-attributes.csv", response.headers.contentDisposition.filename)
        assertEquals("attachment", response.headers.contentDisposition.type)

        println(response.body)
        assertEquals(
            """
               name,type,required,active,choices,label,description
               a,TEXT,yes,yes,P1|P2,label-a,description-a
               b,LONGTEXT,,yes,,label-b,
               c,EMAIL,,,,,
            """.trimIndent(),
            response.body?.trimIndent()
        )
    }
}
