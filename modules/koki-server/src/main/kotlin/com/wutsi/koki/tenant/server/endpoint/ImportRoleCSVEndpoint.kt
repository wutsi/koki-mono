package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.tenant.server.io.AttributeCSVImporter
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping
class ImportAttributeCSVEndpoint(
    private val importer: AttributeCSVImporter,
) {
    @PostMapping("/v1/attributes/csv")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam file: MultipartFile
    ): ImportResponse =
        importer.import(tenantId, file.inputStream)
}
