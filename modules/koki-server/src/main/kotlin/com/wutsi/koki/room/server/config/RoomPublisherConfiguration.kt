package com.wutsi.koki.room.server.config

import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.room.server.service.RoomPublisherValidator
import com.wutsi.koki.room.server.service.validation.RoomMustHaveGeolocationRule
import com.wutsi.koki.room.server.service.validation.RoomMustHaveImageRule
import com.wutsi.koki.room.server.service.validation.RoomMustHavePriceRule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoomPublisherConfiguration(
    private val fileService: FileService
) {
    @Bean
    fun roomPublisherValidator(): RoomPublisherValidator {
        return RoomPublisherValidator(
            listOf(
                RoomMustHavePriceRule(),
                RoomMustHaveGeolocationRule(),
                RoomMustHaveImageRule(fileService)
            )
        )
    }
}
