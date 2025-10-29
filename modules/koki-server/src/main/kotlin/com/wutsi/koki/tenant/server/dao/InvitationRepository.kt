package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface InvitationRepository : CrudRepository<InvitationEntity, String> {
    fun findByStatusAndDeletedAndExpiresAtIsLessThan(
        status: InvitationStatus,
        deleted: Boolean,
        expiresAt: Date
    ): List<InvitationEntity>

    fun findByStatusAndDeletedAndEmail(
        status: InvitationStatus,
        deleted: Boolean,
        email: String
    ): List<InvitationEntity>
}
