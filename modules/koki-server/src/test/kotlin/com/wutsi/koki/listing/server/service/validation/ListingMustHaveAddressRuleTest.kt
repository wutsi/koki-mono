package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHaveAddressRuleTest {
    private val rule = ListingMustHaveAddressRule()

    @Test
    fun success() {
        rule.validate(
            ListingEntity(
                country = "ca",
                cityId = 111L,
                neighbourhoodId = 222L,
                street = "3030 Pascal"
            )
        )
    }

    @Test
    fun `no country`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    country = null,
                    cityId = 111L,
                    neighbourhoodId = 222L,
                    street = "3030 Pascal"
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_ADDRESS, ex.message)
    }

    @Test
    fun `no city`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    country = "CA",
                    cityId = null,
                    neighbourhoodId = 222L,
                    street = "3030 Pascal"
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_ADDRESS, ex.message)
    }

    @Test
    fun `no neighborhood`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    country = "CA",
                    cityId = 111L,
                    neighbourhoodId = null,
                    street = "3030 Pascal"
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_ADDRESS, ex.message)
    }

    @Test
    fun `no street`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    country = "CA",
                    cityId = 111L,
                    neighbourhoodId = 222L,
                    street = ""
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_ADDRESS, ex.message)
    }
}
