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
import com.wutsi.koki.email.server.service.TenantEmailInitializer
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
        verify(configurationService, times(3)).save(request.capture(), eq(tenantId))

        assertEquals(
            "1",
            request.allValues[0].values[ConfigurationName.INVOICE_EMAIL_ENABLED]
        )
        assertEquals(
            TenantInvoiceInitializer.EMAIL_SUBJECT,
            request.allValues[1].values[ConfigurationName.INVOICE_EMAIL_SUBJECT]
        )
        assertEquals(
            getContent(TenantInvoiceInitializer.EMAIL_BODY_PATH),
            request.allValues[2].values[ConfigurationName.INVOICE_EMAIL_BODY]
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
            TenantEmailInitializer::class.java.getResourceAsStream(path), "utf-8"
        )
    }
}
