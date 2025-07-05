package com.wutsi.koki.tracking.server.config

import com.wutsi.koki.tracking.server.service.Pipeline
import com.wutsi.koki.tracking.server.service.filter.BotFilter
import com.wutsi.koki.tracking.server.service.filter.CountryFilter
import com.wutsi.koki.tracking.server.service.filter.DeviceTypeFilter
import com.wutsi.koki.tracking.server.service.filter.PersisterFilter
import com.wutsi.koki.tracking.server.service.filter.SourceFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipelineConfiguration(
    private val persisterFilter: PersisterFilter,
    private val botFilter: BotFilter,
    private val countryFilter: CountryFilter,
    private val deviceTypeFilter: DeviceTypeFilter,
    private val sourceFilter: SourceFilter,
) {
    @Bean
    fun pipiline(): Pipeline {
        return Pipeline(
            listOf(
                botFilter,
                countryFilter,
                deviceTypeFilter,
                sourceFilter,

                /* Must be the last filter */
                persisterFilter
            )
        )
    }
}
