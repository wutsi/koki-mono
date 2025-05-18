package com.wutsi.koki.room.server.config

import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.room.server.service.RoomPublisherValidator
import com.wutsi.koki.room.server.service.RoomUnitService
import com.wutsi.koki.room.server.service.validation.RoomMustHaveGeolocationRule
import com.wutsi.koki.room.server.service.validation.RoomMustHaveImageRule
import com.wutsi.koki.room.server.service.validation.RoomMustHavePriceRule
import com.wutsi.koki.room.server.service.validation.RoomMustHaveUnitRule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoomPublisherConfiguration(
    private val roomUnitService: RoomUnitService,
    private val fileService: FileService
) {
    @Bean
    fun roomPublisherValidator(): RoomPublisherValidator {
        return RoomPublisherValidator(
            listOf(
                RoomMustHavePriceRule(),
                RoomMustHaveGeolocationRule(),
                RoomMustHaveUnitRule(roomUnitService),
                RoomMustHaveImageRule(fileService)
            )
        )
    }
}
