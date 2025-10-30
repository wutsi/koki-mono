package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.SendUsernameRequest
import com.wutsi.koki.tenant.dto.UpdateUserPhotoRequest
import com.wutsi.koki.tenant.dto.UpdateUserProfileRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.command.SendUsernameCommand
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class UserService(
    private val dao: UserRepository,
    private val passwordEncryptor: PasswordEncryptor,
    private val roleService: RoleService,
    private val securityService: SecurityService,
    private val invitationService: InvitationService,
    private val em: EntityManager,
    private val publisher: Publisher,
) {
    fun get(id: Long, tenantId: Long): UserEntity {
        val user = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.USER_NOT_FOUND)) }

        if (user.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.USER_NOT_FOUND))
        }
        return user
    }

    fun getByEmail(email: String, tenantId: Long): UserEntity {
        return dao.findByEmailAndTenantId(email.lowercase(), tenantId)
            ?: throw NotFoundException(Error(ErrorCode.USER_NOT_FOUND))
    }

    @Transactional
    fun create(request: CreateUserRequest, tenantId: Long): UserEntity {
        checkDuplicateUsername(null, request.username, tenantId)
        checkDuplicateEmail(null, request.email, tenantId)

        /* Create user */
        val salt = UUID.randomUUID().toString()
        val currentUserId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val user = dao.save(
            UserEntity(
                tenantId = tenantId,
                username = request.username.lowercase(),
                email = request.email?.lowercase()?.ifEmpty { null },
                displayName = request.displayName?.ifEmpty { null },
                status = UserStatus.ACTIVE,
                salt = salt,
                password = passwordEncryptor.hash(request.password, salt),
                createdById = currentUserId,
                modifiedById = currentUserId,
                language = request.language?.lowercase()?.ifEmpty { null },
                employer = request.employer?.uppercase()?.ifEmpty { null },
                categoryId = request.categoryId,
                country = request.country?.lowercase()?.ifEmpty { null },
                cityId = request.cityId,
                mobile = request.mobile,
                invitationId = request.invitationId,
                createdAt = now,
                modifiedAt = now,
            )
        )

        /* Assign roles */
        val invitation = request.invitationId?.let { invitationId -> getPendingInvitation(invitationId, tenantId) }
        val invitationRoleId = invitation?.let { invitationService.getRoleId(invitation) }
        val roleIds = request.roleIds.toMutableList()
        if (invitationRoleId != null) {
            roleIds.add(invitationRoleId)
        }
        setRoles(user, roleIds.distinct())

        /* Update the invitation status */
        if (invitation != null) {
            invitationService.status(invitation, InvitationStatus.ACCEPTED)
        }
        return user
    }

    private fun getPendingInvitation(invitationId: String, tenantId: Long): InvitationEntity {
        val invitation = invitationService.get(invitationId, tenantId)
        if (invitation.status == InvitationStatus.PENDING) {
            return invitation
        } else if (invitation.status == InvitationStatus.EXPIRED) {
            throw ConflictException(
                error = Error(ErrorCode.INVITATION_EXPIRED)
            )
        } else if (invitation.status == InvitationStatus.ACCEPTED) {
            throw ConflictException(
                error = Error(ErrorCode.INVITATION_ALREADY_ACCEPTED)
            )
        } else {
            throw ConflictException(
                error = Error(ErrorCode.INVITATION_BAD_STATUS)
            )
        }
    }

    @Transactional
    fun update(id: Long, request: UpdateUserRequest, tenantId: Long) {
        checkDuplicateEmail(id, request.email, tenantId)

        val user = get(id, tenantId)
        user.email = request.email?.lowercase()?.ifEmpty { null }
        user.displayName = request.displayName?.ifEmpty { null }
        user.language = request.language?.lowercase()?.ifEmpty { null }
        user.mobile = request.mobile?.ifEmpty { null }
        user.categoryId = request.categoryId
        user.employer = request.employer?.uppercase()?.ifEmpty { null }
        user.modifiedById = securityService.getCurrentUserIdOrNull()
        user.country = request.country?.lowercase()?.ifEmpty { null }
        user.cityId = request.cityId
        user.mobile = request.mobile
        user.modifiedAt = Date()
        request.roleIds?.let { roleIds -> setRoles(user, roleIds.distinct()) }
    }

    @Transactional
    fun updateProfile(id: Long, request: UpdateUserProfileRequest, tenantId: Long) {
        checkDuplicateEmail(id, request.email, tenantId)

        val user = get(id, tenantId)
        user.email = request.email?.lowercase()?.ifEmpty { null }
        user.displayName = request.displayName?.ifEmpty { null }
        user.language = request.language?.lowercase()?.ifEmpty { null }
        user.mobile = request.mobile?.ifEmpty { null }
        user.categoryId = request.categoryId
        user.employer = request.employer?.uppercase()?.ifEmpty { null }
        user.modifiedById = securityService.getCurrentUserIdOrNull()
        user.country = request.country?.lowercase()?.ifEmpty { null }
        user.cityId = request.cityId
        user.mobile = request.mobile
        user.biography = request.biography
        user.websiteUrl = request.websiteUrl
        user.facebookUrl = request.facebookUrl
        user.instagramUrl = request.instagramUrl
        user.youtubeUrl = request.youtubeUrl
        user.tiktokUrl = request.tiktokUrl
        user.twitterUrl = request.twitterUrl
        user.modifiedAt = Date()
    }

    @Transactional
    fun updatePassword(user: UserEntity, password: String) {
        user.salt = UUID.randomUUID().toString()
        user.password = passwordEncryptor.hash(password, user.salt)
        dao.save(user)
    }

    @Transactional
    fun updatePhoto(id: Long, request: UpdateUserPhotoRequest, tenantId: Long) {
        val user = get(id, tenantId)
        user.photoUrl = request.photoUrl?.ifEmpty { null }
        dao.save(user)
    }

    @Transactional
    fun save(user: UserEntity) {
        dao.save(user)
    }

    fun sendUsername(request: SendUsernameRequest, tenantId: Long) {
        val user = getByEmail(request.email, tenantId)
        publisher.publish(
            SendUsernameCommand(
                userId = user.id ?: -1,
                tenantId = user.tenantId,
            )
        )
    }

    private fun checkDuplicateUsername(id: Long?, username: String, tenantId: Long) {
        val duplicate = dao.findByUsernameAndTenantId(username.lowercase(), tenantId)
        if (duplicate != null && duplicate.id != id) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.USER_DUPLICATE_USERNAME
                )
            )
        }
    }

    private fun checkDuplicateEmail(id: Long?, email: String?, tenantId: Long) {
        if (email.isNullOrEmpty()) {
            return
        }

        val duplicate = dao.findByEmailAndTenantId(email.lowercase(), tenantId)
        if (duplicate != null && duplicate.id != id) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.USER_DUPLICATE_EMAIL
                )
            )
        }
    }

    private fun setRoles(user: UserEntity, roleIds: List<Long>) {
        if (roleIds.isEmpty()) {
            user.roles.clear()
        } else {
            user.roles = roleService.search(
                tenantId = user.tenantId,
                ids = roleIds,
                limit = roleIds.size,
            ).toMutableList()
        }
        user.modifiedById = securityService.getCurrentUserIdOrNull()
        user.modifiedAt = Date()
        dao.save(user)
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        status: UserStatus? = null,
        permissions: List<String> = emptyList(),
        username: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<UserEntity> {
        val jql = StringBuilder("SELECT U FROM UserEntity U")
        if (roleIds.isNotEmpty() || permissions.isNotEmpty()) {
            jql.append(" JOIN U.roles R")
        }
        if (permissions.isNotEmpty()) {
            jql.append(" JOIN R.permissions P")
        }

        jql.append(" WHERE U.tenantId = :tenantId")
        if (keyword != null) {
            jql.append(" AND ((UPPER(U.displayName) LIKE :keyword) OR (UPPER(U.username) LIKE :keyword) OR (UPPER(email) LIKE :keyword))")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND U.id IN :ids")
        }
        if (status != null) {
            jql.append(" AND U.status = :status")
        }
        if (username != null) {
            jql.append(" AND U.username = :username")
        }
        if (roleIds.isNotEmpty()) {
            jql.append(" AND R.id IN :roleIds")
        }
        if (permissions.isNotEmpty()) {
            jql.append(" AND P.name IN :permissions")
        }
        jql.append(" ORDER BY U.displayName")

        val query = em.createQuery(jql.toString(), UserEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword != null) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (roleIds.isNotEmpty()) {
            query.setParameter("roleIds", roleIds)
        }
        if (status != null) {
            query.setParameter("status", status)
        }
        if (username != null) {
            query.setParameter("username", username.lowercase())
        }
        if (permissions.isNotEmpty()) {
            query.setParameter("permissions", permissions)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
