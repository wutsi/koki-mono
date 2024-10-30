package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.common.service.TenantIdProvider
import com.wutsi.koki.tenant.server.io.AttributeCSVImporter
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping
class ImportCSVAttributeEndpoint(
    private val importer: AttributeCSVImporter,
    private val tenantIdProvider: TenantIdProvider,
) {
    @PostMapping("/v1/attributes/csv")
    fun get(
        @RequestParam file: MultipartFile
    ): ImportResponse =
        importer.import(tenantIdProvider.get(), file.inputStream)
}
