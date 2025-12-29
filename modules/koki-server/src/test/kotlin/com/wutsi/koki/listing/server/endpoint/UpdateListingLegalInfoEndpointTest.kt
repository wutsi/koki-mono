package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.MutationType
import com.wutsi.koki.listing.dto.UpdateListingLegalInfoRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingLegalInfoEndpoint.sql"])
class UpdateListingLegalInfoEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun `should update all legal info fields`() {
        val id = 100L
        val request = UpdateListingLegalInfoRequest(
            landTitle = true,
            technicalFile = false,
            numberOfSigners = 2,
            mutationType = MutationType.TOTAL,
            transactionWithNotary = true
        )
        val response = rest.postForEntity("/v1/listings/$id/legal-info", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(true, listing.landTitle)
        assertEquals(false, listing.technicalFile)
        assertEquals(2, listing.numberOfSigners)
        assertEquals(MutationType.TOTAL, listing.mutationType)
        assertEquals(true, listing.transactionWithNotary)
    }

    @Test
    fun `should update partial legal info`() {
        val id = 100L
        val request = UpdateListingLegalInfoRequest(
            landTitle = true,
            technicalFile = null,
            numberOfSigners = null,
            mutationType = null,
            transactionWithNotary = null
        )
        val response = rest.postForEntity("/v1/listings/$id/legal-info", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(true, listing.landTitle)
    }

    @Test
    fun `should update with mutation type PARTIAL`() {
        val id = 100L
        val request = UpdateListingLegalInfoRequest(
            mutationType = MutationType.PARTIAL
        )
        val response = rest.postForEntity("/v1/listings/$id/legal-info", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(MutationType.PARTIAL, listing.mutationType)
    }

    @Test
    fun `should clear legal info when setting to null`() {
        val id = 100L
        // First set some values
        var request = UpdateListingLegalInfoRequest(
            landTitle = true,
            numberOfSigners = 3
        )
        rest.postForEntity("/v1/listings/$id/legal-info", request, Any::class.java)

        // Then clear them
        request = UpdateListingLegalInfoRequest(
            landTitle = null,
            numberOfSigners = null
        )
        val response = rest.postForEntity("/v1/listings/$id/legal-info", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertNull(listing.landTitle)
        assertNull(listing.numberOfSigners)
    }

    @Test
    fun `should return 404 for non-existent listing`() {
        val request = UpdateListingLegalInfoRequest(
            landTitle = true
        )
        val response = rest.postForEntity("/v1/listings/999999/legal-info", request, Any::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should validate number of signers is not negative`() {
        val id = 100L
        val request = UpdateListingLegalInfoRequest(
            numberOfSigners = -1
        )
        val response = rest.postForEntity("/v1/listings/$id/legal-info", request, Any::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should accept zero signers`() {
        val id = 100L
        val request = UpdateListingLegalInfoRequest(
            numberOfSigners = 0
        )
        val response = rest.postForEntity("/v1/listings/$id/legal-info", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(0, listing.numberOfSigners)
    }
}
