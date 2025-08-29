package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHavePriceRuleTest {
    private val rule = ListingMustHavePriceRule()

    @Test
    fun success() {
        rule.validate(ListingEntity(price = 300))
    }

    @Test
    fun free() {
        val ex = assertThrows<ValidationException> {
            rule.validate(ListingEntity(price = 0L))
        }
        assertEquals(ErrorCode.LISTING_MISSING_PRICE, ex.message)
    }

    @Test
    fun `no price`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(ListingEntity(price = null))
        }
        assertEquals(ErrorCode.LISTING_MISSING_PRICE, ex.message)
    }
}
