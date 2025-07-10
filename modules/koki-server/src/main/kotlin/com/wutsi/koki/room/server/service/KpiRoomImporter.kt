package com.wutsi.koki.room.server.service


import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class KpiRoomImporter(
    private val persister: KpiRoomPersister,
    private val storageServiceBuilder: StorageServiceBuilder,
    private val logger: KVLogger,
) {
    fun import(date: LocalDate): ImportResponse {
        val file = download(date)
        return import(date, file)
    }

    private fun import(date: LocalDate, file: File): ImportResponse {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(
                    "tenant_id",
                    "product_id",
                    "total_impressions",
                    "total_clicks",
                    "total_views",
                    "total_messages",
                    "total_visitors",
                ).build(),
        )

        // Load kpis
        val errorMessages = mutableListOf<ImportMessage>()
        var row = 0
        var updated = 0
        parser.use {
            for (record in parser) {
                ++row
                try {
                    persister.persist(date, record)
                    ++updated
                } catch (ex: Exception) {
                    errorMessages.add(
                        ImportMessage(
                            location = row.toString(),
                            code = ErrorCode.IMPORT_ERROR,
                            message = ex.message
                        )
                    )
                }
            }
        }

        return ImportResponse(
            added = 0,
            updated = updated,
            errors = errorMessages.size,
            errorMessages = errorMessages
        )
    }

    private fun download(date: LocalDate): File {
        val file = File.createTempFile(UUID.randomUUID().toString(), ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            val storage = storageServiceBuilder.default()
            val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/rooms.csv"
            val url = storage.toURL(path)

            logger.add("url", url)
            storage.get(url, fout)
        }
        return file
    }
}
