package com.wutsi.koki.sdk

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.form.dto.CreateFormRequest
import com.wutsi.koki.form.dto.CreateFormResponse
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.form.dto.UpdateFormRequest
import org.springframework.web.client.RestTemplate

class KokiForms(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/forms"
    }

    fun form(id: Long): GetFormResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFormResponse::class.java).body
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun forms(
        ids: List<Long>,
        active: Boolean?,
        ownerId: Long?,
        ownerType: ObjectType?,
        limit: Int,
        offset: Int,
    ): SearchFormResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "active" to active,
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchFormResponse::class.java).body
    }

    fun create(request: CreateFormRequest): CreateFormResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateFormResponse::class.java).body
    }

    fun update(id: Long, request: UpdateFormRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }
}
