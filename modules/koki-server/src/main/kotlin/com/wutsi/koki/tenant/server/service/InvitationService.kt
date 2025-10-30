package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.CreateInvitationRequest
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import com.wutsi.koki.tenant.server.dao.InvitationRepository
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Collections.emptyList
import java.util.Date
import java.util.UUID

@Service
class InvitationService(
    private val dao: InvitationRepository,
    private val userDao: UserRepository,
    private val securityService: SecurityService,
    private val configurationService: ConfigurationService,
    private val em: EntityManager,
    @Value("\${koki.module.invitation.ttl-days}") private val ttlDays: Int,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(InvitationService::class.java)
    }

    @Transactional
    fun create(request: CreateInvitationRequest, tenantId: Long): InvitationEntity {
        val email = request.email.lowercase()
        if (userDao.findByEmailAndTenantId(email, tenantId) != null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.INVITATION_ALREADY_USER
                )
            )
        }
        if (dao.findByStatusAndDeletedAndEmail(InvitationStatus.PENDING, false, email).isNotEmpty()) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.INVITATION_ALREADY_INVITED
                )
            )
        }

        val now = Date()
        return dao.save(
            InvitationEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                email = email,
                displayName = request.displayName,
                createdById = securityService.getCurrentUserIdOrNull(),
                createdAt = now,
                expiresAt = DateUtils.addDays(now, ttlDays),
                status = InvitationStatus.PENDING,
                type = request.type,
                language = request.language,
            )
        )
    }

    @Transactional
    fun delete(id: String, tenantId: Long) {
        val invitation = get(id, tenantId)
        invitation.deleted = true
        invitation.deletedAt = Date()
        invitation.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(invitation)
    }

    @Transactional
    fun expire(id: String, tenantId: Long) {
        val invitation = get(id, tenantId)
        status(invitation, InvitationStatus.EXPIRED)
        dao.save(invitation)
    }

    @Transactional
    fun status(invitation: InvitationEntity, status: InvitationStatus) {
        invitation.status = status
        if (status == InvitationStatus.EXPIRED) {
            invitation.expiresAt = Date()
        } else if (status == InvitationStatus.ACCEPTED) {
            invitation.acceptedAt = Date()
        }
        dao.save(invitation)
    }

    fun get(id: String, tenantId: Long): InvitationEntity {
        val role = dao.findById(id)
            .orElseThrow { NotFoundException(Error(code = ErrorCode.INVITATION_NOT_FOUND)) }

        if (role.tenantId != tenantId || role.deleted) {
            throw NotFoundException(Error(code = ErrorCode.INVITATION_NOT_FOUND))
        }
        return role
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        statuses: List<InvitationStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<InvitationEntity> {
        val jql = StringBuilder("SELECT U FROM InvitationEntity U WHERE U.deleted=false AND U.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND U.id IN :ids")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND U.status IN :statuses")
        }
        jql.append(" ORDER BY U.displayName")

        val query = em.createQuery(jql.toString(), InvitationEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun getRoleId(invitation: InvitationEntity): Long? {
        if (invitation.type == InvitationType.AGENT) {
            val config = configurationService.search(
                tenantId = invitation.tenantId,
                names = listOf(ConfigurationName.ROLE_ID_AGENT),
            ).firstOrNull()
            val roleId = config?.value
            try {
                return roleId?.toLong()
            } catch (ex: Exception) {
                LOGGER.warn("Invalid configuration: " + ConfigurationName.ROLE_ID_AGENT + "=$roleId", ex)
            }
        }

        return null
    }

    fun searchNotExpired(date: Date): List<InvitationEntity> {
        return dao.findByStatusAndDeletedAndExpiresAtIsLessThan(InvitationStatus.PENDING, false, date)
    }
}
