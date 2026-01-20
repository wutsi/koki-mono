package com.wutsi.koki.webscraping.server.dao

import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WebpageRepository : JpaRepository<WebpageEntity, Long> {
    fun findByIdAndTenantId(id: Long, tenantId: Long): WebpageEntity?
    fun findByUrlHashAndTenantId(urlHash: String, tenantId: Long): WebpageEntity?
}
