package com.wutsi.koki.contact.server.service

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
            jql.append(" AND C.accountId IN :accountIds")
        }
        if (createdByIds.isNotEmpty()) {
            jql.append(" AND C.createdById IN :createdByIds")
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
                accountId = request.accountId,
                contactTypeId = request.contactTypeId,
                firstName = request.firstName,
                lastName = request.lastName,
                gender = request.gender,
                salutation = request.salutations,
                phone = request.phone,
                email = request.email,
                mobile = request.mobile,
                profession = request.profession,
                employer = request.employer,
                createdById = userId,
                modifiedById = userId,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateContactRequest, tenantId: Long): ContactEntity {
        val contact = get(id, tenantId)
        contact.accountId = request.accountId
        contact.contactTypeId = request.contactTypeId
        contact.firstName = request.firstName
        contact.lastName = request.lastName
        contact.gender = request.gender
        contact.salutation = request.salutations
        contact.phone = request.phone
        contact.email = request.email
        contact.mobile = request.mobile
        contact.profession = request.profession
        contact.employer = request.employer
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
