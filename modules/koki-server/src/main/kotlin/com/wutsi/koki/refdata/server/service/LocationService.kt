package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.dao.LocationRepository
import com.wutsi.koki.refdata.server.domain.LocationEntity
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.text.Normalizer

@Service
class LocationService(
    private val dao: LocationRepository,
) {
    companion object {
        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        private val LOGGER = LoggerFactory.getLogger(LocationService::class.java)
    }

    /**
     * In-memory cache of locations to reduce database hits.
     * TODO: This should be moved to a distributed cache like Redis.
     */
    @Suppress("ktlint:standard:backing-property-naming")
    private var __cache: MutableMap<Long, LocationEntity>? = null

    @PostConstruct
    fun init() {
        clearCache()
    }

    fun get(id: Long): LocationEntity {
        return getOrNull(id)
            ?: throw NotFoundException(error = Error(ErrorCode.LOCATION_NOT_FOUND, parameter = Parameter(value = id)))
    }

    fun getOrNull(id: Long): LocationEntity? {
        return loadCache().get(id)
    }

    fun get(id: Long, type: LocationType): LocationEntity {
        val location = get(id)
        if (location.type != type) {
            throw NotFoundException(error = Error(ErrorCode.LOCATION_NOT_FOUND, parameter = Parameter(value = id)))
        }
        return location
    }

    @Transactional
    fun save(location: LocationEntity): LocationEntity {
        location.asciiName = toAscii(location.name)
        val saved = dao.save(location)

        cache(saved)
        return saved
    }

    @Transactional
    fun link(parentId: Long, childId: Long): Boolean {
        val child = getOrNull(childId) ?: return false
        val parent = getOrNull(parentId) ?: return false

        child.parentId = parent.id
        dao.save(child)
        return true
    }

    fun search(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        parentId: Long? = null,
        types: List<LocationType> = emptyList(),
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<LocationEntity> {
        return loadCache().values.filter { location ->
            (keyword.isNullOrEmpty() || location.asciiName.uppercase().startsWith(toAscii(keyword).uppercase())) &&
                (ids.isEmpty() || ids.contains(location.id)) &&
                (parentId == null || location.parentId == parentId) &&
                (types.isEmpty() || types.contains(location.type)) &&
                (country.isNullOrEmpty() || location.country.equals(country, ignoreCase = true))
        }
            .sortedWith(
                compareBy<LocationEntity> { it.name }
                    .thenByDescending { it.population }
            )
            .drop(offset)
            .take(limit)
    }

    fun toAscii(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
            .replace(" ", "-")
    }

    private fun cacheKey(entity: LocationEntity): Long {
        return entity.id ?: -1L
    }

    private fun cache(entity: LocationEntity) {
        loadCache()[cacheKey(entity)] = entity
    }

    private fun loadCache(): MutableMap<Long, LocationEntity> {
        if (__cache == null || __cache!!.isEmpty()) {
            __cache = dao.findAll().associateBy { entity -> entity.id ?: -1 }.toMutableMap()
            LOGGER.info("${__cache?.size} locations put the cache")
        }
        return __cache!!
    }

    private fun clearCache() {
        __cache = null
    }
}
