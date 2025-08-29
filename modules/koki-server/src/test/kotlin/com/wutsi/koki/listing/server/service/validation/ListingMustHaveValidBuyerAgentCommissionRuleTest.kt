package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHaveValidBuyerAgentCommissionRuleTest {
    private val rule = ListingMustHaveValidBuyerAgentCommissionRule()

    @Test
    fun success() {
        rule.validate(ListingEntity(sellerAgentCommission = 10.0, buyerAgentCommission = 5.0))
    }

    @Test
    fun `no seller agent commission`() {
        rule.validate(ListingEntity(sellerAgentCommission = null, buyerAgentCommission = 5.0))
    }

    @Test
    fun `zero seller agent commission`() {
        rule.validate(ListingEntity(sellerAgentCommission = 0.0, buyerAgentCommission = 5.0))
    }

    @Test
    fun `invalid commission`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(ListingEntity(sellerAgentCommission = 10.0, buyerAgentCommission = 20.0))
        }
        assertEquals(ErrorCode.LISTING_INVALID_BUYER_COMMISSION, ex.message)
    }
}
