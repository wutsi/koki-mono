package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRule
import jakarta.validation.ValidationException

class RoomMustHaveImageRule(
    private val fileService: FileService,
    private val min: Int
) : PublishRule {
    override fun validate(room: RoomEntity) {
        val images = fileService.search(
            tenantId = room.tenantId,
            ownerId = room.id,
            ownerType = ObjectType.ROOM,
            type = FileType.IMAGE,
            status = FileStatus.APPROVED,
            limit = min
        )
        if (images.isEmpty()) {
            throw ValidationException(ErrorCode.ROOM_IMAGE_MISSING)
        } else if (images.size < min) {
            throw ValidationException(ErrorCode.ROOM_IMAGE_THRESHOLD)
        }
    }
}
