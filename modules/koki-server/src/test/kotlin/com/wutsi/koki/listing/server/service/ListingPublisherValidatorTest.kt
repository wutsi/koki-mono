package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.validation.ListingMustHaveAddressRule
import com.wutsi.koki.listing.server.service.validation.ListingMustHaveGeneralInformationRule
import com.wutsi.koki.listing.server.service.validation.ListingMustHaveImageApprovedRule
import com.wutsi.koki.listing.server.service.validation.ListingMustHavePriceRule
import com.wutsi.koki.listing.server.service.validation.ListingMustNotHaveImageUnderReviewRule
import jakarta.validation.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.Test

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ListingPublisherValidatorTest {
    @Autowired
    private lateinit var validator: ListingPublisherValidator

    @Test
    fun rules() {
        assertEquals(5, validator.rules.size)
        assertEquals(ListingMustHaveGeneralInformationRule::class, validator.rules[0]::class)
        assertEquals(ListingMustHaveAddressRule::class, validator.rules[1]::class)
        assertEquals(ListingMustHavePriceRule::class, validator.rules[2]::class)
        assertEquals(ListingMustHaveImageApprovedRule::class, validator.rules[3]::class)
        assertEquals(ListingMustNotHaveImageUnderReviewRule::class, validator.rules[4]::class)
    }

    @Test
    fun validate() {
        assertThrows<ValidationException> {
            validator.validate(ListingEntity())
        }
    }
}
