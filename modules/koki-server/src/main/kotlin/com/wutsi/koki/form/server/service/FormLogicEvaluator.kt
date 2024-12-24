package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.FormAction
import com.wutsi.koki.form.dto.FormLogic
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.platform.expression.ExpressionEvaluator

class FormExpressionEvaluator(
    private val objectMapper: ObjectMapper,
    private val delegate: ExpressionEvaluator,
) {
    fun evaluate(logic: FormLogic, formData: FormDataEntity) : Boolean {
        return if (logic.expression?.trim().isNullOrEmpty()){
            true
        } else {
            val data = formData.dataAsMap(objectMapper)
            delegate.evaluate(logic.expression!!, data)
        }
    }
}
