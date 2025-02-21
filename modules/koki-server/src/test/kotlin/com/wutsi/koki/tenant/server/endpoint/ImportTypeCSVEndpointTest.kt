package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.tenant.server.dao.TypeRepository
import com.wutsi.koki.tenant.server.domain.TypeEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/ImportTypeCSVEndpoint.sql"])
class ImportTypeCSVEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TypeRepository

    private fun upload(objectType: ObjectType, body: String): ImportResponse {
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
            "/v1/types/csv?object-type=$objectType",
            HttpMethod.POST,
            requestEntity,
            ImportResponse::class.java,
        ).body!!
    }

    @Test
    fun import() {
        val response = upload(
            ObjectType.ACCOUNT,
            """
                "name","title","description"
                "a","RoleA",
                "b","RoleB","Priority of the ticket"
                "c",,
                "new",,""
            """.trimIndent()
        )

        assertEquals(1, response.added)
        assertEquals(4, response.updated)
        assertEquals(0, response.errors)
        assertTrue(response.errorMessages.isEmpty())

        val typeA = findType("a", ObjectType.ACCOUNT)
        assertEquals("a", typeA?.name)
        assertEquals("RoleA", typeA?.title)
        assertEquals(TENANT_ID, typeA?.tenantId)
        assertEquals(ObjectType.ACCOUNT, typeA?.objectType)
        assertEquals(true, typeA?.active)
        assertNull(typeA?.description)

        val typeB = findType("b", ObjectType.ACCOUNT)
        assertEquals("b", typeB?.name)
        assertEquals("RoleB", typeB?.title)
        assertEquals(TENANT_ID, typeB?.tenantId)
        assertEquals(true, typeB?.active)
        assertEquals("Priority of the ticket", typeB?.description)

        val typeC = findType("c", ObjectType.ACCOUNT)
        assertEquals(TENANT_ID, typeC?.tenantId)
        assertEquals("c", typeC?.name)
        assertNull(typeC?.title)
        assertEquals(true, typeC?.active)
        assertNull(typeC?.description)

        val typeX = findType("x", ObjectType.ACCOUNT)
        assertEquals(false, typeX?.active)

        val typeNew = findType("new", ObjectType.ACCOUNT)
        assertEquals(TENANT_ID, typeNew?.tenantId)
        assertEquals("new", typeNew?.name)
        assertNull(typeNew?.title)
        assertEquals(true, typeNew?.active)
        assertNull(typeNew?.description)
    }

    @Test
    fun noName() {
        val response = upload(
            ObjectType.ACCOUNT,
            """
                "name","description"
                "","Priority of the ticket"
            """.trimIndent()
        )

        assertEquals(1, response.errors)
        assertFalse(response.errorMessages.isEmpty())
        assertEquals(ErrorCode.TYPE_NAME_MISSING, response.errorMessages[0].code)
    }

    @Test
    fun `malformed row`() {
        val response = upload(
            ObjectType.ACCOUNT,
            """
                "name","description"
                "b11"
            """.trimIndent()
        )

        assertEquals(1, response.errors)

        assertFalse(response.errorMessages.isEmpty())
        assertEquals(ErrorCode.IMPORT_ERROR, response.errorMessages[0].code)
    }

    private fun findType(name: String, objectType: ObjectType): TypeEntity? {
        return dao.findByNameIgnoreCaseAndObjectTypeAndTenantId(name, objectType, TENANT_ID)
    }
}
