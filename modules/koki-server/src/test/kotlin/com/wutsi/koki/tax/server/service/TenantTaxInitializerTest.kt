package com.wutsi.koki.tax.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.invoice.server.service.TenantTaxInitializer
import com.wutsi.koki.payment.server.service.TenantPaymentInitializer
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
class TenantTaxInitializerTest {
    @MockitoBean
    private lateinit var configurationService: ConfigurationService

    @Autowired
    private lateinit var initializer: TenantTaxInitializer

    private val tenantId = 111L

    @Test
    fun init() {
        doReturn(emptyList<ConfigurationEntity>())
            .whenever(configurationService)
            .search(any(), any(), anyOrNull())

        initializer.init(tenantId)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(configurationService, times(9)).save(request.capture(), eq(tenantId))

        assertEquals(
            "1",
            request.allValues[0].values[ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED]
        )
        assertEquals(
            TenantTaxInitializer.EMAIL_ASSIGNEE_SUBJECT,
            request.allValues[1].values[ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT]
        )
        assertEquals(
            getContent(TenantTaxInitializer.EMAIL_ASSIGNEE_BODY_PATH),
            request.allValues[2].values[ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY]
        )

        assertEquals(
            "1",
            request.allValues[3].values[ConfigurationName.TAX_EMAIL_DONE_ENABLED]
        )
        assertEquals(
            TenantTaxInitializer.EMAIL_DONE_SUBJECT,
            request.allValues[4].values[ConfigurationName.TAX_EMAIL_DONE_SUBJECT]
        )
        assertEquals(
            getContent(TenantTaxInitializer.EMAIL_DONE_BODY_PATH),
            request.allValues[5].values[ConfigurationName.TAX_EMAIL_DONE_BODY]
        )

        assertEquals(
            "1",
            request.allValues[6].values[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED]
        )
        assertEquals(
            TenantTaxInitializer.EMAIL_GATHERING_DOCUMENTS_SUBJECT,
            request.allValues[7].values[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT]
        )
        assertEquals(
            getContent(TenantTaxInitializer.EMAIL_GATHERING_DOCUMENTS_BODY_PATH),
            request.allValues[8].values[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY]
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
            TenantPaymentInitializer::class.java.getResourceAsStream(path), "utf-8"
        )
    }
}
