package com.wutsi.koki.platform.core.image.config

import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.platform.image.imagekit.ImageKitService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.image.type"],
    havingValue = "image-kit",
)
@ConfigurationProperties(prefix = "wutsi.platform.image.image-kit")
open class ImageKitConfiguration {
    var originUrl: String = ""
    var endpointUrl: String = ""

    @Bean
    open fun imageService(): ImageService {
        return ImageKitService(originUrl, endpointUrl)
    }
}
