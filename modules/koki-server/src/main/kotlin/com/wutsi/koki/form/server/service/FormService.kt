package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.form.server.dao.FormRepository
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.workflow.dto.FormSortBy
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class FormService(
    private val dao: FormRepository,
    private val em: EntityManager,
    private val objectMapper: ObjectMapper
) {
    fun get(id: String, tenantId: Long): FormEntity {
        val form = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_NOT_FOUND)) }

        if (form.tenantId != tenantId || form.deleted) {
            throw NotFoundException(Error(ErrorCode.FORM_NOT_FOUND))
        }
        return form
    }

    fun getByName(name: String, tenantId: Long): FormEntity {
        val form = dao.findByNameIgnoreCaseAndTenantId(name, tenantId)
            ?: throw NotFoundException(
                Error(
                    ErrorCode.FORM_NOT_FOUND,
                    parameter = Parameter(value = name),
                )
            )

        if (form.tenantId != tenantId || form.deleted) {
            throw NotFoundException(
                Error(
                    ErrorCode.FORM_NOT_FOUND,
                    parameter = Parameter(value = name),
                )
            )
        }
        return form
    }

    fun extractInputName(form: FormEntity): List<String> {
        val names = mutableListOf<String>()
        val content = objectMapper.readValue(form.content, FormContent::class.java)
        content.elements.forEach { elt -> extractInputName(elt, names) }
        return names
    }

    fun extractInputName(element: FormElement, names: MutableList<String>) {
        if (!element.name.isNullOrEmpty()) {
            names.add(element.name)
        }
        element.elements?.forEach { elt -> extractInputName(elt, names) }
    }

    fun search(
        tenantId: Long,
        ids: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
        sortBy: FormSortBy?,
        ascending: Boolean,
    ): List<FormEntity> {
        val jql = StringBuilder("SELECT F FROM FormEntity F")
        jql.append(" WHERE F.deleted=false AND F.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (active != null) {
            jql.append(" AND F.active = :active")
        }
        if (sortBy != null) {
            val column = when (sortBy) {
                FormSortBy.NAME -> "name"
                FormSortBy.TITLE -> "title"
                FormSortBy.CREATED_AT -> "createdAt"
                FormSortBy.MODIFIED_AT -> "modifiedAt"
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
    fun save(form: FormEntity, request: SaveFormRequest): FormEntity {
        val duplicate = dao.findByNameIgnoreCaseAndTenantId(request.content.name, form.tenantId)
        if (duplicate != null && duplicate.id != form.id) {
            throw ConflictException(
                error = Error(code = ErrorCode.FORM_DUPLICATE_NAME)
            )
        }

        form.name = request.content.name
        form.title = request.content.title
        form.description = request.content.description
        form.content = objectMapper.writeValueAsString(request.content)
        form.active = request.active
        form.modifiedAt = Date()
        return dao.save(form)
    }

    @Transactional
    fun delete(id: String, tenantId: Long) {
        val form = get(id, tenantId)
        form.name = "##-" + form.name + "-" + UUID.randomUUID().toString()
        form.deleted = true
        form.deletedAt = Date()
        dao.save(form)
    }
}
