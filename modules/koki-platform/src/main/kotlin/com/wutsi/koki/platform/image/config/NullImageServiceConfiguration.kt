package com.wutsi.koki.platform.core.image.config

import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.platform.image.NullImageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.image.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class NullImageServiceConfiguration {
    @Bean
    open fun imageService(): ImageService {
        return NullImageService()
    }
}
