package com.wutsi.koki.service.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.script.dto.ServiceSortBy
import com.wutsi.koki.service.dto.CreateServiceRequest
import com.wutsi.koki.service.dto.UpdateServiceRequest
import com.wutsi.koki.service.server.dao.ServiceRepository
import com.wutsi.koki.service.server.domain.ServiceEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class ServiceService(
    private val dao: ServiceRepository,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): ServiceEntity {
        val script = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.SERVICE_NOT_FOUND)) }

        if (script.tenantId != tenantId || script.deleted) {
            throw NotFoundException(Error(ErrorCode.SERVICE_NOT_FOUND))
        }
        return script
    }

    fun getByName(name: String, tenantId: Long): ServiceEntity {
        return search(tenantId = tenantId, names = listOf(name))
            .firstOrNull()
            ?: throw NotFoundException(Error(ErrorCode.SERVICE_NOT_FOUND))
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: ServiceSortBy? = null,
        ascending: Boolean = true,
    ): List<ServiceEntity> {
        val jql = StringBuilder("SELECT S FROM ServiceEntity S")
        jql.append(" WHERE S.deleted=false AND S.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND S.id IN :ids")
        }
        if (names.isNotEmpty()) {
            jql.append(" AND UPPER(S.name) IN :names")
        }
        if (active != null) {
            jql.append(" AND S.active = :active")
        }
        if (sortBy != null) {
            val column = when (sortBy) {
                ServiceSortBy.NAME -> "name"
                ServiceSortBy.TITLE -> "title"
                ServiceSortBy.CREATED_AT -> "createdAt"
                ServiceSortBy.MODIFIED_AT -> "modifiedAt"
            }
            jql.append(" ORDER BY S.$column")
            if (!ascending) {
                jql.append(" DESC")
            }
        }

        val query = em.createQuery(jql.toString(), ServiceEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (names.isNotEmpty()) {
            query.setParameter("names", names.map { name -> name.uppercase() })
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateServiceRequest, tenantId: Long): ServiceEntity {
        val duplicate = dao.findByNameIgnoreCaseAndTenantId(request.name, tenantId)
        if (duplicate != null) {
            throw ConflictException(
                error = Error(ErrorCode.SERVICE_DUPLICATE_NAME)
            )
        }

        return dao.save(
            ServiceEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                name = request.name,
                title = request.title,
                description = request.description,
                baseUrl = request.baseUrl,
                authenticationType = request.authenticationType,
                active = request.active,
                username = request.username,
                password = request.password,
                apiKey = request.apiKey,
            )
        )
    }

    @Transactional
    fun update(id: String, request: UpdateServiceRequest, tenantId: Long): ServiceEntity {
        val duplicate = dao.findByNameIgnoreCaseAndTenantId(request.name, tenantId)
        if (duplicate != null && duplicate.id != id) {
            throw ConflictException(
                error = Error(ErrorCode.SERVICE_DUPLICATE_NAME)
            )
        }

        val service = duplicate ?: get(id, tenantId)
        service.name = request.name
        service.title = request.title
        service.description = request.description
        service.baseUrl = request.baseUrl
        service.authenticationType = request.authenticationType
        service.active = request.active
        service.username = request.username
        service.password = request.password
        service.apiKey = request.apiKey
        service.modifiedAt = Date()

        return dao.save(service)
    }

    @Transactional
    fun delete(id: String, tenantId: Long) {
        val service = get(id, tenantId)
        service.name = "##-" + service.name + "-" + UUID.randomUUID().toString()
        service.deleted = true
        service.deletedAt = Date()
        dao.save(service)
    }
}
