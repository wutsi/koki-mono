package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.telegram.tenant.service.TenantProviderImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class TenantProviderImplTest {
    val provider = TenantProviderImpl(111L)

    @Test
    fun get() {
        assertEquals(111L, provider.id())
    }
}
