package com.wutsi.koki.portal.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.AccountFixtures.attributes
import com.wutsi.koki.RefDataFixtures.locations
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.UpdateAccountRequest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class EditAccountControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/accounts/${account.id}/edit")
        assertCurrentPageIs(PageName.ACCOUNT_EDIT)

        input("#name", "Ray Construction Inc")
        select("#accountTypeId", 2)
        input("#phone", "5147580000")
        input("#mobile", "5147580011")
        input("#email", "info@ray-construction.com")
        scroll(0.25)
        input("#website", "https://www.ray-construction.com")
        select2("#language", "English")
        input("#description", "This is the description")
        select2("#shippingCountry", "Cameroon")
        select2("#shippingCityId", "${locations[3].name}, ${locations[0].name}")
        scroll(0.25)
        input("#shippingStreet", "340 Pascal")
        input("#shippingPostalCode", "H0H 0H0")
        select("#billingSameAsShippingAddress", 1)
        select2("#billingCountry", "Cameroon")
        scrollToBottom()
        select2("#billingCityId", "${locations[2].name}, ${locations[0].name}")
        input("#billingStreet", "340 Nicolet")
        input("#billingPostalCode", "HzH zHz")
        attributes.forEach { attribute ->
            input("#attribute-${attribute.id}", "${attribute.id}2222")
        }

        click("button[type=submit]")

        val request = argumentCaptor<UpdateAccountRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/accounts/${account.id}"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("Ray Construction Inc", request.firstValue.name)
        assertEquals(112L, request.firstValue.accountTypeId)
        assertEquals("+15147580000", request.firstValue.phone)
        assertEquals("+15147580011", request.firstValue.mobile)
        assertEquals("info@ray-construction.com", request.firstValue.email)
        assertEquals("https://www.ray-construction.com", request.firstValue.website)
        assertEquals("en", request.firstValue.language)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals("340 Pascal", request.firstValue.shippingStreet)
        assertEquals("H0H 0H0", request.firstValue.shippingPostalCode)
        assertEquals(locations[3].id, request.firstValue.shippingCityId)
        assertEquals("CM", request.firstValue.shippingCountry)
        assertEquals(false, request.firstValue.billingSameAsShippingAddress)
        assertEquals("340 Nicolet", request.firstValue.billingStreet)
        assertEquals("HzH zHz", request.firstValue.billingPostalCode)
        assertEquals(locations[2].id, request.firstValue.billingCityId)
        assertEquals("CM", request.firstValue.billingCountry)
        attributes.forEach { attribute ->
            assertEquals("${attribute.id}2222", request.firstValue.attributes[attribute.id])
        }

        assertCurrentPageIs(PageName.ACCOUNT)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `edit - with full_access permission`() {
        setupUserWithFullAccessPermissions("account")

        navigateTo("/accounts/${account.id}/edit")
        assertCurrentPageIs(PageName.ACCOUNT_EDIT)
    }

    @Test
    fun `billing same as shipping`() {
        navigateTo("/accounts/${account.id}/edit")
        assertCurrentPageIs(PageName.ACCOUNT_EDIT)

        input("#name", "Ray Construction Inc")
        select("#accountTypeId", 2)
        input("#phone", "5147580000")
        input("#mobile", "5147580011")
        input("#email", "info@ray-construction.com")
        scroll(0.25)
        input("#website", "https://www.ray-construction.com")
        select2("#language", "French")
        input("#description", "This is the description")
        select("#shippingCountry", 3)
        scroll(0.25)
        select2("#shippingCityId", "${locations[3].name}, ${locations[0].name}")
        input("#shippingStreet", "340 Pascal")
        input("#shippingPostalCode", "H0H 0H0")
        select("#billingSameAsShippingAddress", 0)
        scrollToBottom()
        attributes.forEach { attribute ->
            input("#attribute-${attribute.id}", "${attribute.id}2222")
        }

        click("button[type=submit]")

        val request = argumentCaptor<UpdateAccountRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/accounts/${account.id}"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("Ray Construction Inc", request.firstValue.name)
        assertEquals(112L, request.firstValue.accountTypeId)
        assertEquals("+15147580000", request.firstValue.phone)
        assertEquals("+15147580011", request.firstValue.mobile)
        assertEquals("info@ray-construction.com", request.firstValue.email)
        assertEquals("https://www.ray-construction.com", request.firstValue.website)
        assertEquals("fr", request.firstValue.language)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals("340 Pascal", request.firstValue.shippingStreet)
        assertEquals("H0H 0H0", request.firstValue.shippingPostalCode)
        assertEquals(locations[3].id, request.firstValue.shippingCityId)
        assertEquals("DZ", request.firstValue.shippingCountry)
        assertEquals(true, request.firstValue.billingSameAsShippingAddress)
        scrollToBottom()
        attributes.forEach { attribute ->
            assertEquals("${attribute.id}2222", request.firstValue.attributes[attribute.id])
        }

        assertCurrentPageIs(PageName.ACCOUNT)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun cancel() {
        navigateTo("/accounts/${account.id}/edit")

        input("#name", "Ray Construction Inc")
        select("#accountTypeId", 3)
        input("#phone", "5147580000")
        input("#mobile", "5147580011")
        input("#email", "info@ray-construction.com")
        scrollToMiddle()
        input("#website", "https://www.ray-construction.com")
        select2("#language", "French")
        input("#description", "This is the description")
        scrollToBottom()
        click(".btn-cancel")
        assertCurrentPageIs(PageName.ACCOUNT_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<UpdateAccountRequest>(),
            eq(Any::class.java)
        )

        navigateTo("/accounts/${account.id}/edit")

        input("#name", "Ray Construction Inc")
        select("#accountTypeId", 2)
        input("#phone", "5147580000")
        input("#mobile", "5147580011")
        input("#email", "info@ray-construction.com")
        scroll(0.25)
        input("#website", "https://www.ray-construction.com")
        select2("#language", "French")
        input("#description", "This is the description")
        select("#shippingCountry", 3)
        scroll(0.25)
        select2("#shippingCityId", "${locations[3].name}, ${locations[0].name}")
        input("#shippingStreet", "340 Pascal")
        input("#shippingPostalCode", "H0H 0H0")
        select("#billingSameAsShippingAddress", 0)
        scrollToBottom()
        attributes.forEach { attribute ->
            input("#attribute-${attribute.id}", "${attribute.id}2222")
        }
        click("button[type=submit]")

        assertCurrentPageIs(PageName.ACCOUNT_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/accounts/${account.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `edit - without permission account-manage`() {
        setupUserWithoutPermissions(listOf("account:manage"))

        navigateTo("/accounts/${account.id}/edit")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `edit - not manager`() {
        doReturn(
            ResponseEntity(
                GetAccountResponse(account.copy(managedById = -1)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAccountResponse::class.java)
            )

        navigateTo("/accounts/${account.id}/edit")

        assertCurrentPageIs(PageName.ERROR_403)
    }
}
