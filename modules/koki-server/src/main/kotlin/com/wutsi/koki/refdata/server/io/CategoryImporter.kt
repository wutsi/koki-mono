package com.wutsi.koki.refdata.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.server.domain.CategoryEntity
import com.wutsi.koki.refdata.server.service.CategoryService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class CategoryImporter(
    private val service: CategoryService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CategoryImporter::class.java)

        private const val RECORD_ID = 0
        private const val RECORD_LONG_NAME = 1
        private const val RECORD_LONG_NAME_FR = 2
    }

    fun import(type: CategoryType): ImportResponse {
        var added = 0
        var updated = 0
        var errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/refdata/category/${type.name.lowercase()}.csv"
        val input = CategoryImporter::class.java.getResourceAsStream(filename)
            ?: throw ConflictException(
                error = Error(
                    code = ErrorCode.CATEGORY_TYPE_NOT_SUPPORTED,
                    message = "Resource not found $filename",
                )
            )

        val categories = service.getByType(type).toMutableList()
        val categoryIds = mutableListOf<Long>()
        val parser = createParser(input)
        for (record in parser) {
            val id = record.get(RECORD_ID).toLong()
            var category = service.getByIdOrNull(id)
            if (category == null) {
                category = create(type, record, categories)
                categories.add(category)
                added++
            } else {
                update(type, category, record, categories)
                updated++
            }
            categoryIds.add(id)
        }

        /* deactivate */
        categories.filter { category -> !categoryIds.contains(category.id) }
            .forEach { category -> deactivate(category) }

        LOGGER.info("${added + updated} category(ies) of type $type imported with ${errors.size} error(s)")
        return ImportResponse(
            added = added,
            updated = updated,
            errors = errors.size,
            errorMessages = errors
        )
    }

    private fun createParser(input: InputStream): CSVParser {
        return CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .get(),
        )
    }

    private fun create(
        type: CategoryType,
        record: CSVRecord,
        categories: List<CategoryEntity>
    ): CategoryEntity {
        val id = record.get(RECORD_ID).toLong()
        val longName = record.get(RECORD_LONG_NAME)
        val longNameFr = record.get(RECORD_LONG_NAME_FR)
        val parentName = extractParentName(longName)
        return service.save(
            CategoryEntity(
                id = id,
                type = type,
                longName = longName,
                name = extractName(longName),
                longNameFr = longNameFr,
                nameFr = extractName(longNameFr),
                active = true,
                level = computeLevel(longName),
                parentId = parentName?.let { name -> findParent(name, categories) }?.id
            )
        )
    }

    private fun update(
        type: CategoryType,
        category: CategoryEntity,
        record: CSVRecord,
        categories: List<CategoryEntity>
    ) {
        val longName = record.get(RECORD_LONG_NAME)
        val longNameFr = record.get(RECORD_LONG_NAME_FR)
        val parentName = extractParentName(longName)

        category.type = type
        category.longName = longName
        category.name = extractName(longName)
        category.longNameFr = longNameFr
        category.nameFr = extractName(longNameFr)
        category.level = computeLevel(longName)
        category.active = true
        category.parentId = parentName?.let { name -> findParent(name, categories) }?.id
        service.save(category)
    }

    private fun extractName(longName: String): String {
        val i = longName.lastIndexOf(">")
        return if (i > 0) {
            longName.substring(i + 1).trim()
        } else {
            longName
        }
    }

    private fun extractParentName(longName: String): String? {
        val i = longName.lastIndexOf(">")
        return if (i > 0) {
            longName.substring(0, i).trim()
        } else {
            null
        }
    }

    private fun computeLevel(longName: String): Int {
        return longName.count { ch -> ch == '>' }
    }

    private fun findParent(longName: String, categories: List<CategoryEntity>): CategoryEntity {
        return categories.find { category -> category.longName == longName }!!
    }

    private fun deactivate(category: CategoryEntity) {
        category.active = false
        service.save(category)
    }
}
