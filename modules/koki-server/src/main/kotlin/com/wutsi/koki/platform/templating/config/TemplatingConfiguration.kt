package com.wutsi.koki.platform.templating.config

import com.github.mustachejava.DefaultMustacheFactory
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.platform.templating.TemplatingEngine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TemplatingConfiguration {
    @Bean
    open fun templatingEngine(): TemplatingEngine {
        return MustacheTemplatingEngine(DefaultMustacheFactory())
    }
}
