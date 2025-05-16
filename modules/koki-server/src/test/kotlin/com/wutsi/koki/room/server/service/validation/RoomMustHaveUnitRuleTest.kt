package com.wutsi.koki.room.server.service.validation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.domain.RoomUnitEntity
import com.wutsi.koki.room.server.service.RoomUnitService
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomMustHaveUnitRuleTest {
    private val unitService = mock<RoomUnitService>()
    private val rule = RoomMustHaveUnitRule(unitService)
    private val room = RoomEntity(id = 1)

    @Test
    fun success() {
        doReturn(
            listOf(
                RoomUnitEntity(id = 1),
                RoomUnitEntity(id = 2)
            )
        ).whenever(unitService).search(
            any(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
        rule.validate(room)
    }

    @Test
    fun failure() {
        doReturn(
            emptyList<RoomUnitEntity>()
        ).whenever(unitService).search(
            any(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        val ex = assertThrows<ValidationException> {
            rule.validate(room)
        }
        assertEquals(ErrorCode.ROOM_UNIT_MISSING, ex.message)
    }
}
