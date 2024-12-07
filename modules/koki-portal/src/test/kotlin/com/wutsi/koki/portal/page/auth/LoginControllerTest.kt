package com.wutsi.koki.portal.page.auth

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.party.dto.LoginResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(LoginResponse(accessToken)).whenever(kokiAuthentication).login(any(), any())
    }

    @Test
    fun login() {
        // WHEN
        navigateTo("/login")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON")

        verify(kokiAuthentication).login("ray.sponsible@gmail.com", "secret")

        val accessTokenArg = argumentCaptor<String>()
        verify(accessTokenHolder).set(accessTokenArg.capture(), any(), any())
        assertEquals(accessToken, accessTokenArg.firstValue)

        assertCurrentPageIs(PageName.HOME)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun `login failure`() {
        // GIVEN
        val ex = createHttpClientErrorException(409, ErrorCode.AUTHENTICATION_FAILED)
        doThrow(ex).whenever(kokiAuthentication).login(any(), any())

        // WHEN
        navigateTo("/login")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON")

        verify(kokiAuthentication).login("ray.sponsible@gmail.com", "secret")

        assertCurrentPageIs(PageName.LOGIN)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `redirect after login`() {
        // GIVEN
        setUpAnonymousUser()

        val form = Form(
            id = "309302",
            name = "FRM-001",
            title = "Incident Report",
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).form(any())

        val html = generateFormHtml()
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/4304309")
        setupUsers()

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
        input("INPUT[name=email]", "ray.sponsible@gmail.com")
        input("INPUT[name=password]", "secret")
        click("BUTTON", 1000)

        verify(kokiAuthentication).login("ray.sponsible@gmail.com", "secret")

        val accessTokenArg = argumentCaptor<String>()
        verify(accessTokenHolder).set(accessTokenArg.capture(), any(), any())
        assertEquals(accessToken, accessTokenArg.firstValue)

        assertCurrentPageIs(PageName.FORM)
    }

    private fun generateFormHtml(): String {
        return """
            <DIV class='form test'>
              <FORM method='post' action=''>
                <DIV class='form-header'>
                  <H1 class='form-title'>Incident Report</H1>
                </DIV>
                <DIV class='form-body'>
                  <DIV class='section'>
                    <DIV class='section-body'>
                      <DIV class='section-item'>
                        <LABEL class='title'><SPAN>Customer Name</SPAN><SPAN class='required'>*</SPAN></LABEL>
                        <INPUT name='customer_name' required/>
                      </DIV>
                      <DIV class='section-item'>
                        <LABEL class='title'><SPAN>Customer Email</SPAN><SPAN class='required'>*</SPAN></LABEL>
                        <INPUT name='customer_email' type='email' required/>
                      </DIV>
                    </DIV>
                  </DIV>
                </DIV>
                <DIV class='form-footer'>
                  <DIV class='form-button-group'>
                    <BUTTON type='submit'>Submit</BUTTON>
                  </DIV>
                </DIV>
              </FORM>
            </DIV>
        """.trimIndent()
    }
}
