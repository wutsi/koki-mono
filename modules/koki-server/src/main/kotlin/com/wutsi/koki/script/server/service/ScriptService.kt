package com.wutsi.koki.script.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.script.dto.CreateScriptRequest
import com.wutsi.koki.script.dto.ScriptSortBy
import com.wutsi.koki.script.dto.UpdateScriptRequest
import com.wutsi.koki.script.server.dao.ScriptRepository
import com.wutsi.koki.script.server.domain.ScriptEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class ScriptService(
    private val dao: ScriptRepository,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): ScriptEntity {
        val script = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.SCRIPT_NOT_FOUND)) }

        if (script.tenantId != tenantId || script.deleted) {
            throw NotFoundException(Error(ErrorCode.SCRIPT_NOT_FOUND))
        }
        return script
    }

    fun getByName(name: String, tenantId: Long): ScriptEntity {
        return search(
            tenantId = tenantId,
            names = listOf(name),
            limit = 1,
        ).firstOrNull()
            ?: throw NotFoundException(Error(ErrorCode.SCRIPT_NOT_FOUND))
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: ScriptSortBy? = null,
        ascending: Boolean = true,
    ): List<ScriptEntity> {
        val jql = StringBuilder("SELECT S FROM ScriptEntity S")
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
                ScriptSortBy.NAME -> "name"
                ScriptSortBy.TITLE -> "title"
                ScriptSortBy.CREATED_AT -> "createdAt"
                ScriptSortBy.MODIFIED_AT -> "modifiedAt"
            }
            jql.append(" ORDER BY S.$column")
            if (!ascending) {
                jql.append(" DESC")
            }
        }

        val query = em.createQuery(jql.toString(), ScriptEntity::class.java)
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
    fun create(request: CreateScriptRequest, tenantId: Long): ScriptEntity {
        val script = dao.findByNameIgnoreCaseAndTenantId(request.name, tenantId)
        if (script != null) {
            throw ConflictException(
                error = Error(code = ErrorCode.SCRIPT_DUPLICATE_NAME)
            )
        }

        return dao.save(
            ScriptEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                name = request.name,
                title = request.title,
                description = request.description,
                active = request.active,
                language = request.language,
                code = request.code,
                parameters = toParameterString(request.parameters)
            )
        )
    }

    @Transactional
    fun update(id: String, request: UpdateScriptRequest, tenantId: Long) {
        val duplicate = dao.findByNameIgnoreCaseAndTenantId(request.name, tenantId)
        if (duplicate != null && duplicate.id != id) {
            throw ConflictException(
                error = Error(code = ErrorCode.SCRIPT_DUPLICATE_NAME)
            )
        }

        val script = duplicate ?: get(id, tenantId)
        script.name = request.name
        script.title = request.title
        script.description = request.description
        script.active = request.active
        script.language = request.language
        script.code = request.code
        script.parameters = toParameterString(request.parameters)
        dao.save(script)
    }

    @Transactional
    fun delete(id: String, tenantId: Long) {
        val script = get(id, tenantId)
        script.name = "##-" + script.name + "-" + UUID.randomUUID().toString()
        script.deleted = true
        script.deletedAt = Date()
        dao.save(script)
    }

    private fun toParameterString(parameters: List<String>): String? {
        return if (parameters.isEmpty()) {
            null
        } else {
            parameters
                .map { param -> param.trim() }
                .joinToString(separator = ",")
        }
    }
}
