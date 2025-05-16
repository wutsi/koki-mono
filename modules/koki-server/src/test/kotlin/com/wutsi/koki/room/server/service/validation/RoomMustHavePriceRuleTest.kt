package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.room.server.domain.RoomEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomMustHavePriceRuleTest {
    private val rule = RoomMustHavePriceRule()

    @Test
    fun success() {
        rule.validate(RoomEntity(pricePerNight = 150000.0))
    }

    @Test
    fun noPrice() {
        val ex = assertThrows<ValidationException> {
            rule.validate(RoomEntity(pricePerNight = null))
        }
        assertEquals(ErrorCode.ROOM_PRICE_MISSING, ex.message)
    }

    @Test
    fun free() {
        val ex = assertThrows<ValidationException> {
            rule.validate(RoomEntity(pricePerNight = 0.0))
        }
        assertEquals(ErrorCode.ROOM_PRICE_MISSING, ex.message)
    }
}
