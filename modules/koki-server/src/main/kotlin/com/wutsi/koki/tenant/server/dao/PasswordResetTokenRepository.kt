package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.PasswordResetTokenEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetTokenRepository : CrudRepository<PasswordResetTokenEntity, String>
