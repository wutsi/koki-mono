package com.wutsi.koki.form.server.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.form.server.service.FormService
import org.springframework.stereotype.Service

@Service
class FormImporter(
    private val service: FormService,
    private val objectMapper: ObjectMapper
) {
    fun import(form: FormEntity, content: FormContent) {

    }
}
