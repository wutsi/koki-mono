package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.PasswordResetEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetRepository : CrudRepository<PasswordResetEntity, String>
