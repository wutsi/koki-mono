package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.form.dto.CreateFormRequest
import com.wutsi.koki.form.dto.CreateFormResponse
import com.wutsi.koki.form.server.dao.FormRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql"])
class CreateFormEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var formDao: FormRepository

    private val request = CreateFormRequest(
        name = "T-100",
        description = "This is a form for entering information",
        active = true
    )

    @Test
    fun create() {
        val result = rest.postForEntity("/v1/forms", request, CreateFormResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formId = result.body!!.formId
        val form = formDao.findById(formId).get()
        assertEquals(request.name, form.name)
        assertEquals(request.description, form.description)
        assertEquals(request.active, form.active)
        assertEquals(TENANT_ID, form.tenantId)
        assertEquals(USER_ID, form.createdById)
        assertEquals(USER_ID, form.modifiedById)
    }
}
