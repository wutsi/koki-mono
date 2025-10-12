package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.server.dao.AmenityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql"])
class ImportAmenityEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AmenityRepository

    @Test
    fun import() {
        rest.getForEntity("/v1/categories/import?type=AMENITY", ImportResponse::class.java) // Import categories first

        val response = rest.getForEntity("/v1/amenities/import", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(60, dao.findAll().toList().size)

        var amenity = dao.findById(1000).get()
        assertEquals(40000, amenity.categoryId)
        assertEquals(true, amenity.active)
        assertEquals("Electricity", amenity.name)
        assertEquals("Électricité", amenity.nameFr)
        assertEquals(false, amenity.top)

        amenity = dao.findById(1011).get()
        assertEquals(40001, amenity.categoryId)
        assertEquals(true, amenity.active)
        assertEquals("Stove", amenity.name)
        assertEquals("Cuisinière", amenity.nameFr)
        assertEquals(true, amenity.top)
    }
}
