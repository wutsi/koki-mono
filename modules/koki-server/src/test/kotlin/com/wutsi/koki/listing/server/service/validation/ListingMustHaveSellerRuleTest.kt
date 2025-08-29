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
                sellerName = "Ray Sponsible",
                sellerEmail = "ray.sponsible@gmail.com",
                sellerPhone = "+15147580000"
            )
        )
    }

    @Test
    fun `no name`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    sellerName = "",
                    sellerEmail = "ray.sponsible@gmail.com",
                    sellerPhone = "+15147580000"
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_SELLER, ex.message)
    }

    @Test
    fun `no email`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    sellerName = "Ray Sponsible",
                    sellerEmail = null,
                    sellerPhone = "+15147580000"
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_SELLER, ex.message)
    }

    @Test
    fun `no phone`() {
        val ex = assertThrows<ValidationException> {
            rule.validate(
                ListingEntity(
                    sellerName = "Ray Sponsible",
                    sellerEmail = "ray.sponsible@gmail.com",
                    sellerPhone = null
                )
            )
        }
        assertEquals(ErrorCode.LISTING_MISSING_SELLER, ex.message)
    }
}
