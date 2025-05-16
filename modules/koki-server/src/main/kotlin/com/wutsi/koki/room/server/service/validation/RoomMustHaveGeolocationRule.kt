package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRule
import jakarta.validation.ValidationException
import org.apache.tika.mime.MediaType.image
import org.springframework.stereotype.Service

@Service
class RoomMustLocationImageRule(private val fileService: FileService) : PublishRule {
    override fun validate(room: RoomEntity) {
        if (room.longitude == null || room.latitude == null){
            
        }
    }
}
