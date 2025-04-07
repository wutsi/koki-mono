package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.UpdateFormRequest
import com.wutsi.koki.form.server.dao.FormRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/UpdateFormEndpoint.sql"])
class UpdateFormEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var formDao: FormRepository

    private val request = UpdateFormRequest(
        code = "T100",
        name = "T-100",
        description = "This is a form for entering information",
        active = true
    )

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/forms/100", request, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val form = formDao.findById(100L).get()
        assertEquals(request.name, form.name)
        assertEquals(request.description, form.description)
        assertEquals(request.active, form.active)
        assertEquals(USER_ID, form.modifiedById)
    }

    @Test
    fun `duplicate code`() {
        val result = rest.postForEntity("/v1/forms/100", request.copy(code = "T-500"), ErrorResponse::class.java)
        assertEquals(HttpStatus.CONFLICT, result.statusCode)

        assertEquals(ErrorCode.FORM_DUPLICATE_CODE, result.body?.error?.code)
    }
}
