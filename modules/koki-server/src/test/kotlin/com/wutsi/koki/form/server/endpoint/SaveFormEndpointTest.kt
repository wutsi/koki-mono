package com.wutsi.koki.form.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.dto.FormOption
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.form.dto.SaveFormResponse
import com.wutsi.koki.form.server.dao.FormRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/SaveFormEndpoint.sql"])
class SaveFormEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var formDao: FormRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val request = SaveFormRequest(
        content = FormContent(
            name = "WFT-100",
            title = "Sample form",
            description = "This is an exempla of form",
            elements = listOf(
                FormElement(
                    type = FormElementType.SECTION,
                    title = "Personal Information",
                    description = "Enter the client personal information",
                    elements = listOf(
                        FormElement(
                            type = FormElementType.CHECKBOXES,
                            name = "var1",
                            title = "test",
                            description = "This is the description",
                            options = listOf(
                                FormOption(value = "1"),
                                FormOption(value = "foo", text = "FOO"),
                                FormOption(value = "value1", text = "Value #1"),
                            )
                        ),
                        FormElement(
                            type = FormElementType.IMAGE,
                            url = "https://www.google.com/img/1.png",
                            title = "test",
                        ),
                        FormElement(
                            type = FormElementType.PARAGRAPH,
                            name = "var1",
                            title = "test",
                            description = "This is the description",
                        ),
                    ),
                )
            )
        )

    )

    private fun assertEquals(expected: FormContent, json: String) {
        val value = objectMapper.readValue(json, FormContent::class.java)
        assertEquals(expected, value)
    }

    @Test
    fun create() {
        val result = rest.postForEntity("/v1/forms", request, SaveFormResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formId = result.body!!.formId
        val form = formDao.findById(formId).get()
        assertEquals(request.content.name, form.name)
        assertEquals(request.content.title, form.title)
        assertEquals(request.content.description, form.description)
        assertEquals(request.content, form.content)
    }

    @Test
    fun `create with duplicate name`() {
        val xrequest = request.copy(
            content = request.content.copy(name = "f-100")
        )
        val result = rest.postForEntity("/v1/forms", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.FORM_DUPLICATE_NAME, result.body?.error?.code)
    }

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/forms/100", request, SaveFormResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val formId = result.body!!.formId
        val form = formDao.findById(formId).get()
        assertEquals(request.content.name, form.name)
        assertEquals(request.content.title, form.title)
        assertEquals(request.content.description, form.description)
        assertEquals(request.content, form.content)
    }

    @Test
    fun `update workflow not found`() {
        val result = rest.postForEntity("/v1/forms/999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update workflow of another tenant`() {
        val result = rest.postForEntity("/v1/forms/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update with duplicate name`() {
        val xrequest = request.copy(
            content = request.content.copy(name = "f-100")
        )
        val result = rest.postForEntity("/v1/forms/110", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.FORM_DUPLICATE_NAME, result.body?.error?.code)
    }
}
