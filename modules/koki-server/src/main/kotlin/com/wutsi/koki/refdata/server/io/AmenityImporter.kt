package com.wutsi.koki.refdata.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.domain.CategoryEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.CategoryService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class AmenityImporter(
    private val categoryService: CategoryService,
    private val service: AmenityService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AmenityImporter::class.java)

        private const val RECORD_ID = 0
        private const val RECORD_CATEGORY = 1
        private const val RECORD_NAME = 2
    }

    fun import(): ImportResponse {
        var added = 0
        var updated = 0
        var errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/refdata/amenity/all.csv"
        val input = AmenityImporter::class.java.getResourceAsStream(filename)
        val categories = categoryService.getByType(CategoryType.AMENITY).toMutableList()
        val amenities = service.all().toMutableList()
        val amenityIds = mutableListOf<Long>()
        val parser = createParser(input)
        var row = 0
        for (record in parser) {
            if (row++ == 0) {
                continue
            }
            val id = record.get(RECORD_ID).toLong()
            var amenity = service.getByIdOrNull(id)
            if (amenity == null) {
                amenity = create(row, record, categories, errors)
                if (amenity != null) {
                    amenities.add(amenity)
                    added++
                }
            } else {
                if (update(row, amenity, record, categories, errors)) {
                    updated++
                }
            }
            amenityIds.add(id)
        }

        /* deactivate */
        amenities.filter { amenity -> !amenityIds.contains(amenity.id) }
            .forEach { amenity -> deactivate(amenity) }

        LOGGER.info("${added + updated} amenity(ies) imported with $errors error(s)")
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
        row: Int,
        record: CSVRecord,
        categories: List<CategoryEntity>,
        errors: MutableList<ImportMessage>,
    ): AmenityEntity? {
        val category = getCategory(record, categories)
        if (category == null) {
            errors.add(
                ImportMessage(
                    location = row.toString(),
                    code = "",
                    message = "Invalid category: " + record.get(RECORD_CATEGORY)
                )
            )
            return null
        }

        return service.save(
            AmenityEntity(
                id = record.get(RECORD_ID).toLong(),
                categoryId = category.id,
                name = record.get(RECORD_NAME),
                active = true,
            )
        )
    }

    private fun update(
        row: Int,
        amenity: AmenityEntity,
        record: CSVRecord,
        categories: List<CategoryEntity>,
        errors: MutableList<ImportMessage>,
    ): Boolean {
        val category = getCategory(record, categories)
        if (category == null) {
            errors.add(
                ImportMessage(
                    location = row.toString(),
                    code = "",
                    message = "Invalid category: " + record.get(RECORD_CATEGORY),
                )
            )
            return false
        }

        amenity.name = record.get(RECORD_NAME)
        amenity.categoryId = category.id
        amenity.name = record.get(RECORD_NAME)
        amenity.active = true
        service.save(amenity)
        return true
    }

    fun getCategory(record: CSVRecord, categories: List<CategoryEntity>): CategoryEntity? {
        val categoryName = record.get(RECORD_CATEGORY)
        return categories.find { cat -> cat.name.equals(categoryName, true) }
    }

    private fun deactivate(amenity: AmenityEntity) {
        amenity.active = false
        service.save(amenity)
    }
}
