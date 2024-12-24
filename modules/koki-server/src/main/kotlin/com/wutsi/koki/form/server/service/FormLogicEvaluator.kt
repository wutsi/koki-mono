package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.FormAction
import com.wutsi.koki.form.dto.FormLogic
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.platform.expression.ExpressionEvaluator
import org.springframework.stereotype.Service

@Service
class FormLogicEvaluator(
    private val objectMapper: ObjectMapper,
    private val delegate: ExpressionEvaluator,
) {
    fun evaluate(logic: FormLogic, formData: FormDataEntity): FormAction {
        val data = formData.dataAsMap(objectMapper)
        return evaluate(logic, data)
    }

    fun evaluate(logic: FormLogic, data: Map<String, Any>): FormAction {
        if (logic.expression.trim().isEmpty() || delegate.evaluate(logic.expression, data)) {
            return logic.action
        }
        return FormAction.NONE
    }
}
