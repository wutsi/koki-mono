package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHaveGeolocationRuleTest {
    private val rule = ListingMustHaveGeolocationRule()

    @Test
    fun success() {
        rule.validate(ListingEntity(longitude = 1.0, latitude = 3.0))
    }

    @Test
    fun failure() {
        val ex = assertThrows<ValidationException> {
            rule.validate(ListingEntity())
        }
        assertEquals(ErrorCode.LISTING_MISSING_GEOLOCATION, ex.message)
    }
}
