package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.server.dao.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportCategoryEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: CategoryRepository

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun service() {
        val response = rest.getForEntity("/v1/categories/import?type=SERVICE", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = dao.findByType(CategoryType.SERVICE)
        assertEquals(138, categories.size)

        val category = dao.findById(10023L).get()
        assertEquals(10022L, category.parentId)
        assertEquals(2, category.level)
        assertEquals(true, category.active)
        assertEquals("Lawn Mowing", category.name)
        assertEquals("Home Services > Landscaping > Lawn Mowing", category.longName)
        assertEquals("Tonte de Pelouse", category.nameFr)
        assertEquals("Services à Domicile > Aménagement Paysager > Tonte de Pelouse", category.longNameFr)
    }

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun digital() {
        val response = rest.getForEntity("/v1/categories/import?type=DIGITAL", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = dao.findByType(CategoryType.DIGITAL)
        assertEquals(94, categories.size)

        val category = dao.findById(20084L).get()
        assertEquals(20079L, category.parentId)
        assertEquals(1, category.level)
        assertEquals(true, category.active)
        assertEquals("Research & Data", category.name)
        assertEquals("Educational & Informational > Research & Data", category.longName)
        assertEquals("Recherche et Données", category.nameFr)
        assertEquals("Éducatif et Informatif > Recherche et Données", category.longNameFr)
    }

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun physical() {
        val response = rest.getForEntity("/v1/categories/import?type=PHYSICAL", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = dao.findByType(CategoryType.PHYSICAL)
        assertEquals(96, categories.size)

        val category = dao.findById(30086L).get()
        assertEquals(30083L, category.parentId)
        assertEquals(2, category.level)
        assertEquals(true, category.active)
        assertEquals("Cabinets", category.name)
        assertEquals("Office Supplies > Office Furniture > Cabinets", category.longName)
    }

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun amenities() {
        val response = rest.getForEntity("/v1/categories/import?type=AMENITY", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = dao.findByType(CategoryType.AMENITY)
        assertEquals(8, categories.size)

        val category = dao.findById(40001).get()
        assertEquals(null, category.parentId)
        assertEquals(0, category.level)
        assertEquals(true, category.active)
        assertEquals("Kitchen & Dining", category.name)
        assertEquals("Kitchen & Dining", category.longName)
        assertEquals("Cuisine et salle à manger", category.nameFr)
        assertEquals("Cuisine et salle à manger", category.longNameFr)
    }

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun user() {
        val response = rest.getForEntity("/v1/categories/import?type=USER", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = dao.findByType(CategoryType.USER)
        assertEquals(3, categories.size)

        val category = dao.findById(50000).get()
        assertEquals(null, category.parentId)
        assertEquals(0, category.level)
        assertEquals(true, category.active)
        assertEquals("Real Estate Agent", category.name)
        assertEquals("Real Estate Agent", category.longName)
        assertEquals("Agent Immobilier", category.nameFr)
        assertEquals("Agent Immobilier", category.longNameFr)
    }

    @Test
    fun `bad type`() {
        val response = rest.getForEntity("/v1/categories/import?type=UNKNOWN", ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.CATEGORY_TYPE_NOT_SUPPORTED, response.body?.error?.code)
    }
}
