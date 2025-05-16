package com.wutsi.koki.room.server.service.rule

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.validation.RoomMustHaveImageRule
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomMustHaveImageRuleTest {
    private val fileService = mock<FileService>()
    private val rule = RoomMustHaveImageRule(fileService)

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
