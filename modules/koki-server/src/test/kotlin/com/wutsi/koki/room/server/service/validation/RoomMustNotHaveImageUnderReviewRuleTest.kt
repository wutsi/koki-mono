package com.wutsi.koki.room.server.service.validation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.room.server.domain.RoomEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomMustNotHaveImageUnderReviewRuleTest {
    private val fileService = mock<FileService>()
    private val rule = RoomMustNotHaveImageUnderReviewRule(fileService)
    private val room = RoomEntity(id = 1, tenantId = 11L)

    @Test
    fun success() {
        doReturn(
            emptyList<FileEntity>()
        ).whenever(fileService).search(
            room.tenantId,
            emptyList(), // ids
            room.id, // ownerId
            ObjectType.ROOM, // ownerType
            FileType.IMAGE, // fileType
            FileStatus.UNDER_REVIEW, // fileStatus
            1, // limit
            0, // offset
        )
        rule.validate(room)
    }

    @Test
    fun failure() {
        doReturn(
            listOf(FileEntity())
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
        assertEquals(ErrorCode.ROOM_IMAGE_UNDER_REVIEW, ex.message)
    }
}
