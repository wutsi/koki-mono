package com.wutsi.koki.portal.room.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.room.model.RoomUnitModel
import com.wutsi.koki.room.dto.RoomUnit
import com.wutsi.koki.room.dto.RoomUnitSummary
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class RoomUnitMapper(private val messages: MessageSource) : TenantAwareMapper() {
    fun toRoomUnitModel(entity: RoomUnit): RoomUnitModel {
        val fmt = createDateTimeFormat()
        return RoomUnitModel(
            id = entity.id,
            status = entity.status,
            floor = entity.floor,
            floorText = toFloorText(entity.floor),
            number = entity.number,
            roomId = entity.roomId,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toRoomUnitModel(entity: RoomUnitSummary): RoomUnitModel {
        return RoomUnitModel(
            id = entity.id,
            status = entity.status,
            floor = entity.floor,
            floorText = toFloorText(entity.floor),
            number = entity.number,
            roomId = entity.roomId,
        )
    }

    fun toFloorText(i: Int): String {
        val locale = LocaleContextHolder.getLocale()
        return if (i <= 3) {
            messages.getMessage("floor-$i", emptyArray(), locale)
        } else {
            messages.getMessage("floor-n", arrayOf(i), locale)
        }
    }
}
