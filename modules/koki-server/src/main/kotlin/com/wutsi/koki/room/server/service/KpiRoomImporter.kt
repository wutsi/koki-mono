package com.wutsi.koki.room.server.service

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
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
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiRoomImporter::class.java)
    }

    fun import(date: LocalDate): ImportResponse {
        val roomIds = mutableSetOf<Long>()
        val file = download(date)
        val result = import(date, file, roomIds)
        aggregate(roomIds)

        return result
    }

    private fun import(date: LocalDate, file: File, roomIds: MutableSet<Long>): ImportResponse {
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
                ).get(),
        )

        // Load kpis
        val errorMessages = mutableListOf<ImportMessage>()
        var row = 0
        var updated = 0
        parser.use {
            for (record in parser) {
                ++row
                try {
                    val roomId = persister.persist(date, record)
                    roomIds.add(roomId)
                    ++updated
                    LOGGER.info("Imported - Room#$roomId")
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

    private fun aggregate(roomIds: Collection<Long>) {
        roomIds.forEach { roomId ->
            try {
                LOGGER.info("Aggregating - Room#$roomId")
                persister.aggregate(roomId)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to aggregate Room#$roomId", ex)
            }
        }
    }
}
