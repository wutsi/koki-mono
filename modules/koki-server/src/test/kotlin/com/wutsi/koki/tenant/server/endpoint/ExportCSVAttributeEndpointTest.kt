package com.wutsi.koki.tenant.server.endpoint

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/ExportCSVAttributeEndpoint.sql"])
class ExportCSVAttributeEndpointTest : TenantAwareEndpointTest() {
    override fun getTenantId() = 1L

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
               name,type,active,choices,label,description
               1,a,TEXT,true,label-a,description-a
               2,b,LONGTEXT,true,label-b,
               3,c,EMAIL,false,,
           """.trimIndent(),
            response.body?.trimIndent()
        )
    }
}
