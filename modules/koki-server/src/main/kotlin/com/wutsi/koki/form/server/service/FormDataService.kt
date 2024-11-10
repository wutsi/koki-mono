package com.wutsi.koki.form.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.server.dao.FormRepository
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.workflow.dto.FormSortBy
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class FormService(
    private val dao: FormRepository,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): FormEntity {
        val form = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_NOT_FOUND)) }

        if (form.tenant.id != tenantId) {
            throw NotFoundException(Error(ErrorCode.FORM_NOT_FOUND))
        }
        return form
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: FormSortBy? = null,
        ascending: Boolean = true,
    ): List<FormEntity> {
        val jql = StringBuilder("SELECT F FROM FormEntity F")
        jql.append(" WHERE F.tenant.id = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (active != null) {
            jql.append(" AND F.active = :active")
        }
        if (sortBy != null) {
            val column = when (sortBy) {
                FormSortBy.TITLE -> "title"
                FormSortBy.CREATED_AT -> "createdAt"
            }
            jql.append(" ORDER BY F.$column")
            if (!ascending) {
                jql.append(" DESC")
            }
        }

        val query = em.createQuery(jql.toString(), FormEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun save(form: FormEntity): FormEntity {
        form.modifiedAt = Date()
        return dao.save(form)
    }
}
