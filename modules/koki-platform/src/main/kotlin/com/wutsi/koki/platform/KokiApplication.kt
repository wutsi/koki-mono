package com.wutsi.koki.platform

import com.wutsi.koki.platform.ai.config.AIConfiguration
import com.wutsi.koki.platform.cache.config.LocalCacheConfiguration
import com.wutsi.koki.platform.cache.config.NoCacheConfiguration
import com.wutsi.koki.platform.cache.config.RedisCacheConfiguration
import com.wutsi.koki.platform.executor.config.ExecutorConfiguration
import com.wutsi.koki.platform.geoip.config.GeoIpConfiguration
import com.wutsi.koki.platform.logger.config.LoggerConfiguration
import com.wutsi.koki.platform.mq.config.RabbitMQConfiguration
import com.wutsi.koki.platform.storage.config.StorageConfiguration
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        AIConfiguration::class,

        LocalCacheConfiguration::class,
        NoCacheConfiguration::class,
        RedisCacheConfiguration::class,

        ExecutorConfiguration::class,

        GeoIpConfiguration::class,

        LoggerConfiguration::class,

        RabbitMQConfiguration::class,

        StorageConfiguration::class,
    ],
)
annotation class KokiApplication
