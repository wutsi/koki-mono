package com.wutsi.koki.tax.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileOwnerService
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.LabelService
import com.wutsi.koki.tax.dto.TaxFileData
import com.wutsi.koki.tax.server.dao.TaxFileRepository
import com.wutsi.koki.tax.server.domain.TaxFileEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TaxFileService(
    private val dao: TaxFileRepository,
    private val fileService: FileService,
    private val fileOwnerService: FileOwnerService,
    private val labelService: LabelService,
    private val objectMapper: ObjectMapper,
) {
    fun get(id: Long, tenantId: Long): TaxFileEntity {
        val file = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.TAX_FILE_NOT_FOUND)) }

        if (file.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.TAX_FILE_NOT_FOUND))
        }
        return file
    }

    @Transactional
    fun save(file: FileEntity, data: TaxFileData): TaxFileEntity {
        // Update file data
        val owner = fileOwnerService.findByFileIdAnAndOwnerType(file.id!!, ObjectType.TAX)
            ?: throw NotFoundException(error = Error(ErrorCode.TAX_NOT_FOUND))

        val opt = dao.findById(file.id!!)
        val fileData = if (opt.isEmpty) {
            dao.save(
                TaxFileEntity(
                    id = file.id,
                    tenantId = file.tenantId,
                    taxId = owner.ownerId,
                    data = objectMapper.writeValueAsString(data)
                )
            )
        } else {
            val tf = opt.get()
            tf.data = objectMapper.writeValueAsString(data)
            tf.modifiedAt = Date()
            dao.save(tf)
        }

        // Update file
        file.language = data.language
        file.description = data.description
        file.numberOfPages = data.numberOfPages

        val codes = data.sections.mapNotNull { section -> section.code }
        file.labels = if (codes.isEmpty()) {
            emptyList()
        } else {
            labelService.findOrCreate(
                names = data.sections.mapNotNull { section -> section.code },
                tenantId = file.tenantId,
            )
        }
        fileService.save(file)

        return fileData
    }
}
