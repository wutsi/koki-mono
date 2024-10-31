package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.common.service.TenantIdProvider
import com.wutsi.platform.core.error.exception.BadRequestException
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class TenantIdProviderTest {
    private val request = mock<HttpServletRequest>()
    private val provider = TenantIdProvider(request)

    @Test
    fun get() {
        doReturn("1").whenever(request).getHeader(HttpHeader.TENANT_ID)

        val tenantId = provider.get()
        assertEquals(1L, tenantId)
    }

    @Test
    fun missingHeader() {
        doReturn(null).whenever(request).getHeader(HttpHeader.TENANT_ID)

        val ex = assertThrows<BadRequestException> {
            provider.get()
        }

        assertEquals(ErrorCode.TENANT_MISSING_FROM_HEADER, ex.error.code)
    }
}
