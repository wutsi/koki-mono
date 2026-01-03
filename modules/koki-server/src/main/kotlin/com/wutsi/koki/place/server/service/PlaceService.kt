package com.wutsi.koki.place.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class PlaceService(
    private val dao: PlaceRepository,
    private val securityService: SecurityService,
    private val contentGeneratorFactory: ContentGeneratorAgentFactory,
    private val em: EntityManager,
    private val locationService: LocationService,
) {
    fun get(id: Long): PlaceEntity {
        return dao.findByIdAndDeleted(id, false)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.PLACE_NOT_FOUND,
                    ),
                )
            }
    }

    fun search(
        neighbourhoodIds: List<Long>? = null,
        cityIds: List<Long>? = null,
        types: List<PlaceType>? = null,
        statuses: List<PlaceStatus>? = null,
        keyword: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<PlaceEntity> {
        val jql = StringBuilder("SELECT P FROM PlaceEntity P WHERE P.deleted=false")

        if (!neighbourhoodIds.isNullOrEmpty()) {
            jql.append(" AND P.neighbourhoodId IN :neighbourhoodIds")
        }
        if (!cityIds.isNullOrEmpty()) {
            jql.append(" AND P.cityId IN :cityIds")
        }
        if (!types.isNullOrEmpty()) {
            jql.append(" AND P.type IN :types")
        }
        if (!statuses.isNullOrEmpty()) {
            jql.append(" AND P.status IN :statuses")
        }
        if (!keyword.isNullOrBlank()) {
            jql.append(" AND UPPER(P.asciiName) LIKE :keyword")
        }

        jql.append(" ORDER BY P.name")

        val query = em.createQuery(jql.toString(), PlaceEntity::class.java)

        if (!neighbourhoodIds.isNullOrEmpty()) {
            query.setParameter("neighbourhoodIds", neighbourhoodIds)
        }
        if (!cityIds.isNullOrEmpty()) {
            query.setParameter("cityIds", cityIds)
        }
        if (!types.isNullOrEmpty()) {
            query.setParameter("types", types)
        }
        if (!statuses.isNullOrEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (!keyword.isNullOrBlank()) {
            val ascii = toAscii(keyword).uppercase()
            query.setParameter("keyword", "%$ascii%")
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreatePlaceRequest): PlaceEntity {
        // Ensure its unique
        val neighbourhood = locationService.get(request.neighbourhoodId, LocationType.NEIGHBORHOOD)
        val asciiName = toAscii(request.name)
        val duplicate = findPlace(
            name = request.name,
            cityId = neighbourhood.parentId ?: -1,
            type = request.type,
        )
        if (duplicate != null) {
            throw ConflictException(
                error = Error(code = ErrorCode.PLACE_DUPLICATE_NAME)
            )
        }

        // Create
        val city = locationService.get(neighbourhood.parentId ?: -1, LocationType.CITY)
        val userId = securityService.getCurrentUserIdOrNull()
        val place = dao.save(
            PlaceEntity(
                createdById = userId,
                modifiedById = userId,
                name = request.name,
                asciiName = asciiName,
                type = request.type,
                neighbourhoodId = request.neighbourhoodId,
                cityId = city.id ?: -1,
                status = if (request.generateContent) PlaceStatus.PUBLISHING else PlaceStatus.PUBLISHED,
            )
        )

        // Content
        if (request.generateContent) {
            val generator = contentGeneratorFactory.get(request.type)
            generator.generate(place, neighbourhood, city)

            place.status = PlaceStatus.PUBLISHED
            dao.save(place)
        }

        return place
    }

    @Transactional
    fun update(id: Long): PlaceEntity {
        val place = get(id)
        val neighbourhood = locationService.get(place.neighbourhoodId)
        val city = locationService.get(place.cityId)
        place.modifiedById = securityService.getCurrentUserIdOrNull()

        val generator = contentGeneratorFactory.get(place.type)
        generator.generate(place, neighbourhood, city)
        return dao.save(place)
    }

    @Transactional
    fun delete(id: Long) {
        val entity = dao.findByIdAndDeleted(id, false)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.PLACE_NOT_FOUND,
                    ),
                )
            }

        entity.name = "deleted-place-$id-" + UUID.randomUUID().toString()
        entity.asciiName = toAscii(entity.name)
        entity.deleted = true
        entity.deletedAt = Date()
        entity.modifiedById = securityService.getCurrentUserIdOrNull()
        entity.modifiedAt = Date()
        dao.save(entity)
    }

    @Transactional
    fun save(place: PlaceEntity): PlaceEntity {
        place.asciiName = toAscii(place.name)
        place.modifiedAt = Date()
        return dao.save(place)
    }

    /**
     * Find existing school by name and city, or return null if not found.
     * Used by SchoolImporter for idempotent school creation/updates.
     * Schools are uniquely identified by name + cityId + type=SCHOOL.
     *
     * @param name The school name (will be normalized to ASCII)
     * @param cityId The ID of the city where the school is located
     * @return Existing school entity or null if not found
     */
    fun findSchool(name: String, cityId: Long): PlaceEntity? {
        return findPlace(name, cityId, PlaceType.SCHOOL)
    }

    /**
     * Find existing place by name, city, and type, or return null if not found.
     * Used by importers for idempotent place creation/updates.
     * Places are uniquely identified by name + cityId + type.
     *
     * @param name The place name (will be normalized to ASCII)
     * @param cityId The ID of the city where the place is located
     * @param type The type of place
     * @return Existing place entity or null if not found
     */
    fun findPlace(name: String, cityId: Long, type: PlaceType): PlaceEntity? {
        return search(
            keyword = name,
            cityIds = listOf(cityId),
            types = listOf(type),
            limit = 1,
        ).firstOrNull()
    }

    /**
     * Create a new school entity without triggering content generation.
     * Used by SchoolImporter for bulk imports from CSV.
     * Schools are created with status=PUBLISHED and no AI content generation.
     *
     * @param name The school name
     * @param neighbourhoodId The ID of the neighbourhood where the school is located
     * @param cityId The ID of the city where the school is located
     * @return The created school entity
     */
    @Transactional
    fun createSchool(name: String, neighbourhoodId: Long, cityId: Long): PlaceEntity {
        return dao.save(
            PlaceEntity(
                name = name,
                asciiName = toAscii(name),
                type = PlaceType.SCHOOL,
                status = PlaceStatus.PUBLISHED,
                neighbourhoodId = neighbourhoodId,
                cityId = cityId,
            )
        )
    }

    /**
     * Create a new place entity without triggering content generation.
     * Used by importers for bulk imports from CSV.
     * Places are created with status=PUBLISHED and no AI content generation.
     *
     * @param name The place name
     * @param type The type of place
     * @param neighbourhoodId The ID of the neighbourhood where the place is located
     * @param cityId The ID of the city where the place is located
     * @return The created place entity
     */
    @Transactional
    fun createPlace(name: String, type: PlaceType, neighbourhoodId: Long, cityId: Long): PlaceEntity {
        return dao.save(
            PlaceEntity(
                name = name,
                asciiName = toAscii(name),
                type = type,
                status = PlaceStatus.PUBLISHED,
                neighbourhoodId = neighbourhoodId,
                cityId = cityId,
            )
        )
    }

    fun toAscii(name: String): String {
        return StringUtils.toAscii(name)
            .replace(" ", "-")
            .lowercase()
    }
}
