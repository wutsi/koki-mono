package com.wutsi.koki.invoice.server.service

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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TenantInvoiceInitializerTest {
    @MockitoBean
    private lateinit var configurationService: ConfigurationService

    @Autowired
    private lateinit var initializer: TenantInvoiceInitializer

    private val tenantId = 111L

    @Test
    fun init() {
        doReturn(emptyList<ConfigurationEntity>())
            .whenever(configurationService)
            .search(any(), any(), anyOrNull())

        initializer.init(tenantId)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(configurationService, times(6)).save(request.capture(), eq(tenantId))

        assertNotNull(request.allValues[0].values[ConfigurationName.INVOICE_EMAIL_OPENED_ENABLED])
        assertNotNull(request.allValues[1].values[ConfigurationName.INVOICE_EMAIL_OPENED_SUBJECT])
        assertNotNull(request.allValues[2].values[ConfigurationName.INVOICE_EMAIL_OPENED_BODY])

        assertNotNull(request.allValues[3].values[ConfigurationName.INVOICE_EMAIL_PAID_ENABLED])
        assertNotNull(request.allValues[4].values[ConfigurationName.INVOICE_EMAIL_PAID_SUBJECT])
        assertNotNull(request.allValues[5].values[ConfigurationName.INVOICE_EMAIL_PAID_BODY])
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
