package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHaveSellerAgentCommissionRuleTest {
    private val rule = ListingMustHaveSellerAgentCommissionRule()

    @Test
    fun success() {
        rule.validate(ListingEntity(sellerAgentCommission = 10.0))
    }

    @Test
    fun `zero commission`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(ListingEntity(sellerAgentCommission = 0.0))
        }
        assertEquals(ErrorCode.LISTING_MISSING_SELLER_COMMISSION, ex.message)
    }

    @Test
    fun `no commission`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(ListingEntity(sellerAgentCommission = null))
        }
        assertEquals(ErrorCode.LISTING_MISSING_SELLER_COMMISSION, ex.message)
    }
}
