package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.refdata.server.dao.AmenityRepository
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AmenityService(
    private val dao: AmenityRepository,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AmenityService::class.java)
    }

    /**
     * In-memory cache of locations to reduce database hits.
     * TODO: This should be moved to a distributed cache like Redis.
     */
    @Suppress("ktlint:standard:backing-property-naming")
    private var __cache: MutableMap<Long, AmenityEntity>? = null

    fun all(): List<AmenityEntity> {
        return loadCache().values.toList()
    }

    fun get(id: Long): AmenityEntity {
        return getByIdOrNull(id)
            ?: throw NotFoundException(error = Error(ErrorCode.AMENITY_NOT_FOUND))
    }

    fun getByIdOrNull(id: Long): AmenityEntity? {
        return loadCache()[id]
    }

    @Transactional
    fun save(amenity: AmenityEntity): AmenityEntity {
        val saved = dao.save(amenity)
        cache(saved)
        return saved
    }

    fun search(
        ids: List<Long> = emptyList(),
        categoryId: Long? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<AmenityEntity> {
        return loadCache().values.filter {
            (ids.isNotEmpty() && ids.contains(it.id)) ||
                (categoryId != null && it.categoryId == categoryId) ||
                (active != null && it.active == active)
        }.drop(offset).take(limit)
    }

    private fun cacheKey(entity: AmenityEntity): Long {
        return entity.id
    }

    private fun cache(entity: AmenityEntity) {
        loadCache()[cacheKey(entity)] = entity
    }

    private fun loadCache(): MutableMap<Long, AmenityEntity> {
        if (__cache == null || __cache!!.isEmpty()) {
            __cache = dao.findAll().associateBy { entity -> cacheKey(entity) }.toMutableMap()
            LOGGER.info("${__cache?.size} amenities put the cache")
        }
        return __cache!!
    }

    private fun clearCache() {
        __cache = null
    }
}
