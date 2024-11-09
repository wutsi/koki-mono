package com.wutsi.koki.form.server.service

import com.wutsi.koki.form.server.dao.FormRepository
import com.wutsi.koki.form.server.domain.FormEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class FormService(private val dao: FormRepository) {
    @Transactional
    fun save(form: FormEntity) {
        form.modifiedAt = Date()
        dao.save(form)
    }
}
