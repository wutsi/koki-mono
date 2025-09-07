package com.wutsi.koki.contact.server.service

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.UpdateContactRequest
import com.wutsi.koki.contact.server.dao.ContactRepository
import com.wutsi.koki.contact.server.domain.ContactEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ContactService(
    private val dao: ContactRepository,
    private val em: EntityManager,
    private val securityService: SecurityService,
    private val accountService: AccountService,
) {
    fun get(id: Long, tenantId: Long): ContactEntity {
        val account = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.CONTACT_NOT_FOUND)) }

        if (account.tenantId != tenantId || account.deleted) {
            throw NotFoundException(Error(ErrorCode.CONTACT_NOT_FOUND))
        }
        return account
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        contactTypeIds: List<Long> = emptyList(),
        accountIds: List<Long> = emptyList(),
        createdByIds: List<Long> = emptyList(),
        accountManagerIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<ContactEntity> {
        val jql = StringBuilder("SELECT C FROM ContactEntity C WHERE C.deleted=false AND C.tenantId = :tenantId")
        if (keyword != null) {
            jql.append(" AND ( (UPPER(C.firstName) LIKE :keyword) OR (UPPER(C.lastName) LIKE :keyword) OR (UPPER(C.email) LIKE :keyword) )")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND C.id IN :ids")
        }
        if (contactTypeIds.isNotEmpty()) {
            jql.append(" AND C.contactTypeId IN :contactTypeIds")
        }
        if (accountIds.isNotEmpty()) {
            jql.append(" AND C.account.id IN :accountIds")
        }
        if (createdByIds.isNotEmpty()) {
            jql.append(" AND C.createdById IN :createdByIds")
        }
        if (accountManagerIds.isNotEmpty()) {
            jql.append(" AND C.account.managedById IN :accountManagerIds")
        }
        jql.append(" ORDER BY C.firstName, C.lastName")

        val query = em.createQuery(jql.toString(), ContactEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword != null) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (contactTypeIds.isNotEmpty()) {
            query.setParameter("contactTypeIds", contactTypeIds)
        }
        if (accountIds.isNotEmpty()) {
            query.setParameter("accountIds", accountIds)
        }
        if (createdByIds.isNotEmpty()) {
            query.setParameter("createdByIds", createdByIds)
        }
        if (accountManagerIds.isNotEmpty()) {
            query.setParameter("accountManagerIds", accountManagerIds)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateContactRequest, tenantId: Long): ContactEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        return dao.save(
            ContactEntity(
                tenantId = tenantId,
                account = request.accountId?.let { id -> accountService.get(id, tenantId) },
                contactTypeId = request.contactTypeId,
                firstName = request.firstName,
                lastName = request.lastName,
                gender = request.gender,
                language = request.language?.ifEmpty { null },
                salutation = request.salutations?.ifEmpty { null },
                phone = request.phone?.ifEmpty { null },
                email = request.email?.ifEmpty { null },
                mobile = request.mobile?.ifEmpty { null },
                profession = request.profession?.ifEmpty { null },
                employer = request.employer?.ifEmpty { null },
                createdById = userId,
                modifiedById = userId,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateContactRequest, tenantId: Long): ContactEntity {
        val contact = get(id, tenantId)
        contact.account = request.accountId?.let { id -> accountService.get(id, tenantId) }
        contact.contactTypeId = request.contactTypeId
        contact.firstName = request.firstName
        contact.lastName = request.lastName
        contact.gender = request.gender
        contact.language = request.language?.ifEmpty { null }
        contact.salutation = request.salutations?.ifEmpty { null }
        contact.phone = request.phone?.ifEmpty { null }
        contact.email = request.email?.ifEmpty { null }
        contact.mobile = request.mobile?.ifEmpty { null }
        contact.profession = request.profession?.ifEmpty { null }
        contact.employer = request.employer?.ifEmpty { null }
        contact.modifiedAt = Date()
        contact.modifiedById = securityService.getCurrentUserIdOrNull()
        return dao.save(contact)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val contact = get(id, tenantId)
        contact.deleted = true
        contact.deletedAt = Date()
        contact.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(contact)
    }
}
