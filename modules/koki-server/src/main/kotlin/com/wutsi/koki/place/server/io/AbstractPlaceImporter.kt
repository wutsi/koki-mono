package com.wutsi.koki.place.server.io

import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStream

abstract class AbstractPlaceImporter(
    protected val placeService: PlaceService,
    protected val locationService: LocationService,
) {
    protected fun createParser(input: InputStream): CSVParser {
        return CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .get(),
        )
    }

    protected fun findCity(cityName: String, country: String): LocationEntity? {
        return locationService.search(
            keyword = cityName,
            types = listOf(LocationType.CITY),
            country = country,
            limit = 1
        ).firstOrNull()
    }

    protected fun findNeighbourhood(neighbourhoodName: String, city: LocationEntity): LocationEntity? {
        return locationService.search(
            keyword = neighbourhoodName,
            parentId = city.id,
            types = listOf(LocationType.NEIGHBORHOOD),
            limit = 1
        ).firstOrNull()
    }

    protected fun findPlace(name: String, city: LocationEntity, type: PlaceType): PlaceEntity? {
        return placeService.findPlace(name, city.id ?: -1, type)
    }

    protected fun toStringOrNull(record: CSVRecord, column: Int): String? {
        return try {
            val value = record.get(column)?.trim()
            if (value.isNullOrBlank()) null else value
        } catch (ex: Exception) {
            null
        }
    }

    protected fun toStringList(record: CSVRecord, column: Int): List<String>? {
        return try {
            val value = record.get(column)?.trim()
            if (value.isNullOrBlank()) {
                null
            } else {
                value.split(";")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .takeIf { it.isNotEmpty() }
            }
        } catch (ex: Exception) {
            null
        }
    }

    protected fun toDouble(record: CSVRecord, column: Int): Double? {
        return try {
            val value = record.get(column)?.trim()
            if (value.isNullOrBlank()) null else value.toDouble()
        } catch (ex: Exception) {
            null
        }
    }

    protected fun toBoolean(record: CSVRecord, column: Int): Boolean? {
        return try {
            val value = record.get(column)?.trim()?.lowercase()
            when (value) {
                "true", "1", "yes" -> true
                "false", "0", "no" -> false
                else -> null
            }
        } catch (ex: Exception) {
            null
        }
    }

    protected fun toPlaceType(record: CSVRecord, column: Int): PlaceType {
        val value = record.get(column)?.trim()
        return when (value?.uppercase()) {
            "PARK" -> PlaceType.PARK
            "MUSEUM" -> PlaceType.MUSEUM
            "MARKET" -> PlaceType.MARKET
            "SUPERMARKET" -> PlaceType.SUPERMARKET
            else -> throw IllegalStateException("Unsupported type: $value")
        }
    }
}
