package com.wutsi.koki.sdk

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.tenant.dto.GetTypeResponse
import com.wutsi.koki.tenant.dto.SearchTypeResponse
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

class KokiTypes(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/types"
    }

    fun type(id: Long): GetTypeResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetTypeResponse::class.java).body!!
    }

    fun types(
        ids: List<Long>,
        keyword: String?,
        active: Boolean?,
        objectType: ObjectType?,
        limit: Int,
        offset: Int,
    ): SearchTypeResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "q" to keyword,
                "object-type" to objectType,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchTypeResponse::class.java).body!!
    }

    fun uploadTypes(file: MultipartFile, objectType: ObjectType): ImportResponse {
        val url = urlBuilder.build(
            "$PATH_PREFIX/csv",
            mapOf(
                "object-type" to objectType,
            )
        )
        return upload(url, file, ImportResponse::class.java)
    }
}
