package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByEmailAndTenantId(email: String, tenantId: Long): UserEntity?
    fun findByIdInAndTenantId(id: List<Long>, tenantId: Long): List<UserEntity>
}
