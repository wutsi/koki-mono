package com.wutsi.koki.form.server.dao

import com.wutsi.koki.form.server.domain.FormDataEntity
import org.springframework.data.repository.CrudRepository

interface FormDataRepository : CrudRepository<FormDataEntity, String>
