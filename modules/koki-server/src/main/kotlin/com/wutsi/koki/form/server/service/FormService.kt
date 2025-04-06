package com.wutsi.koki.form.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.dao.FormOwnerRepository
import com.wutsi.koki.form.dto.CreateFormRequest
import com.wutsi.koki.form.dto.UpdateFormRequest
import com.wutsi.koki.form.server.dao.FormRepository
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.form.server.domain.FormOwnerEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class FormService(
    private val dao: FormRepository,
    private val ownerDao: FormOwnerRepository,
    private val em: EntityManager,
    private val securityService: SecurityService,
) {
    fun get(id: Long, tenantId: Long): FormEntity {
        val form = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_NOT_FOUND)) }

        if (form.tenantId != tenantId || form.deleted) {
            throw NotFoundException(Error(ErrorCode.FORM_NOT_FOUND))
        }
        return form
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        active: Boolean? = null,
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<FormEntity> {
        val jql = StringBuilder("SELECT F FROM FormEntity F")
        if (ownerId != null || ownerType != null) {
            jql.append(" JOIN F.formOwners AS O")
        }

        jql.append(" WHERE F.deleted=false AND F.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (active != null) {
            jql.append(" AND F.active = :active")
        }
        if (ownerId != null) {
            jql.append(" AND O.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND O.ownerType = :ownerType")
        }
        jql.append(" ORDER BY LOWER(F.name)")

        val query = em.createQuery(jql.toString(), FormEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        if (ownerId != null) {
            query.setParameter("ownerId", ownerId)
        }
        if (ownerType != null) {
            query.setParameter("ownerType", ownerType)
        }
        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateFormRequest, tenantId: Long): FormEntity {
        ensureCodeUnique(null, request.code, tenantId)

        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()

        val form = dao.save(
            FormEntity(
                tenantId = tenantId,
                code = request.code,
                name = request.name,
                description = request.description,
                active = request.active,
                createdAt = now,
                modifiedAt = now,
                createdById = userId,
                modifiedById = userId
            )
        )

        if (request.owner != null) {
            ownerDao.save(
                FormOwnerEntity(
                    formId = form.id!!,
                    ownerId = request.owner!!.id,
                    ownerType = request.owner!!.type
                )
            )
        }
        return form
    }

    @Transactional
    fun update(id: Long, request: UpdateFormRequest, tenantId: Long): FormEntity {
        ensureCodeUnique(id, request.code, tenantId)

        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()

        val form = get(id, tenantId)
        form.code = request.code
        form.name = request.name
        form.description = request.description
        form.active = request.active
        form.modifiedAt = now
        form.modifiedById = userId
        return dao.save(form)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val form = get(id, tenantId)
        form.deleted = true
        form.deletedAt = Date()
        form.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(form)
    }

    private fun ensureCodeUnique(id: Long?, code: String, tenantId: Long) {
        val form = dao.findByCodeAndTenantId(code, tenantId)
        if (form != null && form.id != id) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.FORM_DUPLICATE_CODE,
                    parameter = Parameter(value = code),
                )
            )
        }
    }
}
