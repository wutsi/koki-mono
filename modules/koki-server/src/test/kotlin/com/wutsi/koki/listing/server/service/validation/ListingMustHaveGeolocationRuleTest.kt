package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.room.server.domain.RoomEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomMustHaveGeolocationRuleTest {
    private val rule = RoomMustHaveGeolocationRule()

    @Test
    fun success() {
        rule.validate(RoomEntity(longitude = 1.0, latitude = 3.0))
    }

    @Test
    fun failure() {
        val ex = assertThrows<ValidationException> {
            rule.validate(RoomEntity())
        }
        assertEquals(ErrorCode.ROOM_GEOLOCATION_MISSING, ex.message)
    }
}
