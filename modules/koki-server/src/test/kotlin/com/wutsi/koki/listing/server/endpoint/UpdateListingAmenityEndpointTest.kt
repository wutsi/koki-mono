package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.UpdateListingAmenitiesRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingAmenitiesEndpoint.sql"])
class UpdateListingAmenityEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Autowired
    private lateinit var ds: DataSource

    @Test
    fun update() {
        val id = 100L
        val request = UpdateListingAmenitiesRequest(
            furnitureType = FurnitureType.FULLY_FURNISHED,
            amenityIds = listOf(1101L, 1102L, 1103L)
        )
        val response = rest.postForEntity("/v1/listings/$id/amenities", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.furnitureType, listing.furnitureType)
        assertEquals(request.amenityIds.sorted(), getAmenityIds(id))
    }

    @Test
    fun `no amenities`() {
        val id = 101L
        val request = UpdateListingAmenitiesRequest(
            furnitureType = FurnitureType.UNFURNISHED,
            amenityIds = emptyList()
        )
        val response = rest.postForEntity("/v1/listings/$id/amenities", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.furnitureType, listing.furnitureType)
        assertEquals(true, getAmenityIds(id).isEmpty())
    }

    private fun getAmenityIds(listingId: Long): List<Long> {
        val result = mutableListOf<Long>()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT amenity_fk FROM T_LISTING_AMENITY where listing_fk=$listingId")
                rs.use {
                    while (rs.next()) {
                        result.add(rs.getLong(1))
                    }
                }
            }
        }
        return result.sorted()
    }
}
