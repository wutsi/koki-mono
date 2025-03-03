package com.wutsi.koki.email.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TenantEmailInitializerTest {
    @MockitoBean
    private lateinit var configurationService: ConfigurationService

    @Autowired
    private lateinit var initializer: TenantEmailInitializer

    private val tenantId = 111L

    @Test
    fun init() {
        doReturn(emptyList<ConfigurationEntity>())
            .whenever(configurationService)
            .search(any(), any(), anyOrNull())

        initializer.init(tenantId)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(configurationService).save(request.capture(), eq(tenantId))
        assertNotNull(request.firstValue.values[ConfigurationName.EMAIL_DECORATOR])
    }

    @Test
    fun overwrite() {
        doReturn(listOf(ConfigurationEntity(name = "x", value = "y")))
            .whenever(configurationService)
            .search(any(), any(), anyOrNull())

        initializer.init(tenantId)

        verify(configurationService, never()).save(any(), any())
    }
}
