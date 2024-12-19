package com.wutsi.koki.service.server.service

import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.server.service.auth.ApiKeyAuthorizationHeader
import com.wutsi.koki.service.server.service.auth.BasicAuthorizationHeader
import com.wutsi.koki.service.server.service.auth.NoAuthorizationHeader
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorizationHeaderProviderTest {
    private val basic = mock<BasicAuthorizationHeader>()
    private val apiKey = mock<ApiKeyAuthorizationHeader>()
    private val none = mock<NoAuthorizationHeader>()
    private val provider = AuthorizationHeaderProvider(basic, apiKey, none)

    @Test
    fun `basic auth`() {
        assertEquals(basic, provider.get(AuthorizationType.BASIC))
    }

    @Test
    fun `api key auth`() {
        assertEquals(apiKey, provider.get(AuthorizationType.API_KEY))
    }

    @Test
    fun `no auth`() {
        assertEquals(none, provider.get(AuthorizationType.NONE))
        assertEquals(none, provider.get(AuthorizationType.UNKNOWN))
    }
}
