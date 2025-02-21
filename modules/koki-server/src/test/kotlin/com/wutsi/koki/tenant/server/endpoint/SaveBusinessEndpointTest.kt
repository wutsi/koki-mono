package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tenant.dto.SaveBusinessRequest
import com.wutsi.koki.tenant.server.dao.BusinessRepository
import com.wutsi.koki.tenant.server.dao.BusinessTaxIdentifierRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SaveBusinessEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: BusinessRepository

    @Autowired
    private lateinit var taxIdDao: BusinessTaxIdentifierRepository

    @Autowired
    protected lateinit var ds: DataSource

    private val request = SaveBusinessRequest(
        companyName = "Company Inc",
        phone = "+5141110000",
        fax = "+5141110011",
        email = "info@ray-sponsible-inc.com",
        website = "https://www.ray-sponsible-inc.com",
        addressStreet = "340 Pascal",
        addressPostalCode = "123 111",
        addressCityId = 111L,
        addressStateId = 222L,
        addressCountry = "ca",
        taxIdentifiers = mapOf(
            1111L to "GST-00001430943",
            1112L to "PST-11004394090"
        ),
        juridictionIds = listOf(
            1010L,
            1011L,
        ),
    )

    private fun getTaxId(businessId: Long): List<Long> {
        val ids = mutableListOf<Long>()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery(
                    "SELECT juridiction_fk FROM T_BUSINESS_JURIDICTION where business_fk=$businessId"
                )
                rs.use {
                    while (rs.next()) {
                        ids.add(rs.getLong(1))
                    }
                }
            }
        }
        return ids
    }

    @Sql(value = ["/db/test/clean.sql", "/db/test/tenant/CreateBusinessEndpoint.sql"])
    @Test
    fun create() {
        val result = rest.postForEntity("/v1/businesses", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val business = dao.findByTenantId(TENANT_ID)!!
        assertEquals(TENANT_ID, business.tenantId)
        assertEquals(request.companyName, business.companyName)
        assertEquals(request.phone, business.phone)
        assertEquals(request.fax, business.fax)
        assertEquals(request.email, business.email)
        assertEquals(request.website, business.website)
        assertEquals(request.addressStreet, business.addressStreet)
        assertEquals(request.addressPostalCode, business.addressPostalCode)
        assertEquals(request.addressCountry?.uppercase(), business.addressCountry)
        assertEquals(request.addressCityId, business.addressCityId)
        assertEquals(request.addressStateId, business.addressStateId)
        assertEquals(USER_ID, business.createdById)
        assertEquals(USER_ID, business.modifiedById)

        val taxIds = taxIdDao.findByBusinessId(business.id)
        assertEquals(request.taxIdentifiers.size, taxIds.size)
        assertEquals(request.taxIdentifiers[taxIds[0].salesTaxId], taxIds[0].number)
        assertEquals(request.taxIdentifiers[taxIds[1].salesTaxId], taxIds[1].number)

        assertEquals(request.juridictionIds, getTaxId(business.id))
    }

    @Sql(value = ["/db/test/clean.sql", "/db/test/tenant/UpdateBusinessEndpoint.sql"])
    @Test
    fun update() {
        val result = rest.postForEntity("/v1/businesses", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val business = dao.findByTenantId(TENANT_ID)!!
        assertEquals(100L, business.id)
        assertEquals(TENANT_ID, business.tenantId)
        assertEquals(request.companyName, business.companyName)
        assertEquals(request.phone, business.phone)
        assertEquals(request.fax, business.fax)
        assertEquals(request.email, business.email)
        assertEquals(request.website, business.website)
        assertEquals(request.addressStreet, business.addressStreet)
        assertEquals(request.addressPostalCode, business.addressPostalCode)
        assertEquals(request.addressCountry?.uppercase(), business.addressCountry)
        assertEquals(request.addressCityId, business.addressCityId)
        assertEquals(request.addressStateId, business.addressStateId)
        assertEquals(USER_ID, business.modifiedById)

        val taxIds = taxIdDao.findByBusinessId(business.id)
        assertEquals(request.taxIdentifiers.size, taxIds.size)
        assertEquals(request.taxIdentifiers[taxIds[0].salesTaxId], taxIds[0].number)
        assertEquals(request.taxIdentifiers[taxIds[1].salesTaxId], taxIds[1].number)

        assertEquals(request.juridictionIds, getTaxId(business.id))
    }
}
