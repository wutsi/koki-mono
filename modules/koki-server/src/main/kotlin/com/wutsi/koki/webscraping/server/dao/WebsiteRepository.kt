package com.wutsi.koki.webscraping.server.dao

import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WebsiteRepository : JpaRepository<WebsiteEntity, Long> {
    fun findByIdAndTenantId(id: Long, tenantId: Long): WebsiteEntity?
    fun findByBaseUrlHashAndTenantId(baseUrlHash: String, tenantId: Long): WebsiteEntity?
}
