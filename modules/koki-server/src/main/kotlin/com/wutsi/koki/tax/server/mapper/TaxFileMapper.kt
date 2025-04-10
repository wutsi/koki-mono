package com.wutsi.koki.tax.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.tax.dto.TaxFile
import com.wutsi.koki.tax.dto.TaxFileData
import com.wutsi.koki.tax.server.domain.TaxFileEntity
import org.springframework.stereotype.Service

@Service
class TaxFileMapper(private val objectMapper: ObjectMapper) {
    fun toTaxFile(entity: TaxFileEntity): TaxFile {
        return TaxFile(
            fileId = entity.id!!,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            data = objectMapper.readValue(entity.data, TaxFileData::class.java)
        )
    }
}
