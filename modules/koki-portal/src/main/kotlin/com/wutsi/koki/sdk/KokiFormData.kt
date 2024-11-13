package com.wutsi.koki.sdk

import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import org.springframework.web.client.RestTemplate

class KokiFormData(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private val PATH_PREFIX = "/v1/form-data"
    }

    fun submit(formId: String, data: Map<String, Any>): SubmitFormDataResponse {
        val request = SubmitFormDataRequest(
            formId = formId,
            data = data
        )
        val path = PATH_PREFIX
        val url = urlBuilder.build(path)
        return rest.postForEntity(url, request, SubmitFormDataResponse::class.java).body
    }

    fun update(formDataId: String, data: Map<String, Any>) {
        val request = UpdateFormDataRequest(
            data = data
        )
        val url = urlBuilder.build("PATH_PREFIX/$formDataId")
        rest.postForEntity(url, request, Any::class.java)
    }
}
