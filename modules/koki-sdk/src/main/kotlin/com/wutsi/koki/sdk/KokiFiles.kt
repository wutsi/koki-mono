package com.wutsi.koki.sdk

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.tenant.TenantProvider
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

class KokiFiles(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
    private val tenantProvider: TenantProvider,
    private val accessTokenHolder: AccessTokenHolder,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/files"
    }

    fun file(id: Long): GetFileResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFileResponse::class.java).body
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun uploadUrl(
        ownerId: Long?,
        ownerType: ObjectType?,
        fileType: ObjectType
    ): String {
        return urlBuilder.build(
            "$PATH_PREFIX/upload", mapOf(
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "file-type" to fileType,
                "tenant-id" to tenantProvider.id(),
                "access-token" to accessTokenHolder.get()
            )
        )
    }

    fun upload(
        ownerId: Long?,
        ownerType: ObjectType?,
        fileType: ObjectType,
        file: MultipartFile
    ): UploadFileResponse {
        val url = uploadUrl(ownerId, ownerType, fileType)
        return upload(url, file, UploadFileResponse::class.java)
    }

    fun files(
        ids: List<Long>,
        ownerId: Long?,
        ownerType: ObjectType?,
        limit: Int,
        offset: Int,
    ): SearchFileResponse {
        val url = urlBuilder.build(
            PATH_PREFIX, mapOf(
                "id" to ids,
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchFileResponse::class.java).body
    }
}
