package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHaveSellerRuleTest {
    private val rule = ListingMustHaveSellerRule()

    @Test
    fun success() {
        rule.validate(
            ListingEntity(
                sellerContactId = 111L
            )
        )
    }

    @Test
    fun failure() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    sellerContactId = null
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_SELLER, ex.message)
    }
}
