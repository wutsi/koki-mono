package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.CreateRoleResponse
import com.wutsi.koki.tenant.server.dao.ConfigurationRepository
import com.wutsi.koki.tenant.server.dao.RoleRepository
import com.wutsi.koki.tenant.server.service.TenantInitializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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

        assertEquals(3, names.size)
        assertTrue(names.contains(ConfigurationName.EMAIL_DECORATOR))
        assertTrue(names.contains(ConfigurationName.INVOICE_EMAIL_OPENED))
        assertTrue(names.contains(ConfigurationName.INVOICE_EMAIL_OPENED))
    }
}
