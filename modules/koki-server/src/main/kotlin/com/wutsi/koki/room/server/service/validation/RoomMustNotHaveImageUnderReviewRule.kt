package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRule
import jakarta.validation.ValidationException

class RoomMustNotHaveImageUnderReviewRule(
    private val fileService: FileService
) : PublishRule {
    override fun validate(room: RoomEntity) {
        val images = fileService.search(
            tenantId = room.tenantId,
            ownerId = room.id,
            ownerType = ObjectType.ROOM,
            type = FileType.IMAGE,
            status = FileStatus.UNDER_REVIEW,
            limit = 1
        )
        if (images.isNotEmpty()) {
            throw ValidationException(ErrorCode.ROOM_IMAGE_UNDER_REVIEW)
        }
    }
}
