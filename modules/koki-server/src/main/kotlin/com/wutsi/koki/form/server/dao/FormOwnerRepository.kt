package com.wutsi.koki.file.server.dao

import com.wutsi.koki.form.server.domain.FormOwnerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FormOwnerRepository : CrudRepository<FormOwnerEntity, Long> {
    fun findByFormId(formId: Long): List<FormOwnerEntity>
}
