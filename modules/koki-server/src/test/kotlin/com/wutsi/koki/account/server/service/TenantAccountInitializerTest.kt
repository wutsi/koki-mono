package com.wutsi.koki.account.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TenantAccountInitializerTest {
    @MockitoBean
    private lateinit var configurationService: ConfigurationService

    @Autowired
    private lateinit var initializer: TenantAccountInitializer

    private val tenantId = 111L

    @Test
    fun init() {
        doReturn(emptyList<ConfigurationEntity>())
            .whenever(configurationService)
            .search(any(), any(), anyOrNull())

        initializer.init(tenantId)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(configurationService, times(2)).save(request.capture(), eq(tenantId))

        assertEquals(
            TenantAccountInitializer.INVITATION_EMAIL_SUBJECT,
            request.allValues[0].values[ConfigurationName.ACCOUNT_INVITATION_EMAIL_SUBJECT]
        )
        assertEquals(
            getContent(TenantAccountInitializer.INVITATION_EMAIL_BODY_PATH),
            request.allValues[1].values[ConfigurationName.ACCOUNT_INVITATION_EMAIL_BODY]
        )
    }

    @Test
    fun overwrite() {
        doReturn(listOf(ConfigurationEntity(name = "x", value = "y")))
            .whenever(configurationService)
            .search(any(), any(), anyOrNull())

        initializer.init(tenantId)

        verify(configurationService, never()).save(any(), any())
    }

    private fun getContent(path: String): String {
        return IOUtils.toString(
            TenantAccountInitializer::class.java.getResourceAsStream(path), "utf-8"
        )
    }
}
