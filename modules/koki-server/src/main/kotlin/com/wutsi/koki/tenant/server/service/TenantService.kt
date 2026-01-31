package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.dao.TenantRepository
import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val dao: TenantRepository
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TenantService::class.java)
    }

    /**
     * In-memory cache of locations to reduce database hits.
     * TODO: This should be moved to a distributed cache like Redis.
     */
    @Suppress("ktlint:standard:backing-property-naming")
    private var __cache: MutableMap<Long, TenantEntity>? = null

    fun get(id: Long): TenantEntity {
        return loadCache()[id] ?: throw NotFoundException(Error(ErrorCode.TENANT_NOT_FOUND))
    }

    fun all(): List<TenantEntity> {
        return loadCache().values.toList()
    }

    private fun cacheKey(entity: TenantEntity): Long {
        return entity.id ?: -1L
    }

    private fun loadCache(): MutableMap<Long, TenantEntity> {
        if (__cache == null || __cache!!.isEmpty()) {
            __cache = dao.findAll().associateBy { entity -> cacheKey(entity) }.toMutableMap()
            LOGGER.info("${__cache?.size} tenants put the cache")
        }
        return __cache!!
    }
}
