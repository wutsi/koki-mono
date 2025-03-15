package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.dao.ConfigurationRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/InitTenantEndpointTest.sql"])
class InitTenantEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ConfigurationRepository

    @Test
    fun init() {
        val result = rest.postForEntity("/v1/tenants/1/init", emptyMap<String, Any>(), Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val names = dao.findByTenantId(1).map { config -> config.name }

        assertEquals(10, names.size)
        assertTrue(names.contains(ConfigurationName.EMAIL_DECORATOR))
        assertTrue(names.contains(ConfigurationName.SMTP_TYPE))

        assertTrue(names.contains(ConfigurationName.STORAGE_TYPE))

        assertTrue(names.contains(ConfigurationName.INVOICE_EMAIL_ENABLED))
        assertTrue(names.contains(ConfigurationName.INVOICE_EMAIL_SUBJECT))
        assertTrue(names.contains(ConfigurationName.INVOICE_EMAIL_BODY))

        assertTrue(names.contains(ConfigurationName.PAYMENT_METHOD_CASH_ENABLED))
        assertTrue(names.contains(ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED))
    }
}
