package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByEmailAndTypeAndTenantId(email: String, type: UserType, tenantId: Long): UserEntity?
    fun findByUsernameAndTypeAndTenantId(username: String, type: UserType, tenantId: Long): UserEntity?
}
