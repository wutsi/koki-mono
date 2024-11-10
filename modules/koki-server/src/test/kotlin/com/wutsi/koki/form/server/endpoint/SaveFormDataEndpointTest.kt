package com.wutsi.koki.tenant.server.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.SaveFormDataRequest
import com.wutsi.koki.form.dto.SaveFormDataResponse
import com.wutsi.koki.form.server.dao.FormDataRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/SaveFormDataEndpoint.sql"])
class SaveFormDataEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FormDataRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val request = SaveFormDataRequest(
        data = mapOf(
            "var1" to "value1",
            "var2" to "value2",
        )
    )

    @Test
    fun create() {
        val result = rest.postForEntity("/v1/forms/100/form-data", request, SaveFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.formDataId
        val formData = dao.findById(id).get()

        assertEquals(getTenantId(), formData.tenant.id)
        assertEquals("100", formData.form.id)

        val data = objectMapper.readValue(formData.data, Map::class.java)
        assertEquals(2, data.size)
        assertEquals("value1", data["var1"])
        assertEquals("value2", data["var2"])
    }

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/form-data/110", request, SaveFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.formDataId
        assertEquals("110", id)

        val formData = dao.findById(id).get()
        assertEquals(getTenantId(), formData.tenant.id)
        assertEquals("100", formData.form.id)

        val data = objectMapper.readValue(formData.data, Map::class.java)
        assertEquals(2, data.size)
        assertEquals("value1", data["var1"])
        assertEquals("value2", data["var2"])
    }

    @Test
    fun `update data with invalid ID`() {
        val result = rest.postForEntity("/v1/form-data/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update data of form fom another tenant`() {
        val result = rest.getForEntity("/v1/form-data/210", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }
}
