package com.wutsi.koki.offer.server.dao

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.server.domain.OfferEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface OfferRepository : CrudRepository<OfferEntity, Long> {
    fun countByOwnerIdAndOwnerTypeAndTenantId(
        ownerId: Long,
        ownerType: ObjectType,
        tenantId: Long
    ): Long?

    fun countByOwnerIdAndOwnerTypeAndStatusInAndTenantId(
        ownerId: Long,
        ownerType: ObjectType,
        status: List<OfferStatus>,
        tenantId: Long
    ): Long?

    @Query("select O from OfferEntity O where O.status=?1 and O.version.expiresAt<?2")
    fun findByStatusAndNotExpired(status: OfferStatus, expiresAt: Date): List<OfferEntity>
}
