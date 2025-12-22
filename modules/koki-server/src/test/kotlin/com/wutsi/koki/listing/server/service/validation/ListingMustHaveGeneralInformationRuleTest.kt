package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHaveGeneralInformationRuleTest {
    private val rule = ListingMustHaveGeneralInformationRule()

    private val house = ListingEntity(bedrooms = 3, bathrooms = 3, propertyType = PropertyType.HOUSE)
    private val land = ListingEntity(lotArea = 1000, propertyType = PropertyType.LAND)

    @Test
    fun `house - success`() {
        rule.validate(house)
    }

    @Test
    fun `house - no bedroom`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(house.copy(bedrooms = null))
        }
        assertEquals(ErrorCode.LISTING_MISSING_GENERAL_INFORMATION_HOUSE, ex.message)
    }

    @Test
    fun `land - success`() {
        rule.validate(land)
    }

    @Test
    fun `land - no lot-area`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(land.copy(lotArea = null))
        }
        assertEquals(ErrorCode.LISTING_MISSING_GENERAL_INFORMATION_LAND, ex.message)
    }
}
