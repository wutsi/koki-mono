package com.wutsi.koki.account.server.service

import com.wutsi.koki.account.server.dao.AccountTypeRepository
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.domain.AccountTypeEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class AccountTypeService(
    private val dao: AccountTypeRepository,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): AccountTypeEntity {
        val accountType = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.ACCOUNT_TYPE_NOT_FOUND)) }

        if (accountType.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.ACCOUNT_TYPE_NOT_FOUND))
        }
        return accountType
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<AccountTypeEntity> {
        val jql = StringBuilder("SELECT T FROM AccountTypeEntity AS T WHERE T.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND T.id IN :ids")
        }
        if (names.isNotEmpty()) {
            jql.append(" AND T.name IN :names")
        }
        if (active != null) {
            jql.append(" AND T.active = :active")
        }
        jql.append(" ORDER BY T.name")

        val query = em.createQuery(jql.toString(), AccountTypeEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (names.isNotEmpty()) {
            query.setParameter("names", names)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
