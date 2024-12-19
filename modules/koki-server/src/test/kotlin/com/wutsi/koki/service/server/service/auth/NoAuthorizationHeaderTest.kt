package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertNull

class NoAuthorizationHeaderTest {
    val provider = NoAuthorizationHeader()

    @Test
    fun value() {
        assertNull(provider.value(mock<ServiceEntity>()))
    }
}
