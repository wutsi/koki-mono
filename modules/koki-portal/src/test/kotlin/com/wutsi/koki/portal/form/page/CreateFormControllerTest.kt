package com.wutsi.koki.portal.form.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.CreateFormRequest
import com.wutsi.koki.form.dto.CreateFormResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateFormControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/forms/create")
        assertCurrentPageIs(PageName.FORM_CREATE)

        input("#code", "T-100")
        input("#name", "Control List")
        select("#active", 0)
        input("#description", "This is the description")
        click("[type=submit]")

        val request = argumentCaptor<CreateFormRequest>()
        verify(rest).postForEntity(eq("$sdkBaseUrl/v1/forms"), request.capture(), eq(CreateFormResponse::class.java))

        assertEquals("T-100", request.firstValue.code)
        assertEquals("Control List", request.firstValue.name)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals(true, request.firstValue.active)

        assertCurrentPageIs(PageName.FORM)
        assertElementPresent("#koki-modal")
    }

    @Test
    fun cancel() {
        navigateTo("/forms/create")
        assertCurrentPageIs(PageName.FORM_CREATE)

        input("#code", "T-100")
        input("#name", "Control List")
        select("#active", 0)
        input("#description", "This is the description")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.FORM_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest)
            .postForEntity(any<String>(), any<CreateFormRequest>(), eq(CreateFormResponse::class.java))

        navigateTo("/forms/create")

        input("#code", "T-100")
        input("#name", "Control List")
        select("#active", 0)
        input("#description", "This is the description")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.FORM_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/forms/create")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `create - without permission form-manage`() {
        setUpUserWithoutPermissions(listOf("form:manage"))

        navigateTo("/forms/create")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
