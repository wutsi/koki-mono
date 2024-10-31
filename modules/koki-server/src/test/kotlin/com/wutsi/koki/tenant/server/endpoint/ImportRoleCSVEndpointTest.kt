package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.tenant.server.dao.RoleRepository
import com.wutsi.koki.tenant.server.domain.RoleEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/ImportRoleCSVEndpoint.sql"])
class ImportRoleCSVEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoleRepository

    private fun upload(body: String): ImportResponse {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val fileMap = LinkedMultiValueMap<String, String>()
        val contentDisposition = ContentDisposition
            .builder("form-data")
            .name("file")
            .filename("test.csv")
            .build()
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        val fileEntity = HttpEntity(body.toByteArray(), fileMap)

        val body = LinkedMultiValueMap<String, Any>()
        body.add("file", fileEntity)

        val requestEntity = HttpEntity<MultiValueMap<String, Any>>(body, headers)
        return rest.exchange(
            "/v1/roles/csv",
            HttpMethod.POST,
            requestEntity,
            ImportResponse::class.java,
        ).body!!
    }

    @Test
    fun import() {
        val response = upload(
            """
                "name","active","description"
                "a","Yes",,,
                "b","No","Priority of the ticket"
                "c",,
                "new","yes",""
            """.trimIndent()
        )

        assertEquals(3, response.updated)
        assertEquals(1, response.added)
        assertEquals(0, response.errors)
        assertTrue(response.errorMessages.isEmpty())

        val attrA = findRole("a")
        assertEquals("a", attrA.name)
        assertTrue(attrA.active)
        assertNull(attrA.description)

        val attrB = findRole("b")
        assertEquals("b", attrB.name)
        assertFalse(attrB.active)
        assertEquals("Priority of the ticket", attrB.description)

        val attrC = findRole("c")
        assertEquals("c", attrC.name)
        assertFalse(attrC.active)
        assertNull(attrC.description)

        val attrNew = findRole("new")
        assertEquals("new", attrNew.name)
        assertTrue(attrNew.active)
        assertNull(attrNew.description)
    }

    @Test
    fun noName() {
        val response = upload(
            """
                "name","active","description"
                "","No","Priority of the ticket"
            """.trimIndent()
        )

        assertEquals(0, response.updated)
        assertEquals(0, response.added)
        assertEquals(1, response.errors)
        assertFalse(response.errorMessages.isEmpty())
        assertEquals(ErrorCode.ROLE_NAME_MISSING, response.errorMessages[0].code)
    }

    private fun findRole(name: String): RoleEntity {
        return dao.findByTenantIdAndNameIn(getTenantId(), listOf(name)).first()
    }
}
