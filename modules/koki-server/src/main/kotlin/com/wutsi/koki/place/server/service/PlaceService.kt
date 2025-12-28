package com.wutsi.koki.place.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.platform.util.StringUtils.toAscii
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class PlaceService(
    private val dao: PlaceRepository,
    private val securityService: SecurityService,
    private val contentGeneratorFactory: ContentGeneratorAgentFactory,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): PlaceEntity {
        return dao.findByIdAndTenantIdAndDeleted(id, tenantId, false)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.PLACE_NOT_FOUND,
                    ),
                )
            }
    }

    fun search(
        tenantId: Long,
        neighbourhoodIds: List<Long>? = null,
        types: List<PlaceType>? = null,
        statuses: List<PlaceStatus>? = null,
        keyword: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<PlaceEntity> {
        val jql = StringBuilder("SELECT P FROM PlaceEntity P WHERE P.tenantId = :tenantId AND P.deleted=false")

        if (!neighbourhoodIds.isNullOrEmpty()) {
            jql.append(" AND P.neighbourhoodId IN :neighbourhoodIds")
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
        query.setParameter("tenantId", tenantId)

        if (!neighbourhoodIds.isNullOrEmpty()) {
            query.setParameter("neighbourhoodIds", neighbourhoodIds)
        }
        if (!types.isNullOrEmpty()) {
            query.setParameter("types", types)
        }
        if (!statuses.isNullOrEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (!keyword.isNullOrBlank()) {
            query.setParameter("keyword", "%${toAscii(keyword).uppercase()}%")
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreatePlaceRequest, tenantId: Long): PlaceEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val place = dao.save(
            PlaceEntity(
                tenantId = tenantId,
                createdById = userId,
                modifiedById = userId,
                name = request.name,
                type = request.type,
                neighbourhoodId = request.neighbourhoodId,
                status = PlaceStatus.PUBLISHING,
            )
        )
        val generator = contentGeneratorFactory.get(request.type)
        generator.generate(place)

        place.status = PlaceStatus.PUBLISHED
        return dao.save(place)
    }

    @Transactional
    fun update(id: Long, tenantId: Long): PlaceEntity {
        val place = get(id, tenantId)
        place.modifiedById = securityService.getCurrentUserIdOrNull()

        val generator = contentGeneratorFactory.get(place.type)
        generator.generate(place)
        return dao.save(place)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val entity = dao.findByIdAndTenantIdAndDeleted(id, tenantId, false)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.PLACE_NOT_FOUND,
                    ),
                )
            }

        entity.deleted = true
        entity.deletedAt = Date()
        entity.modifiedById = securityService.getCurrentUserIdOrNull()
        entity.modifiedAt = Date()
        dao.save(entity)
    }
}
