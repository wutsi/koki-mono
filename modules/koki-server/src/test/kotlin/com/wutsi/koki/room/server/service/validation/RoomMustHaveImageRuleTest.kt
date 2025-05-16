package com.wutsi.koki.room.server.service.rule

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.server.domain.FileEntity
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
    private val room = RoomEntity(id = 1)

    @Test
    fun success() {
        doReturn(
            listOf(
                FileEntity(id = 1),
                FileEntity(id = 2)
            )
        ).whenever(fileService).search(
            any(),
            anyOrNull(),
            anyOrNull(),
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
            emptyList<FileEntity>()
        ).whenever(fileService).search(
            any(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        val ex = assertThrows<ValidationException> {
            rule.validate(room)
        }
        assertEquals(ErrorCode.ROOM_IMAGE_MISSING, ex.message)
    }
}
