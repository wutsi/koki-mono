package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.server.dao.FormOwnerRepository
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

    @Autowired
    private lateinit var ownerDao: FormOwnerRepository

    @Test
    fun create() {
        val request = CreateFormRequest(
            name = "T-100",
            description = "This is a form for entering information",
            active = true
        )

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

        val owners = ownerDao.findByFormId(formId)
        assertEquals(0, owners.size)
    }

    @Test
    fun `create with owner`() {
        val request = CreateFormRequest(
            name = "T-100",
            description = "This is a form for entering information",
            active = true,
            owner = ObjectReference(
                id = 111L,
                type = ObjectType.CONTACT,
            )
        )

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

        val owners = ownerDao.findByFormId(formId)
        assertEquals(1, owners.size)
        assertEquals(form.id, owners[0].formId)
        assertEquals(request.owner?.id, owners[0].ownerId)
        assertEquals(request.owner?.type, owners[0].ownerType)
    }
}
