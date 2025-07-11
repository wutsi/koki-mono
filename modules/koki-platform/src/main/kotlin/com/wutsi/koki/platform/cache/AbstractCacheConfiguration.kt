package com.wutsi.koki.platform.cache

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean

abstract class AbstractCacheConfiguration(
    protected val name: String,
) {
    abstract fun cacheManager(): CacheManager

    @Bean
    open fun cache(): Cache =
        cacheManager().getCache(name)!!
}
